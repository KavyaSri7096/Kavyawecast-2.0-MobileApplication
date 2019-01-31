package com.wecast.mobile.ui.screen.vod.player;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.wecast.mobile.R;
import com.wecast.mobile.databinding.WidgetNextEpisodeBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by ageech@live.com
 */

public class VodPlayerNextEpisodeView extends FrameLayout {

    private Context context;
    private WidgetNextEpisodeBinding binding;
    private OnFinishListener onFinishListener;
    private boolean isCanceled;

    public VodPlayerNextEpisodeView(@NonNull Context context) {
        super(context);
        initialize(context);
    }

    public VodPlayerNextEpisodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public VodPlayerNextEpisodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        this.context = context;

        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = WidgetNextEpisodeBinding.inflate(inflater, this, true);
        setAlpha(0f);
    }

    public void startCounter() {
        // Show view with animation
        animate().alpha(1f).setDuration(500).start();

        // Start countdown timer
        CountDownTimer countDownTimer = new CountDownTimer(5500, 1000) {

            public void onTick(long millisUntilFinished) {
                if (isCanceled) {
                    cancel();
                    onFinish();
                } else {
                    long seconds = millisUntilFinished / 1000;
                    String time = context.getString(R.string.next_episode, seconds);
                    binding.counter.setText(time);
                }
            }

            public void onFinish() {
                if (onFinishListener != null) {
                    onFinishListener.onFinish();
                    setAlpha(0f);
                }
            }
        };
        countDownTimer.start();
    }

    public void forceFinish() {
        isCanceled = true;
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

    public interface OnFinishListener {

        void onFinish();
    }
}
