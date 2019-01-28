package com.wecast.mobile.ui.screen.show.genre;

import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.TVShowRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class TVShowByGenreActivityModule {

    @Provides
    TVShowByGenreActivityViewModel provideTVShowByGenreViewModel(TVShowRepository tvShowRepository, BannerRepository bannerRepository) {
        return new TVShowByGenreActivityViewModel(tvShowRepository, bannerRepository);
    }
}
