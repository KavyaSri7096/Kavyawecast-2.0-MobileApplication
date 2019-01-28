package com.wecast.mobile.ui.screen.settings;

import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.data.repository.BannerRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class SettingsActivityModule {

    @Provides
    SettingsActivityViewModel provideNavigationViewModel(PreferenceManager preferenceManager, BannerRepository bannerRepository, AccountManager accountManager) {
        return new SettingsActivityViewModel(preferenceManager, bannerRepository, accountManager);
    }
}
