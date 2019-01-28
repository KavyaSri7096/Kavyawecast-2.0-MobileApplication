package com.wecast.mobile.ui.screen.settings.language;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class LanguageActivityModule {

    @Provides
    LanguageActivityViewModel provideLanguageActivityViewModel() {
        return new LanguageActivityViewModel();
    }
}
