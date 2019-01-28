package com.wecast.mobile.ui.screen.welcome;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class WelcomeActivityModule {

    @Provides
    WelcomeActivityViewModel provideWelcomeViewModel() {
        return new WelcomeActivityViewModel();
    }
}
