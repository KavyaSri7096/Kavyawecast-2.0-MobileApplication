package com.wecast.mobile.ui.screen.vod.details;

import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.VodRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class VodDetailsActivityModule {

    @Provides
    VodDetailsActivityViewModel provideVodDetailsViewModel(VodRepository vodRepository, BannerRepository bannerRepository) {
        return new VodDetailsActivityViewModel(vodRepository, bannerRepository);
    }
}
