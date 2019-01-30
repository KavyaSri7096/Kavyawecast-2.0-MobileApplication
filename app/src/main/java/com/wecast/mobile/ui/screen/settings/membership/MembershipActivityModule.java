package com.wecast.mobile.ui.screen.settings.membership;

import com.wecast.core.data.api.manager.AccountManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class MembershipActivityModule {

    @Provides
    MembershipActivityViewModel provideMembershipActivityViewModel(AccountManager accountManager) {
        return new MembershipActivityViewModel(accountManager);
    }
}
