package com.wecast.mobile.ui.screen.settings.quality;

import com.wecast.core.data.api.manager.ChannelManager;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.entities.ChannelStreamingProfile;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.ArrayList;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class VideoQualityActivityViewModel extends BaseViewModel<VideoQualityActivityNavigator> {

    private final ChannelManager channelManager;

    public VideoQualityActivityViewModel(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    Observable<ResponseModel<ArrayList<ChannelStreamingProfile>>> getProfiles() {
        return channelManager.getStreamingProfiles();
    }
}
