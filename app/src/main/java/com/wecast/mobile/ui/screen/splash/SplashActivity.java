package com.wecast.mobile.ui.screen.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.wecast.core.analytics.SocketManager;
import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.dao.ReminderDao;
import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.data.db.entities.TVGuideReminder;
import com.wecast.core.data.repository.ComposerRepository;
import com.wecast.core.logger.Logger;
import com.wecast.core.utils.ReminderUtils;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivitySplashBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashActivityViewModel> implements SplashActivityNavigator {

    @Inject
    SocketManager socketManager;
    @Inject
    ReminderUtils reminderUtils;
    @Inject
    ReminderDao reminderDao;
    @Inject
    ComposerRepository composerRepository;
    @Inject
    SplashActivityViewModel viewModel;

    public static void open(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public SplashActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
    }

    private void setupUI() {
        viewModel.setNavigator(this);
        viewModel.checkToken();
    }

    @Override
    public void getReminders() {
        Disposable disposable = viewModel.getReminders()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            addCalendarEvents(response.data);
                        } else if (response.status == ApiStatus.ERROR) {
                            toast(response.message);
                            openMainActivity();
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(this::getReminders);
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            addCalendarEvents(response.data);
                        }
                    }
                }, throwable -> {
                    toast(throwable);
                    openMainActivity();
                });
        subscribe(disposable);
    }

    private void addCalendarEvents(List<TVGuideReminder> data) {
        if (data == null || data.size() == 0) {
            getHighlighted();
            return;
        }

        reminderDao.clear();
        reminderUtils.createCalendar();
        for (TVGuideReminder reminder : data) {
            TVGuideProgramme programme = new TVGuideProgramme();
            programme.setId(reminder.getEpgProgramme().getId());
            programme.setStringId(reminder.getEpgProgramme().getStringId());
            programme.setTitle(reminder.getEpgProgramme().getTitle());
            programme.setDesc(reminder.getEpgProgramme().getDescription());
            programme.setStart(reminder.getEpgProgramme().getStartTimestamp());
            programme.setStop(reminder.getEpgProgramme().getStopTimestamp());
            reminderDao.insert(reminder);
            // Add event to calendar
            boolean hasReminder = reminderUtils.isEventInCalendar(programme.getStart());
            if (!hasReminder) {
                reminderUtils.createEvent(programme);
            }
        }

        getHighlighted();
    }

    private void getHighlighted() {
        Disposable disposable = viewModel.getHighlighted()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(this::getContinueWatching)
                .subscribe(response -> {

                }, this::toast);
        subscribe(disposable);
    }

    private void getContinueWatching() {
        if (composerRepository.getAppModules().hasVod()) {
            Disposable disposable = viewModel.getContinueWatching()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(this::getLiveTV)
                    .subscribe(response -> {

                    }, this::toast);
            subscribe(disposable);
        } else {
            getLiveTV();
        }
    }

    private void getLiveTV() {
        if (composerRepository.getAppModules().hasChannels()) {
            Disposable disposable = viewModel.getLiveTV()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(this::getMovies)
                    .subscribe(response -> {

                    }, this::toast);
            subscribe(disposable);
        } else {
            getMovies();
        }
    }

    private void getMovies() {
        if (composerRepository.getAppModules().hasVod()) {
            Disposable disposable = viewModel.getMovies()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(this::getTVShows)
                    .subscribe(response -> {

                    }, this::toast);
            subscribe(disposable);
        } else {
            getTVShows();
        }
    }

    private void getTVShows() {
        if (composerRepository.getAppModules().hasVod()) {
            Disposable disposable = viewModel.getTVShows()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(this::openMainActivity)
                    .subscribe(response -> {

                    }, this::toast);
            subscribe(disposable);
        } else {
            openMainActivity();
        }
    }

    @Override
    public void openMainActivity() {
        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(this::openMainScreen, 1000);
    }

    @Override
    public void openLoginActivity() {
        ScreenRouter.openLogin(this);
        finish();
    }

    private void openMainScreen() {
        socketManager.connect();
        ScreenRouter.openNavigation(this);
        finish();
    }
}
