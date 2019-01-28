package com.wecast.mobile.ui.screen.vod.player;

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
import com.wecast.core.Constants;
import com.wecast.core.analytics.SocketManager;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.entities.VodSourceProfile;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityVodPlayerBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.base.BaseDialog;
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

public class VodPlayerActivity extends BaseActivity<ActivityVodPlayerBinding, VodPlayerActivityViewModel> implements VodPlayerActivityNavigator,
        AbstractPlayer.PlaybackStateListener, WeExoPlayer.WeCastExoPlayerErrorListener, VodPlayerOnTrackChangedListener {

    public static final int PLAY_TRAILER = 0;
    public static final int PLAY_MOVIE = 1;

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    SocketManager socketManager;
    @Inject
    VodPlayerActivityViewModel viewModel;

    private ActivityVodPlayerBinding binding;
    // Exo player
    private Vod vod;
    private int id;
    private int profileId;
    private String profileBusinessModel;
    private long playAction;
    private DebugTextViewHelper debugViewHelper;
    private WeExoPlayer weCastExoPlayer;
    private int aspectRatioType = 1;
    private long bufferTime = 0;
    private int seekTo = -1;

    public static void open(Context context, Vod item, VodSourceProfile profile, long playAction) {
        Intent intent = new Intent(context, VodPlayerActivity.class);
        intent.putExtra("ID", item.getId());
        intent.putExtra("IS_EPISODE", item.getMultiEventVodId() != null);
        intent.putExtra("PLAY_ACTION", playAction);
        intent.putExtra("PROFILE_ID", profile != null ? profile.getId() : null);
        intent.putExtra("BUSINESS_MODEL", profile != null ? profile.getBusinessModel() : null);
        context.startActivity(intent);
    }

    public static void open(Context context, Vod item) {
        Intent intent = new Intent(context, VodPlayerActivity.class);
        intent.putExtra("ID", item.getId());
        intent.putExtra("IS_EPISODE", item.getMultiEventVodId() != null);
        intent.putExtra("PLAY_ACTION", PLAY_MOVIE);
        intent.putExtra("SEEK_TO", item.getContinueWatching() != null ? item.getContinueWatching().getStoppedTime() : null);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_vod_player;
    }

    @Override
    public VodPlayerActivityViewModel getViewModel() {
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
            id = bundle.getInt("ID");
            boolean isEpisode = bundle.getBoolean("IS_EPISODE");
            profileId = bundle.getInt("PROFILE_ID");
            profileBusinessModel = bundle.getString("BUSINESS_MODEL");
            playAction = bundle.getLong("PLAY_ACTION");
            seekTo = bundle.getInt("SEEK_TO");
            // Fetch vod details from server
            Disposable disposable = viewModel.getByID(id, isEpisode)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response != null) {
                            vod = response;
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
        if (vod != null && vod.getTitle() != null) {
            binding.title.setText(vod.getTitle());
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
        if (vod == null) {
            return;
        }

        if (playAction == PLAY_TRAILER) {
            playTrailer(-1);
        } else {
            fetchMovieSource(-1);
        }
        setupTrackOptions();

        // Continue watching
        if (seekTo > -1) {
            weCastExoPlayer.seekToPosition(seekTo);
        }
    }

    private void playTrailer(int maxBitrate) {
        WePlayerParams playerSource = new WePlayerParams.Builder()
                .setUrl(vod.getTrailerSource().getUrl())
                .setDrmUrl(vod.getTrailerSource().getDrmLicenseUrl())
                .setMaxBitrate(maxBitrate)
                .setBuffer(preferenceManager.getVodBuffer())
                .setSubtitles(vod.getTrailerSource().getSubtitles())
                .build();
        weCastExoPlayer.play(playerSource);
    }

    private void playSource(int maxBitrate) {
        WePlayerParams playerSource = new WePlayerParams.Builder()
                .setUrl(vod.getMovieSource().getUrl())
                .setDrmUrl(vod.getMovieSource().getDrmLicenseUrl())
                .setMaxBitrate(maxBitrate)
                .setBuffer(preferenceManager.getVodBuffer())
                .setSubtitles(vod.getMovieSource().getSubtitles())
                .build();
        weCastExoPlayer.play(playerSource);
    }

    private void fetchMovieSource(int maxBitrate) {
        Disposable disposable = viewModel.getSource(vod.getId(), profileId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isTokenExpired()) {
                        refreshToken(() -> fetchMovieSource(maxBitrate));
                    } else if (response.isSuccessful()) {
                        vod = response.getData();
                        playSource(maxBitrate);
                    } else {
                        toast(response.getMessage());
                    }
                }, this::toast);
        subscribe(disposable);
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
        if (playAction == PLAY_TRAILER) {
            playTrailer(maxBitrate);
        } else {
            playSource(maxBitrate);
        }
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
                bufferTime = System.currentTimeMillis();
                break;
            case Player.STATE_ENDED:
                break;
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                viewModel.setLoading(false);
                trackSocketBuffer();
                startDebugViewHelper();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        trackSocketWatchedTime();
        super.onBackPressed();
    }

    @Override
    public void onError(ExoPlaybackException exception) {
        exception.printStackTrace();
        trackSocketError(exception.getLocalizedMessage());

        // Show error dialog
        ScreenRouter.openVodPlayerError(this, this::play);
    }

    /**
     * SOCKET IMPLEMENTATION
     */

    private void trackSocketBuffer() {
        long buffer = (System.currentTimeMillis() - bufferTime);
        if (buffer < Constants.DEFAULT_MIN_BUFFER_TIME) {
            return;
        }
        socketManager.sendPlayIssueData(true, false, (int) (buffer / 1000), SocketManager.RECORD_MODEL_VOD, vod.getId(), 0);
    }

    private void trackSocketWatchedTime() {
        if (weCastExoPlayer == null || weCastExoPlayer.getPlayer() == null) {
            return;
        }

        boolean isTrailer = playAction == PLAY_TRAILER;
        long watchTime = weCastExoPlayer.getPlayer().getCurrentPosition();
        if (!isTrailer & watchTime > Constants.DEFAULT_TRACK_TIME) {
            int duration = (int) weCastExoPlayer.getPlayer().getDuration() / 1000;
            int watched = (int) watchTime / 1000;
            socketManager.sendVodPlayedData(vod, profileBusinessModel, duration, watched);
        }
    }

    private void trackSocketError(String message) {
        socketManager.sendPlayIssueData(false, true, 0, SocketManager.RECORD_MODEL_VOD, vod.getId(), vod.getType());
        socketManager.sendIncidentData(message, VodPlayerActivity.class.getName(), "onPlayerError", null, null);
    }
}
