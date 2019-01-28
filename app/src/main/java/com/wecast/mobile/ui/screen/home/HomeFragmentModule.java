package com.wecast.mobile.ui.screen.home;

import androidx.lifecycle.ViewModelProvider;

import com.wecast.core.data.api.manager.BannerManager;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.core.data.repository.HighlightedRepository;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.factory.ViewModelProviderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class HomeFragmentModule {

    @Provides
    HomeFragmentViewModel homeFragmentViewModel(HighlightedRepository highlightedRepository, BannerRepository bannerRepository, ChannelRepository channelRepository, VodRepository vodRepository, TVShowRepository tvShowRepository, BannerManager bannerManager) {
        return new HomeFragmentViewModel(highlightedRepository, bannerRepository, channelRepository, vodRepository, tvShowRepository);
    }

    @Provides
    ViewModelProvider.Factory provideHomeFragmentViewModel(HomeFragmentViewModel viewModel) {
        return new ViewModelProviderFactory<>(viewModel);
    }
}
