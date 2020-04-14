package com.wecast.mobile.ui.screen.login;

import android.util.Log;

import com.wecast.core.Constants;
import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.entities.Authentication;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.ui.base.BaseViewModel;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class LoginActivityViewModel extends BaseViewModel<LoginActivityNavigator> {

    private AccountManager accountManager;
    private PreferenceManager preferenceManager;

    LoginActivityViewModel(AccountManager accountManager, PreferenceManager preferenceManager) {
        this.accountManager = accountManager;
        this.preferenceManager = preferenceManager;
    }

    void doLogin(String username, String password) {
        Observable<ResponseModel<Authentication>> observable;
        if (Constants.LOGIN_TYPE == Constants.LoginType.WITH_USERNAME_AND_PASSWORD) {
            observable = accountManager.login(username, password);
        } else {
            observable = accountManager.loginWithUID();
        }
        Disposable disposable = observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        saveData(response.getData());
                    } else {
                        getNavigator().onError(response.getMessage());
                    }
                    setLoading(false);
                }, throwable -> {
                    getNavigator().onError(throwable.getLocalizedMessage());
                    setLoading(false);
                });
        subscribe(disposable);
    }

    private void saveData(Authentication authentication) {
        preferenceManager.setAuthentication(authentication);
        preferenceManager.setAccessToken(authentication.getDevice().getToken());
        getNavigator().openSplashActivity();
    }
}
