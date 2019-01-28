package com.wecast.mobile.ui.screen.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract class HomeFragmentProvider {

    @ContributesAndroidInjector(modules = HomeFragmentModule.class)
    abstract HomeFragment provideHomeFramgnetFactory();
}
