package com.wecast.mobile.ui.screen.splash;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.Highlighted;
import com.wecast.core.data.db.entities.TVGuideReminder;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.core.data.repository.HighlightedRepository;
import com.wecast.core.data.repository.ReminderRepository;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class SplashActivityViewModel extends BaseViewModel<SplashActivityNavigator> {

    private final PreferenceManager preferenceManager;
    private final AccountManager accountManager;
    private final ReminderRepository reminderRepository;
    private final HighlightedRepository highlightedRepository;
    private final ChannelRepository channelRepository;
    private final VodRepository vodRepository;
    private final TVShowRepository tvShowRepository;

    SplashActivityViewModel(PreferenceManager preferenceManager, AccountManager accountManager, ReminderRepository reminderRepository, HighlightedRepository highlightedRepository,
                            ChannelRepository channelRepository, VodRepository vodRepository, TVShowRepository tvShowRepository) {
        this.preferenceManager = preferenceManager;
        this.accountManager = accountManager;
        this.reminderRepository = reminderRepository;
        this.highlightedRepository = highlightedRepository;
        this.channelRepository = channelRepository;
        this.vodRepository = vodRepository;
        this.tvShowRepository = tvShowRepository;
    }

    void checkToken() {
        String token = preferenceManager.getAccessToken();

        if (token == null) {
            getNavigator().openLoginActivity();
            return;
        }

        Disposable disposable = accountManager.checkDeviceToken(token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        String newToken = response.getData().getToken();
                        preferenceManager.setAccessToken(newToken);
                        getNavigator().getReminders();
                    } else {
                        loginWithUID();
                    }
                }, throwable -> loginWithUID());
        subscribe(disposable);
    }

    private void loginWithUID() {
        Disposable disposable = accountManager.loginWithUID()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        String newToken = response.getData().getDevice().getToken();
                        preferenceManager.setAccessToken(newToken);
                        getNavigator().getReminders();
                    } else {
                        getNavigator().openLoginActivity();
                    }
                }, throwable -> getNavigator().openLoginActivity());
        subscribe(disposable);
    }

    Observable<ResponseWrapper<List<TVGuideReminder>>> getReminders() {
        return reminderRepository.getAll(true);
    }

    Observable<ResponseWrapper<List<Highlighted>>> getHighlighted() {
        return highlightedRepository.getAll(true);
    }

    Observable<ResponseWrapper<List<Vod>>> getContinueWatching() {
        return vodRepository.getContinueWatching(true);
    }

    Observable<ResponseWrapper<List<Channel>>> getLiveTV() {
        return channelRepository.getAll(true, 1);
    }

    Observable<ResponseWrapper<List<Vod>>> getMovies() {
        return vodRepository.getAll(true, 1);
    }

    Observable<ResponseWrapper<List<TVShow>>> getTVShows() {
        return tvShowRepository.getAll(true, 1);
    }
}
