package com.wecast.mobile.ui.screen.settings.buffer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;

import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityBufferBinding;
import com.wecast.mobile.ui.base.BaseActivity;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class BufferActivity extends BaseActivity<ActivityBufferBinding, BufferActivityViewModel> implements BufferActivityNavigator {

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    BufferActivityViewModel viewModel;

    private ActivityBufferBinding binding;
    private final int offset = 10;


    public static void open(Context context) {
        Intent intent = new Intent(context, BufferActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_buffer;
    }

    @Override
    public BufferActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        setStatusTranslucent(false);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        // Set toolbar title
        binding.toolbar.title.setText(getString(R.string.buffer_title));

        // Set current live tv buffer
        int liveTVBuffer = preferenceManager.getLiveTVBuffer();
        if (liveTVBuffer >= 0) {
            binding.bufferLiveValue.setText(String.format(getString(R.string.buffer_size_live_tv), liveTVBuffer));
            binding.bufferLive.setProgress(liveTVBuffer);
        }

        // Set current movies buffer
        int moviesBuffer = preferenceManager.getVodBuffer();
        if (moviesBuffer >= 0) {
            binding.bufferVodValue.setText(String.format(getString(R.string.buffer_size_movies), moviesBuffer));
            binding.bufferVod.setProgress(moviesBuffer);
        }
    }

    private void setupListeners() {
        binding.bufferLive.setOnSeekBarChangeListener(new BufferChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progress = Math.round((float) progress / offset) * offset;
                binding.bufferLiveValue.setText(String.format(getString(R.string.buffer_size_live_tv), progress));
                preferenceManager.setLiveTVBuffer(progress);
            }
        });
        binding.bufferVod.setOnSeekBarChangeListener(new BufferChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progress = Math.round((float) progress / offset) * offset;
                binding.bufferVodValue.setText(String.format(getString(R.string.buffer_size_movies), progress));
                preferenceManager.setVodBuffer(progress);
            }
        });
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
    }
}
