package com.wecast.mobile.ui.screen.registration;

import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.api.model.ErrorData;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.entities.Subscription;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class RegistrationActivityViewModel extends BaseViewModel<RegistrationActivityNavigator> {

    private final AccountManager accountManager;

    public RegistrationActivityViewModel(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    Observable<ResponseModel<List<Subscription>>> getSubscriptions() {
        return accountManager.getSubscriptions();
    }

    Observable<ResponseModel<ErrorData>> register(String username, String firstName, String lastName, String email, String password, String age, String pin, String purchasePin, int gender, String subscriptionId) {
        return accountManager.register(username, firstName, lastName, email, password, age, pin, purchasePin, gender, subscriptionId);
    }
}
