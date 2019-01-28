package com.wecast.mobile.di.module;

import com.wecast.mobile.utils.PermissionUtils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class AppModule {

    @Provides
    PermissionUtils providePermissionUtils() {
        return new PermissionUtils();
    }
}
