package com.wecast.mobile.ui.screen.live.channel.search;

import com.wecast.core.data.api.manager.ChannelManager;
import com.wecast.core.data.repository.BannerRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class ChannelSearchActivityModule {

    @Provides
    ChannelSearchActivityViewModel provideChannelSearchViewModel(ChannelManager channelManager, BannerRepository bannerRepository) {
        return new ChannelSearchActivityViewModel(channelManager, bannerRepository);
    }
}
