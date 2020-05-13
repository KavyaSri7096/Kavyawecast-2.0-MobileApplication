package com.wecast.mobile.ui.screen.vod.player;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.wecast.mobile.R;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.screen.show.player.TVShowPlayerActivity;
import com.wecast.player.data.player.exo.WeExoPlayer;

/**
 * Created by ageech@live.com
 */

public class VodPlayerSubtitlesView extends AppCompatImageButton {

    public VodPlayerSubtitlesView(Context context) {
        super(context);
        initialize(context);
    }

    public VodPlayerSubtitlesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public VodPlayerSubtitlesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setFocusable(true);
        setClickable(true);
    }

    public void openSubtitlesDialog(VodPlayerOnTrackChangedListener listner) {
        listner.openSubtitlesDialogBox();
    }

}
