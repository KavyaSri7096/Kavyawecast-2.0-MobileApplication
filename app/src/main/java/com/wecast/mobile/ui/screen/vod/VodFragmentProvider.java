package com.wecast.mobile.ui.screen.vod;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract class VodFragmentProvider {

    @ContributesAndroidInjector(modules = VodFragmentModule.class)
    abstract VodFragment provideVodFragmentFactory();
}
