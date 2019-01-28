package com.wecast.mobile.ui.screen.live.channel.details;

import com.wecast.core.data.api.manager.ChannelManager;
import com.wecast.core.data.repository.ChannelRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class ChannelDetailsActivityModule {

    @Provides
    ChannelDetailsActivityViewModel provideChannelDetailsActivityViewModel(ChannelRepository channelRepository, ChannelManager channelManager) {
        return new ChannelDetailsActivityViewModel(channelRepository, channelManager);
    }
}
