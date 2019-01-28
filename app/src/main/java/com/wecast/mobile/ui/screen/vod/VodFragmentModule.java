package com.wecast.mobile.ui.screen.vod;

import androidx.lifecycle.ViewModelProvider;

import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.VodGenreRepository;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.factory.ViewModelProviderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class VodFragmentModule {

    @Provides
    VodFragmentViewModel vodViewModel(VodRepository vodRepository, BannerRepository bannerRepository, VodGenreRepository vodGenreRepository) {
        return new VodFragmentViewModel(vodRepository, bannerRepository, vodGenreRepository);
    }

    @Provides
    ViewModelProvider.Factory provideVodFragmentViewModel(VodFragmentViewModel viewModel) {
        return new ViewModelProviderFactory<>(viewModel);
    }
}
