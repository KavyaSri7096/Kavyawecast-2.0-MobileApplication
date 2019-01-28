package com.wecast.mobile.ui.screen.settings.profile;

import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.api.model.ErrorData;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.ui.base.BaseViewModel;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class EditProfileActivityViewModel extends BaseViewModel<EditProfileActivityNavigator> {

    private final PreferenceManager preferenceManager;
    private final AccountManager accountManager;

    public EditProfileActivityViewModel(PreferenceManager preferenceManager, AccountManager accountManager) {
        this.preferenceManager = preferenceManager;
        this.accountManager = accountManager;
    }

    public PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    Observable<ResponseModel<ErrorData>> updateInfo(String email, String firstName, String lastName, String currentPassword, String password, String confirmPassword, String purchasePin, String pin) {
        return accountManager.updateInfo(email, firstName, lastName, currentPassword, password, confirmPassword, purchasePin, pin);
    }
}
