package com.wecast.mobile.ui.screen.settings.quality;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wecast.core.data.db.entities.ChannelStreamingProfile;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityVideoQualityBinding;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.common.adapter.SingleItemChoiceAdapter;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class VideoQualityActivity extends BaseActivity<ActivityVideoQualityBinding, VideoQualityActivityViewModel> implements
        VideoQualityActivityNavigator, SingleItemChoiceAdapter.OnCheckListener<ChannelStreamingProfile> {

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    VideoQualityActivityViewModel viewModel;

    private ActivityVideoQualityBinding binding;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_quality;
    }

    @Override
    public VideoQualityActivityViewModel getViewModel() {
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

        // Setup toolbar title
        binding.toolbar.title.setText(getString(R.string.video_quality_title));

        // Get video quality profiles from server
        getProfiles();
    }

    private void getProfiles() {
        Disposable disposable = viewModel.getProfiles()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isTokenExpired()) {
                        refreshToken(this::getProfiles);
                    } else if (response.isSuccessful()) {
                        showData(response.getData());
                    } else {
                        toast(response.getMessage());
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void showData(List<ChannelStreamingProfile> data) {
        data.add(0, new ChannelStreamingProfile(0, "Auto", -1, -1));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.videoQualities.setLayoutManager(layoutManager);
        VideoQualityAdapter adapter = new VideoQualityAdapter(preferenceManager, data, this);
        binding.videoQualities.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onItemChecked(ChannelStreamingProfile item) {
        preferenceManager.setVideoQuality(item);
        preferenceManager.setMaximumBitrate(item.getMaximumBitrate());
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}

