package com.wecast.mobile.ui.screen.vod.player;

import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.player.data.model.WePlayerTrack;

/**
 * Created by ageech@live.com
 */

public interface VodPlayerOnTrackChangedListener {

    void onTrackDialogCreated(BaseDialog dialog);

    void onTrackChanged(WePlayerTrack track);
}