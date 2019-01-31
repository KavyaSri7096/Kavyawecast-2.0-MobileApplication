package com.wecast.mobile.ui.screen.vod.player;

import com.google.android.exoplayer2.ui.TimeBar;

/**
 * Created by ageech@live.com
 */

public class VodPlayerScrubListener implements TimeBar.OnScrubListener {

    private final OnScrubStopListener onScrubStopListener;

    VodPlayerScrubListener(OnScrubStopListener onScrubStopListener) {
        this.onScrubStopListener = onScrubStopListener;
    }

    @Override
    public void onScrubStart(TimeBar timeBar, long position) {

    }

    @Override
    public void onScrubMove(TimeBar timeBar, long position) {

    }

    @Override
    public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
        onScrubStopListener.onStop(timeBar);
    }

    public interface OnScrubStopListener {

        void onStop(TimeBar timeBar);
    }
}
