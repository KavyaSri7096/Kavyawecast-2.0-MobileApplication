package com.wecast.mobile.ui.screen.trending;

import androidx.lifecycle.ViewModelProvider;

import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.factory.ViewModelProviderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class TrendingFragmentModule {

    @Provides
    TrendingFragmentViewModel trendingFragmentViewModel(BannerRepository bannerRepository, ChannelRepository channelRepository, VodRepository vodRepository, TVShowRepository tvShowRepository) {
        return new TrendingFragmentViewModel(bannerRepository, channelRepository, vodRepository, tvShowRepository);
    }

    @Provides
    ViewModelProvider.Factory provideTrendingFragmentViewModel(TrendingFragmentViewModel viewModel) {
        return new ViewModelProviderFactory<>(viewModel);
    }
}
