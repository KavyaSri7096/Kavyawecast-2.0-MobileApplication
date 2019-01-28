package com.wecast.mobile.ui.screen.live.channel.details.progamme;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract  class ProgrammeFragmentProvider {

    @ContributesAndroidInjector(modules = ProgrammeFragmentModule.class)
    abstract ProgrammeFragment provideProgrammeFragmentFactory();
}
