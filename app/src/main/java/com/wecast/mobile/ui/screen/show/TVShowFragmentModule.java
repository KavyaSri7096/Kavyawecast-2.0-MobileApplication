package com.wecast.mobile.ui.screen.show;

import androidx.lifecycle.ViewModelProvider;

import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.TVShowGenreRepository;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.mobile.factory.ViewModelProviderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class TVShowFragmentModule {

    @Provides
    TVShowFragmentViewModel tvShowViewModel(TVShowRepository tvShowRepository, BannerRepository bannerRepository, TVShowGenreRepository tvShowGenreRepository) {
        return new TVShowFragmentViewModel(tvShowRepository, bannerRepository, tvShowGenreRepository);
    }

    @Provides
    ViewModelProvider.Factory provideTVShowFragmentViewModel(TVShowFragmentViewModel viewModel) {
        return new ViewModelProviderFactory<>(viewModel);
    }
}
