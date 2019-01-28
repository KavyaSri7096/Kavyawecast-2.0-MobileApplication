package com.wecast.mobile.ui.screen.show.details;

import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.TVShowRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class TVShowDetailsActivityModule {

    @Provides
    TVShowDetailsActivityViewModel provideTVShowDetailsViewModel(TVShowRepository tvShowRepository, BannerRepository bannerRepository) {
        return new TVShowDetailsActivityViewModel(tvShowRepository, bannerRepository);
    }
}
