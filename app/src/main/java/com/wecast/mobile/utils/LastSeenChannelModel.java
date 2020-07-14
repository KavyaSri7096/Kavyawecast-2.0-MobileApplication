package com.wecast.mobile.utils;

import com.wecast.core.data.db.entities.Channel;
import com.wecast.player.data.model.WePlayerTrack;

import java.io.Serializable;

public class LastSeenChannelModel implements Serializable{

    private Channel channel;
    private int maxBitrate;
    private WePlayerTrack audioTrack;
    private WePlayerTrack subtitle;

    public LastSeenChannelModel(Channel channel, int maxBitrate){
        this.channel = channel;
        this.maxBitrate = maxBitrate;
    }

    public LastSeenChannelModel(Channel channel){
        this.channel = channel;
        this.maxBitrate = 0;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setMaxBitrate(int maxBitrate) {
        this.maxBitrate = maxBitrate;
    }

    public Channel getChannel() {
        return channel;
    }

    public int getMaxBitrate() {
        return maxBitrate;
    }

    public WePlayerTrack getAudioTrack() {
        return audioTrack;
    }

    public void setAudioTrack(WePlayerTrack audioTrack) {
        this.audioTrack = audioTrack;
    }

    public WePlayerTrack getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(WePlayerTrack subtitle) {
        this.subtitle = subtitle;
    }
}
