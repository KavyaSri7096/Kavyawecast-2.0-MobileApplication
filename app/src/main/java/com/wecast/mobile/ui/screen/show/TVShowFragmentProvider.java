package com.wecast.mobile.ui.screen.show;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract class TVShowFragmentProvider {

    @ContributesAndroidInjector(modules = TVShowFragmentModule.class)
    abstract TVShowFragment provideTVShowFragmentFactory();
}
