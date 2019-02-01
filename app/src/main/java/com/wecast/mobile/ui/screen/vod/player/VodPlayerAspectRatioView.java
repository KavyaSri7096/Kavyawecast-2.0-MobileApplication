package com.wecast.mobile.ui.screen.vod.player;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.wecast.mobile.R;
import com.wecast.player.data.player.exo.WeExoPlayer;

import androidx.appcompat.widget.AppCompatImageButton;

/**
 * Created by ageech@live.com
 */

public class VodPlayerAspectRatioView extends AppCompatImageButton {

    private AspectRatio aspectRatioType = AspectRatio.FIT;

    public VodPlayerAspectRatioView(Context context) {
        super(context);
        initialize(context);
    }

    public VodPlayerAspectRatioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public VodPlayerAspectRatioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        setFocusable(true);
        setClickable(true);
    }

    public void changeAspectRatio(WeExoPlayer weExoPlayer) {
        if (weExoPlayer == null) {
            return;
        }

        // Switch aspect ratio type
        if (aspectRatioType == AspectRatio.FILL) {
            aspectRatioType = AspectRatio.FIT;
        } else {
            aspectRatioType = AspectRatio.FILL;
        }

        // Change button image
        setImageResource(aspectRatioType == AspectRatio.FILL
                ? R.drawable.ic_original_ratio
                : R.drawable.ic_fit_screen);

        // Change exo player resize mode
        weExoPlayer.setAspectRatioResizeMode(aspectRatioType == AspectRatio.FILL
                ? AspectRatioFrameLayout.RESIZE_MODE_FILL
                : AspectRatioFrameLayout.RESIZE_MODE_FIT);
    }

    public enum AspectRatio {
        FILL,
        FIT
    }
}
