package com.wecast.mobile.ui.screen.reset;

import com.wecast.core.data.api.manager.AccountManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class ResetPasswordModule {

    @Provides
    ResetPasswordViewModel provideLoginViewModel(AccountManager accountManager) {
        return new ResetPasswordViewModel(accountManager);
    }
}
