package com.wecast.mobile.ui.screen.login;

import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.db.pref.PreferenceManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class LoginActivityModule {

    @Provides
    LoginActivityViewModel provideLoginViewModel(AccountManager accountManager, PreferenceManager preferenceManager) {
        return new LoginActivityViewModel(accountManager, preferenceManager);
    }
}
