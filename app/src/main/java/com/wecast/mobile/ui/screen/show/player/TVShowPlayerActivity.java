package com.wecast.mobile.ui.screen.show.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityTvShowPlayerBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerAspectRatioView;
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
    private WeExoPlayer weExoPlayer;

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
            getById(id);
        }

        // Based on user choice in setting show/hide debug view
        boolean debug = preferenceManager.getDebug();
        binding.debug.setVisibility(debug ? View.VISIBLE : View.GONE);

        // Hide next and previous button
        ImageButton nextEpisode = findViewById(R.id.skipNext);
        nextEpisode.setVisibility(View.GONE);
        ImageButton previousEpisode = findViewById(R.id.skipPrevious);
        previousEpisode.setVisibility(View.GONE);
    }

    private void setupListeners() {
        // Set back button listener
        binding.back.setOnClickListener(view -> onBackPressed());

        // Set exo player view controller
        binding.topControls.setVisibility(View.GONE);
        binding.simpleExoView.setControllerVisibilityListener(v -> {
            if (v == View.GONE) {
                goToFullScreen();
            }
            binding.topControls.setVisibility(v);
        });

        // Set aspect ratio
        VodPlayerAspectRatioView aspectRatio = findViewById(R.id.aspectRatio);
        if (aspectRatio != null) {
            aspectRatio.setOnClickListener(v -> aspectRatio.changeAspectRatio(weExoPlayer));
        }
    }

    private void getById(int id) {
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

    private void setupData() {
        // Set vod title
        TextView title = findViewById(R.id.title);
        if (tvShow != null && tvShow.getTitle() != null) {
            title.setText(tvShow.getTitle());
        } else {
            title.setVisibility(View.GONE);
        }

        // Set vod parental rating
        if (tvShow.getParentalRating() != null) {
            String code = tvShow.getParentalRating().getCode();
            binding.parentalRating.setText(code);
        }

        // Initialize exo player
        weExoPlayer = (WeExoPlayer) WePlayerFactory.get(WePlayerType.EXO_PLAYER, this, binding.simpleExoView);
        if (weExoPlayer != null) {
            weExoPlayer.setPlaybackStateListener(this);
            weExoPlayer.setErrorListener(this);
            weExoPlayer.setUseController(true);
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

    /**
     * Since we are playing trailer we do not
     * need to do additional API call to get real url.
     */
    private void playTrailer(int maxBitrate) {
        WePlayerParams playerSource = new WePlayerParams.Builder()
                .setUrl(tvShow.getTrailerSource().getUrl())
                .setDrmUrl(tvShow.getTrailerSource().getDrmLicenseUrl())
                .setMaxBitrate(maxBitrate)
                .setBuffer(preferenceManager.getVodBuffer())
                .setSubtitles(tvShow.getTrailerSource().getSubtitles())
                .build();
        weExoPlayer.play(playerSource);
    }

    /**
     * Show track option buttons visibility with delay of 3 seconds because
     * defaultTrackSelector will need some time to watch current mapped track info
     */
    private void setupTrackOptions() {
        ExoPlayerTrackSelector trackSelector = weExoPlayer.getTrackSelector();
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
        binding.subtitle.setVisibility(View.GONE);
        // Show subtitle options if stream has multiple text tracks
        if (selector.getSubtitleTracks() != null && selector.getSubtitleTracks().size() > 1) {
            binding.subtitle.setOnClickListener(v -> ScreenRouter.showVodTextTrack(this, this));
            binding.subtitle.setVisibility(View.VISIBLE);
        }
    }

    private void setupAudioTracks(ExoPlayerTrackSelector selector) {
        binding.audio.setVisibility(View.GONE);
        // Show audio options if stream has multiple audio tracks
        if (selector.getAudioTracks() != null && !selector.getAudioTracks().isEmpty()) {
            binding.audio.setOnClickListener(v -> ScreenRouter.showVodAudioTracks(this, this));
            binding.audio.setVisibility(View.VISIBLE);
        }
    }

    private void setupSubtitleTracks(ExoPlayerTrackSelector selector) {
        binding.video.setVisibility(View.GONE);
        // Show video options if stream has multiple video tracks
        if (selector.getVideoTracks() != null && !selector.getVideoTracks().isEmpty()) {
            binding.video.setOnClickListener(v -> ScreenRouter.showVodVideoTracks(this, this));
            binding.video.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTrackDialogCreated(BaseDialog dialog) {
        if (dialog instanceof VodPlayerVideoTrackDialog) {
            VodPlayerVideoTrackDialog dialog1 = (VodPlayerVideoTrackDialog) dialog;
            dialog1.setTrackSelector(weExoPlayer.getTrackSelector());
        } else if (dialog instanceof VodPlayerAudioTrackDialog) {
            VodPlayerAudioTrackDialog dialog1 = (VodPlayerAudioTrackDialog) dialog;
            dialog1.setTrackSelector(weExoPlayer.getTrackSelector());
        } else if (dialog instanceof VodPlayerTextTrackDialog) {
            VodPlayerTextTrackDialog dialog1 = (VodPlayerTextTrackDialog) dialog;
            dialog1.setTrackSelector(weExoPlayer.getTrackSelector());
        }
    }

    @Override
    public void onTrackChanged(WePlayerTrack track) {
        weExoPlayer.getTrackSelector().changeTrack(track);

        switch (track.getTrackType()) {
            case ExoPlayerTrackSelector.TRACK_TYPE_VIDEO:
                preferenceManager.setLastVideoTrack(track.getName());
                playWithDifferentTrack(track);
                break;
            case ExoPlayerTrackSelector.TRACK_TYPE_AUDIO:
                preferenceManager.setLastAudioTrack(track.getName());
                weExoPlayer.getTrackSelector().changeTrack(track);
                break;
            case ExoPlayerTrackSelector.TRACK_TYPE_TEXT:
                preferenceManager.setLastTextTrack(track.getName());
                if (track.isOff()) {
                    weExoPlayer.updateSubtitleVisibility(false);
                } else {
                    weExoPlayer.getTrackSelector().changeTrack(track);
                    if (!weExoPlayer.isSubtitleViewVisible()) {
                        weExoPlayer.updateSubtitleVisibility(true);
                    }
                }
                break;
        }
    }

    private void playWithDifferentTrack(WePlayerTrack track) {
        weExoPlayer.dispose();

        int maxBitrate = track.getMaxBitrate() * 1000;
        playTrailer(maxBitrate);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (weExoPlayer != null) {
            weExoPlayer.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (weExoPlayer != null) {
            weExoPlayer.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (weExoPlayer != null) {
            weExoPlayer.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (weExoPlayer != null) {
            weExoPlayer.onDestroy();
            releaseDebugViewHelper();
        }
    }

    /**
     * Since user has option to show debug in settings
     * we have to start or stop default exo player debug view
     */
    private void startDebugViewHelper() {
        debugViewHelper = new DebugTextViewHelper(weExoPlayer.getPlayer(), binding.debug);
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
        ScreenRouter.showVodPlayerError(this, this::play);
    }
}
