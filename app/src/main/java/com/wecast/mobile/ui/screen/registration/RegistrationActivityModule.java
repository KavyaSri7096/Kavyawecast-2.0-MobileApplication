package com.wecast.mobile.ui.screen.registration;

import com.wecast.core.data.api.manager.AccountManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class RegistrationActivityModule {

    @Provides
    RegistrationActivityViewModel provideRegistrationActivityViewModel(AccountManager accountManager) {
        return new RegistrationActivityViewModel(accountManager);
    }
}
