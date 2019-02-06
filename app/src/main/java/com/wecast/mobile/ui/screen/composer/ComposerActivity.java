package com.wecast.mobile.ui.screen.composer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;

import com.wecast.core.data.db.entities.composer.Composer;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityComposerBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.utils.ThemeUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class ComposerActivity extends BaseActivity<ActivityComposerBinding, ComposerActivityViewModel> implements ComposerActivityNavigator {

    @Inject
    ComposerActivityViewModel viewModel;
    @Inject
    PreferenceManager preferenceManager;

    private ActivityComposerBinding binding;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_composer;
    }

    @Override
    public ComposerActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
    }

    private void setupUI() {
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        // Set loading message
        String message = String.format(getString(R.string.welcome_to), getString(R.string.app_name));
        binding.message.setText(message);

        fetchComposer();
    }

    private void fetchComposer() {
        Disposable disposable = viewModel.fetchComposer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        onSuccess(response);
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void onSuccess(Composer composer) {
        // Save selected theme from composer to app prefs
        if (composer.getTheme().equals("marble")) {
            preferenceManager.setTheme(ThemeUtils.THEME_MARBLE);
        } else {
            preferenceManager.setTheme(ThemeUtils.THEME_PRISM);
        }
        // Proceed with app flow
        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(this::openNextScreen, 1000);
    }

    private void openNextScreen() {
        if (preferenceManager.getAccessToken() == null) {
            ScreenRouter.openWelcome(this);
        } else {
            ScreenRouter.openSplash(this);
        }
        finish();
    }
}
