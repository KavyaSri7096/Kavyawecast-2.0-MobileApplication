package com.wecast.mobile.ui.screen.vod.genre;

import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.VodRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class VodByGenreActivityModule {

    @Provides
    VodByGenreActivityViewModel provideVodByGenreViewModel(VodRepository vodRepository, BannerRepository bannerRepository) {
        return new VodByGenreActivityViewModel(vodRepository, bannerRepository);
    }
}
