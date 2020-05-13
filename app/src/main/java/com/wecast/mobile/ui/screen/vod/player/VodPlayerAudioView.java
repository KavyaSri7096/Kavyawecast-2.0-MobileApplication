package com.wecast.mobile.ui.screen.vod.player;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

/**
 * Created by ageech@live.com
 */

public class VodPlayerAudioView extends AppCompatImageButton {

    public VodPlayerAudioView(Context context) {
        super(context);
        initialize(context);
    }

    public VodPlayerAudioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public VodPlayerAudioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setFocusable(true);
        setClickable(true);
    }

    public void openAudioDialog(VodPlayerOnTrackChangedListener listener) {
        listener.openAudioDialogBox();
    }

}
