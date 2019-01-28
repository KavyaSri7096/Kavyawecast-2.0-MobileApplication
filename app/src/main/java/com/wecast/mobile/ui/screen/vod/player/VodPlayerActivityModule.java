package com.wecast.mobile.ui.screen.vod.player;

import com.wecast.core.data.api.manager.VodManager;
import com.wecast.core.data.repository.VodRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class VodPlayerActivityModule {

    @Provides
    VodPlayerActivityViewModel provideVodPlayerViewModel(VodManager vodManager, VodRepository vodRepository) {
        return new VodPlayerActivityViewModel(vodManager, vodRepository);
    }
}
