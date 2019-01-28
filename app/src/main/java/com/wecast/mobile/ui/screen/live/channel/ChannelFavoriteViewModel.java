package com.wecast.mobile.ui.screen.live.channel;

import com.wecast.core.data.db.entities.Channel;

import androidx.databinding.ObservableField;

/**
 * Created by ageech@live.com
 */

public class ChannelFavoriteViewModel {

    public final ObservableField<String> title;
    public final ObservableField<String> logoUrl;
    public final ObservableField<String> screenShotUrl;
    public final ObservableField<Boolean> isNotRented;

    public final OnClickListener listener;

    private final Channel channel;

    ChannelFavoriteViewModel(Channel channel, OnClickListener listener) {
        this.channel = channel;
        this.listener = listener;

        // Set channel title
        this.title = new ObservableField<>(channel.getTitle());

        // Set channel logo
        this.logoUrl = new ObservableField<>(channel.getLogoUrl());

        // Set channel screenshot
        this.screenShotUrl = new ObservableField<>(channel.getScreenShotUrl());

        // Set channel isNotRented
        this.isNotRented = new ObservableField<>(channel.isNotRented());
    }

    public void onItemClick() {
        listener.onItemClick(channel);
    }

    public interface OnClickListener {

        void onItemClick(Channel item);
    }
}
