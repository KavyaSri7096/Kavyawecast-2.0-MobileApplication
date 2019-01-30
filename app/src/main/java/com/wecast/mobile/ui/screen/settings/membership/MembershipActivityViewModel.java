package com.wecast.mobile.ui.screen.settings.membership;

import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.api.model.PagedData;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.entities.PaymentHistory;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class MembershipActivityViewModel extends BaseViewModel<MembershipActivityNavigator> {

    private final AccountManager accountManager;

    MembershipActivityViewModel(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    Observable<ResponseModel<PagedData<PaymentHistory>>> getPaymentHistory(int page) {
        return accountManager.getPaymentHistory(page);
    }
}
