package com.wecast.mobile.ui.screen.live.channel;

import com.wecast.core.data.db.entities.Channel;

import androidx.databinding.ObservableField;

/**
 * Created by ageech@live.com
 */

public class ChannelViewModel {

    public final ObservableField<String> title;
    public final ObservableField<String> logoUrl;
    public final ObservableField<String> screenShotUrl;
    public final ObservableField<Boolean> isNotRented;

    private final Channel channel;

    ChannelViewModel(Channel channel) {
        this.channel = channel;

        // Set channel title
        this.title = new ObservableField<>(channel.getChannelNumber() + " - " + channel.getTitle());

        // Set channel logo
        this.logoUrl = new ObservableField<>(channel.getLogoUrl());

        // Set channel screenshot
        this.screenShotUrl = new ObservableField<>(channel.getScreenShotUrl());

        // Set channel isNotRented
        this.isNotRented = new ObservableField<>(channel.isNotRented());
    }
}
