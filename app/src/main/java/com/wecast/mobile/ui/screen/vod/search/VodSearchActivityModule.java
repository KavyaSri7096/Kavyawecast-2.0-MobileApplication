package com.wecast.mobile.ui.screen.vod.search;

import com.wecast.core.data.api.manager.VodManager;
import com.wecast.core.data.repository.BannerRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class VodSearchActivityModule {

    @Provides
    VodSearchActivityViewModel provideVodSearchViewModel(VodManager vodManager, BannerRepository bannerRepository) {
        return new VodSearchActivityViewModel(vodManager, bannerRepository);
    }
}
