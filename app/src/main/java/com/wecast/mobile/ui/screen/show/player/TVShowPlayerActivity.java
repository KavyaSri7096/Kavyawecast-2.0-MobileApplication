package com.wecast.mobile.ui.screen.show.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityTvShowPlayerBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerAudioTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerOnTrackChangedListener;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerTextTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerVideoTrackDialog;
import com.wecast.player.WePlayerFactory;
import com.wecast.player.WePlayerType;
import com.wecast.player.data.model.WePlayerParams;
import com.wecast.player.data.model.WePlayerTrack;
import com.wecast.player.data.player.AbstractPlayer;
import com.wecast.player.data.player.exo.WeExoPlayer;
import com.wecast.player.data.player.exo.trackSelector.ExoPlayerTrackSelector;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class TVShowPlayerActivity extends BaseActivity<ActivityTvShowPlayerBinding, TVShowPlayerActivityViewModel> implements TVShowPlayerActivityNavigator,
        AbstractPlayer.PlaybackStateListener, WeExoPlayer.WeCastExoPlayerErrorListener, VodPlayerOnTrackChangedListener {

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    TVShowPlayerActivityViewModel viewModel;

    private ActivityTvShowPlayerBinding binding;
    // Exo player
    private TVShow tvShow;
    private DebugTextViewHelper debugViewHelper;
    private WeExoPlayer weCastExoPlayer;
    private int aspectRatioType = 1;

    public static void open(Context context, TVShow item) {
        Intent intent = new Intent(context, TVShowPlayerActivity.class);
        intent.putExtra("ID", item.getId());
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tv_show_player;
    }

    @Override
    public TVShowPlayerActivityViewModel getViewModel() {
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
        goToFullScreen();
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int id = bundle.getInt("ID");
            // Fetch tv show details from server
            Disposable disposable = viewModel.getByID(id)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response != null) {
                            tvShow = response;
                            setupData();
                        }
                    }, this::toast);
            subscribe(disposable);
        }

        binding.debug.setVisibility(preferenceManager.getDebug() ? View.VISIBLE : View.GONE);
    }

    private void setupListeners() {
        binding.back.setOnClickListener(view -> onBackPressed());

        binding.simpleExoView.requestFocus();
        binding.simpleExoView.setControllerShowTimeoutMs(0);
        binding.simpleExoView.setControllerAutoShow(false);
        updateControllerVisibility(View.GONE);
        binding.simpleExoView.setControllerVisibilityListener(v -> {
            if (v == View.GONE) {
                goToFullScreen();
            }
            updateControllerVisibility(v);
        });

        ImageButton aspectRatio = binding.simpleExoView.findViewById(R.id.exo_aspect_ratio);
        aspectRatio.setOnClickListener(v -> {
            if (aspectRatioType == 1) {
                ImageButton image = (ImageButton) v;
                image.setImageResource(R.drawable.ic_original_ratio);
                weCastExoPlayer.setAspectRatioResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                aspectRatioType = 0;
            } else {
                ImageButton image = (ImageButton) v;
                image.setImageResource(R.drawable.ic_fit_screen);
                weCastExoPlayer.setAspectRatioResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                aspectRatioType = 1;
            }
        });
    }

    private void updateControllerVisibility(int visibility) {
        binding.back.setVisibility(visibility);
        binding.info.setVisibility(visibility);
        binding.actions.root.setVisibility(visibility);
    }

    private void setupData() {
        if (tvShow != null && tvShow.getTitle() != null) {
            binding.title.setText(tvShow.getTitle());
        } else {
            binding.title.setVisibility(View.GONE);
        }

        weCastExoPlayer = (WeExoPlayer) WePlayerFactory.get(WePlayerType.EXO_PLAYER, this, binding.simpleExoView);
        if (weCastExoPlayer != null) {
            weCastExoPlayer.setPlaybackStateListener(this);
            weCastExoPlayer.setErrorListener(this);
            weCastExoPlayer.setUseController(true);
            play();
        }
    }

    /**
     * EXO PLAYER IMPLEMENTATION
     */

    private void play() {
        if (tvShow == null) {
            return;
        }

        playTrailer(-1);
        setupTrackOptions();
    }

    private void playTrailer(int maxBitrate) {
        WePlayerParams playerSource = new WePlayerParams.Builder()
                .setUrl(tvShow.getTrailerSource().getUrl())
                .setDrmUrl(tvShow.getTrailerSource().getDrmLicenseUrl())
                .setMaxBitrate(maxBitrate)
                .setBuffer(preferenceManager.getVodBuffer())
                .setSubtitles(tvShow.getTrailerSource().getSubtitles())
                .build();
        weCastExoPlayer.play(playerSource);
    }


    private void setupTrackOptions() {
        ExoPlayerTrackSelector trackSelector = weCastExoPlayer.getTrackSelector();
        // Edit buttons visibility with delay of 3 sec because defaultTrackSelector
        // will need some time to watch current mapped track info
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (trackSelector != null) {
                setupVideoTracks(trackSelector);
                setupAudioTracks(trackSelector);
                setupSubtitleTracks(trackSelector);
            }
        }, 3000);
    }

    private void setupVideoTracks(ExoPlayerTrackSelector selector) {
        if (selector.getSubtitleTracks() != null && selector.getSubtitleTracks().size() > 1) {
            binding.actions.subtitle.setOnClickListener(v -> ScreenRouter.openVodTextTrackDialog(this, this));
            binding.actions.subtitle.setVisibility(View.VISIBLE);
        } else {
            binding.actions.subtitle.setVisibility(View.GONE);
        }
    }

    private void setupAudioTracks(ExoPlayerTrackSelector selector) {
        if (selector.getAudioTracks() != null && !selector.getAudioTracks().isEmpty()) {
            binding.actions.audio.setOnClickListener(v -> ScreenRouter.openVodAudioTrackDialog(this, this));
            binding.actions.audio.setVisibility(View.VISIBLE);
        } else {
            binding.actions.audio.setVisibility(View.GONE);
        }
    }

    private void setupSubtitleTracks(ExoPlayerTrackSelector selector) {
        if (selector.getVideoTracks() != null && !selector.getVideoTracks().isEmpty()) {
            binding.actions.video.setOnClickListener(v -> ScreenRouter.openVodVideoTrackDialog(this, this));
            binding.actions.video.setVisibility(View.VISIBLE);
        } else {
            binding.actions.video.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTrackDialogCreated(BaseDialog dialog) {
        if (dialog instanceof VodPlayerVideoTrackDialog) {
            VodPlayerVideoTrackDialog dialog1 = (VodPlayerVideoTrackDialog) dialog;
            dialog1.setTrackSelector(weCastExoPlayer.getTrackSelector());
        } else if (dialog instanceof VodPlayerAudioTrackDialog) {
            VodPlayerAudioTrackDialog dialog1 = (VodPlayerAudioTrackDialog) dialog;
            dialog1.setTrackSelector(weCastExoPlayer.getTrackSelector());
        } else if (dialog instanceof VodPlayerTextTrackDialog) {
            VodPlayerTextTrackDialog dialog1 = (VodPlayerTextTrackDialog) dialog;
            dialog1.setTrackSelector(weCastExoPlayer.getTrackSelector());
        }
    }

    @Override
    public void onTrackChanged(WePlayerTrack track) {
        weCastExoPlayer.getTrackSelector().changeTrack(track);

        switch (track.getTrackType()) {
            case ExoPlayerTrackSelector.TRACK_TYPE_VIDEO:
                preferenceManager.setLastVideoTrack(track.getName());
                playWithDifferentTrack(track);
                break;
            case ExoPlayerTrackSelector.TRACK_TYPE_AUDIO:
                preferenceManager.setLastAudioTrack(track.getName());
                weCastExoPlayer.getTrackSelector().changeTrack(track);
                break;
            case ExoPlayerTrackSelector.TRACK_TYPE_TEXT:
                preferenceManager.setLastTextTrack(track.getName());
                if (track.isOff()) {
                    weCastExoPlayer.updateSubtitleVisibility(false);
                } else {
                    weCastExoPlayer.getTrackSelector().changeTrack(track);
                    if (!weCastExoPlayer.isSubtitleViewVisible()) {
                        weCastExoPlayer.updateSubtitleVisibility(true);
                    }
                }
                break;
        }
    }

    private void playWithDifferentTrack(WePlayerTrack track) {
        weCastExoPlayer.dispose();

        int maxBitrate = track.getMaxBitrate() * 1000;
        playTrailer(maxBitrate);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (weCastExoPlayer != null) {
            weCastExoPlayer.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (weCastExoPlayer != null) {
            weCastExoPlayer.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (weCastExoPlayer != null) {
            weCastExoPlayer.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (weCastExoPlayer != null) {
            weCastExoPlayer.onDestroy();
            releaseDebugViewHelper();
        }
    }

    private void startDebugViewHelper() {
        debugViewHelper = new DebugTextViewHelper(weCastExoPlayer.getPlayer(), binding.debug);
        debugViewHelper.start();
    }

    private void releaseDebugViewHelper() {
        if (debugViewHelper != null) {
            debugViewHelper.stop();
            debugViewHelper = null;
        }
    }

    @Override
    public void onPlaybackState(int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                viewModel.setLoading(true);
                break;
            case Player.STATE_ENDED:
                break;
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                viewModel.setLoading(false);
                startDebugViewHelper();
                break;
        }
    }

    @Override
    public void onError(ExoPlaybackException exception) {
        exception.printStackTrace();
        // Show error dialog
        ScreenRouter.openVodPlayerError(this, this::play);
    }
}
