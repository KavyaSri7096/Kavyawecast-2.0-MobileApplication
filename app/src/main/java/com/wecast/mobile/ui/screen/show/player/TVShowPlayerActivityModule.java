package com.wecast.mobile.ui.screen.show.player;

import com.wecast.core.data.repository.TVShowRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class TVShowPlayerActivityModule {

    @Provides
    TVShowPlayerActivityViewModel provideVodPlayerViewModel(TVShowRepository tvShowRepository) {
        return new TVShowPlayerActivityViewModel(tvShowRepository);
    }
}
