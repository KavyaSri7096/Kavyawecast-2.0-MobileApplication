package com.wecast.mobile.ui.screen.trending;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract class TrendingFragmentProvider {

    @ContributesAndroidInjector(modules = TrendingFragmentModule.class)
    abstract TrendingFragment provideTrendingFramgnetFactory();
}
