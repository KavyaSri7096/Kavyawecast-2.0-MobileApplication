package com.wecast.mobile.ui.screen.live.guide;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract class TVGuideFragmentProvider {

    @ContributesAndroidInjector(modules = TVGuideFragmentModule.class)
    abstract TVGuideFragment provideTVGuideFragmentFactory();
}
