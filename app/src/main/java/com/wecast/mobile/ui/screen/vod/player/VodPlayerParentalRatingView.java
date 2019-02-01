package com.wecast.mobile.ui.screen.vod.player;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.wecast.mobile.databinding.WidgetParentalRatingBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ageech@live.com
 */

public class VodPlayerParentalRatingView extends FrameLayout {

    private WidgetParentalRatingBinding binding;

    public VodPlayerParentalRatingView(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public VodPlayerParentalRatingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public VodPlayerParentalRatingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = WidgetParentalRatingBinding.inflate(inflater, this, true);
        setAlpha(0f);
    }

    public void setText(String text) {
        binding.code.setText(text);
        hide();
    }

    public void hide() {
        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(this::startAnimation, 5000);
    }

    private void startAnimation() {
        animate().alpha(0f).setDuration(2000).start();
    }
}
