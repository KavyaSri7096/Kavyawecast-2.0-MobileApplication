package com.wecast.mobile.ui.screen.settings.quality;

import com.wecast.core.data.api.manager.ChannelManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class VideoQualityActivityModule {

    @Provides
    VideoQualityActivityViewModel provideVideoQualityActivityViewModel(ChannelManager channelManager) {
        return new VideoQualityActivityViewModel(channelManager);
    }
}
