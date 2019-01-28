package com.wecast.mobile.ui.screen.live.channel;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract class ChannelFragmentProvider {

    @ContributesAndroidInjector(modules = ChannelFragmentModule.class)
    abstract ChannelFragment provideChannelFragmentFactory();
}
