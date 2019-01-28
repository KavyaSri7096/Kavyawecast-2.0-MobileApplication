package com.wecast.mobile.ui.screen.settings.profile;

import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.db.pref.PreferenceManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class EditProfileActivityModule {

    @Provides
    EditProfileActivityViewModel provideEditProfileActivityViewModel(PreferenceManager preferenceManager, AccountManager accountManager) {
        return new EditProfileActivityViewModel(preferenceManager, accountManager);
    }
}
