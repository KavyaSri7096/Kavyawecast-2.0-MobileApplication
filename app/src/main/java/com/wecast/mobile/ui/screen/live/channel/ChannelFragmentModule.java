package com.wecast.mobile.ui.screen.live.channel;

import androidx.lifecycle.ViewModelProvider;

import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.mobile.factory.ViewModelProviderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class ChannelFragmentModule {

    @Provides
    ChannelFragmentViewModel channelFragmentViewModel(ChannelRepository channelRepository, BannerRepository bannerRepository) {
        return new ChannelFragmentViewModel(channelRepository, bannerRepository);
    }

    @Provides
    ViewModelProvider.Factory provideChannelFragmentViewModel(ChannelFragmentViewModel viewModel) {
        return new ViewModelProviderFactory<>(viewModel);
    }
}
