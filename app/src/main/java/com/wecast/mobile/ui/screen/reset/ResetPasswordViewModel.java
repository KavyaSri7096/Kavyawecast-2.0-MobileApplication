package com.wecast.mobile.ui.screen.reset;

import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.mobile.ui.base.BaseViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class ResetPasswordViewModel extends BaseViewModel<ResetPasswordNavigator> {

    private final AccountManager accountManager;

    public ResetPasswordViewModel(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    void doReset(String email) {
        Disposable disposable = accountManager.resetPassword(email)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        setLoading(false);
                        getNavigator().onSuccess();
                    } else {
                        setLoading(false);
                        getNavigator().onError(response.getMessage());
                    }
                }, throwable -> {
                    setLoading(false);
                    getNavigator().onError(throwable.getLocalizedMessage());
                });
        subscribe(disposable);
    }
}
