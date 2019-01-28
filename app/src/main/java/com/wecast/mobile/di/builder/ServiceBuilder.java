package com.wecast.mobile.di.builder;

import com.wecast.core.service.SyncRemindersService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract class ServiceBuilder {

    @ContributesAndroidInjector
    abstract SyncRemindersService provideSyncRemindersService();
}
