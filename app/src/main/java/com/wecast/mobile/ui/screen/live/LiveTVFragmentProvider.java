package com.wecast.mobile.ui.screen.live;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract class LiveTVFragmentProvider {

    @ContributesAndroidInjector(modules = LiveTVFragmentModule.class)
    abstract LiveTVFragment provideLiveTVFragmentFactory();
}
