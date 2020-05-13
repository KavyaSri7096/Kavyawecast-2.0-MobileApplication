package com.wecast.mobile.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.stream.MalformedJsonException;
import com.wecast.core.analytics.SocketManager;
import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.utils.NetworkUtils;
import com.wecast.core.utils.SystemBarUtils;
import com.wecast.mobile.R;
import com.wecast.mobile.WeApp;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerTextTrackDialog;
import com.wecast.mobile.ui.utils.CustomContextWrapper;
import com.wecast.mobile.utils.CommonUtils;
import com.wecast.mobile.utils.ThemeUtils;
import com.wecast.player.data.player.exo.WeExoPlayer;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Created by ageech@live.com
 */

public abstract class BaseActivity<T extends ViewDataBinding, V extends BaseViewModel> extends DaggerAppCompatActivity {

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    SocketManager socketManager;
    @Inject
    AccountManager accountManager;

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private ProgressDialog progressDialog;
    private Snackbar snackBar;
    private T viewDataBinding;
    private V viewModel;

    public abstract int getBindingVariable();

    public abstract
    @LayoutRes
    int getLayoutId();

    public abstract V getViewModel();

    public abstract boolean shouldSetTheme();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        setDefaultLanguage();
        setDefaultTheme();
        super.onCreate(savedInstanceState);
        setNavigationBarColor();

        // Setup data binding and attach view model to view
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        this.viewModel = viewModel == null ? getViewModel() : viewModel;
        viewDataBinding.setVariable(getBindingVariable(), viewModel);
        viewDataBinding.executePendingBindings();
    }

    private void setDefaultLanguage() {
        if (preferenceManager.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            CustomContextWrapper.wrap(this, "fa");
        } else {
            String shortCode = preferenceManager.getLanguage().getShortCode();
            CustomContextWrapper.wrap(this, shortCode);
        }
    }

    private void setDefaultTheme() {
        // Set selected theme before activity is created
        if (shouldSetTheme()) {
            int themeId = preferenceManager.getTheme();
            setTheme(ThemeUtils.getThemeId(themeId));
        }
    }

    private void setNavigationBarColor() {
        SystemBarUtils systemBarUtils = new SystemBarUtils(this);
        systemBarUtils.setStatusBarTintEnabled(true);
        systemBarUtils.setStatusBarAlpha(0);
        systemBarUtils.setNavigationBarTintEnabled(true);
        systemBarUtils.setNavigationBarTintResource(R.color.black);
    }

    public void setThemeMode(Activity activity) {
        if (preferenceManager.getTheme() == ThemeUtils.THEME_MARBLE) {
            setLightMode(activity);
        } else {
            setDarkMode(activity);
        }
    }

    public void setDarkMode(Activity activity) {
        SystemBarUtils systemBarUtils = new SystemBarUtils(this);
        systemBarUtils.setDarkMode(activity);
    }

    public void setLightMode(Activity activity) {
        SystemBarUtils systemBarUtils = new SystemBarUtils(this);
        systemBarUtils.setLightMode(activity);
    }

    protected void setStatusTranslucent(boolean translucent) {
        if (translucent) {
            SystemBarUtils systemBarUtils = new SystemBarUtils(this);
            systemBarUtils.setStatusBarTintEnabled(true);
            systemBarUtils.setStatusBarAlpha(0);
        }
        setThemeMode(this);
    }

    public void setStatusTransparent(Activity activity) {
        setThemeMode(activity);
        Window window = activity.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public T getViewDataBinding() {
        return viewDataBinding;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    @Override
    protected void onDestroy() {
        subscriptions.dispose();
        super.onDestroy();
    }

    public void subscribe(Disposable disposable) {
        subscriptions.add(disposable);
    }

    /**
     * COMMON METHODS
     */

    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    public void refreshToken(Action action) {
        Disposable disposable = accountManager.loginWithUID()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(action)
                .subscribe(response -> {
                    if (response != null) {
                        String token = response.getData().getDevice().getToken();
                        preferenceManager.setAccessToken(token);
                        socketManager.reconnect();
                    } else {
                        openActivityOnTokenExpire();
                    }
                }, throwable -> openActivityOnTokenExpire());
        subscribe(disposable);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public void showLoading() {
        hideLoading();
        progressDialog = CommonUtils.showLoadingDialog(this);
    }

    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    public void openActivityOnTokenExpire() {
        ScreenRouter.openLogin(this);
        finishAffinity();
    }

    /**
     * TOAST MESSAGES
     */

    public void toast(String message) {
        if (message != null && !message.isEmpty()) {
            Toasty.info(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void toast(int message) {
        if (message > -1) {
            Toasty.info(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void toast(Throwable throwable) {
        throwable.printStackTrace();

        if (throwable instanceof SocketTimeoutException) {
            Toasty.error(this, R.string.error_timeout, Toast.LENGTH_SHORT, true).show();
        } else if (throwable instanceof MalformedJsonException) {
            Toasty.error(this, R.string.error_malformed_json, Toast.LENGTH_SHORT, true).show();
        } else if (throwable instanceof IOException) {
            Toasty.error(this, R.string.error_no_internet_connection, Toast.LENGTH_SHORT, true).show();
        } else if (throwable instanceof HttpException) {
            toast(((HttpException) throwable).response().message());
        } else {
            Toasty.error(this, R.string.error_unknown, Toast.LENGTH_SHORT, true).show();
        }
    }

    /**
     * SNACK BAR MESSAGES
     */

    public void snackBar(int message) {
        snackBar(getString(message));
    }

    public void snackBar(String message) {
        if (message.equals(getString(R.string.error_subscription_expired))) {
            WeApp.SUBSCRIPTION_EXPIRED = true;
        }

        if (snackBar == null) {
            Spanned spanned = Html.fromHtml("<font color=\"#ffffff\">" + message + "</font>");
            snackBar = Snackbar.make(findViewById(android.R.id.content), spanned, Snackbar.LENGTH_INDEFINITE);
            snackBar.setAction("OK", v -> {
                snackBar.dismiss();
                snackBar = null;
            });
            snackBar.setActionTextColor(Color.WHITE);
            View snackBarView = snackBar.getView();
            snackBarView.setBackgroundColor(Color.RED);
            snackBar.show();
        }
    }

    public void goToFullScreen() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

}
