package com.wecast.mobile.ui.screen.show.search;

import com.wecast.core.data.api.manager.TVShowManager;
import com.wecast.core.data.repository.BannerRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class TVShowSearchActivityModule {

    @Provides
    TVShowSearchActivityViewModel provideTVShowSearchViewModel(TVShowManager tvShowManager, BannerRepository bannerRepository) {
        return new TVShowSearchActivityViewModel(tvShowManager, bannerRepository);
    }
}
