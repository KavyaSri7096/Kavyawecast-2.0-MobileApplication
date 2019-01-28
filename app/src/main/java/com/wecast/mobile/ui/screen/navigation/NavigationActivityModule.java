package com.wecast.mobile.ui.screen.navigation;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class NavigationActivityModule {

    @Provides
    NavigationActivityViewModel provideNavigationViewModel() {
        return new NavigationActivityViewModel();
    }
}
