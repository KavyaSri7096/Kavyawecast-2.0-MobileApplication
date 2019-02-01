package com.wecast.mobile.ui.screen.vod.player;

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
import com.wecast.mobile.ui.common.dialog.ParentalPinDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsUtils;
import com.wecast.mobile.ui.widget.TimeBarView;
import com.wecast.player.WePlayerFactory;
import com.wecast.player.WePlayerType;
import com.wecast.player.data.model.WePlayerParams;
import com.wecast.player.data.model.WePlayerTrack;
import com.wecast.player.data.player.AbstractPlayer;
import com.wecast.player.data.player.exo.WeExoPlayer;
import com.wecast.player.data.player.exo.trackSelector.ExoPlayerTrackSelector;

import java.util.List;

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
    private int profileId;
    private String profileBusinessModel;
    private int playAction;
    private DebugTextViewHelper debugViewHelper;
    private WeExoPlayer weExoPlayer;
    private long bufferTime = 0;
    private int seekTo = -1;
    // Playlist continuity
    private VodPlayerNextEpisodeView nextEpisodeView;
    private ImageButton nextEpisode;
    private ImageButton previousEpisode;
    private Handler nextEpisodeHandler;
    private Runnable nextEpisodeRunnable = () -> nextEpisodeView.startCounter();
    private List<Vod> episodes;

    public static void open(Context context, Vod item, VodSourceProfile profile, int playAction, int seekTo) {
        Intent intent = new Intent(context, VodPlayerActivity.class);
        intent.putExtra("ID", item.getId());
        intent.putExtra("IS_EPISODE", item.getMultiEventVodId() != 0);
        intent.putExtra("PLAY_ACTION", playAction);
        intent.putExtra("PROFILE_ID", profile != null ? profile.getId() : null);
        intent.putExtra("BUSINESS_MODEL", profile != null ? profile.getBusinessModel() : null);
        intent.putExtra("SEEK_TO", seekTo);
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
            int id = bundle.getInt("ID");
            boolean isEpisode = bundle.getBoolean("IS_EPISODE");
            profileId = bundle.getInt("PROFILE_ID");
            profileBusinessModel = bundle.getString("BUSINESS_MODEL");
            playAction = bundle.getInt("PLAY_ACTION");
            seekTo = bundle.getInt("SEEK_TO");
            getById(id, isEpisode);
        }

        // Find views by id
        nextEpisodeView = findViewById(R.id.nextEpisode);
        nextEpisode = findViewById(R.id.skipNext);
        previousEpisode = findViewById(R.id.skipPrevious);

        // Based on user choice in setting show/hide debug view
        boolean debug = preferenceManager.getDebug();
        binding.debug.setVisibility(debug ? View.VISIBLE : View.GONE);
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

        // Set play next episode listeners
        nextEpisodeView.setOnFinishListener(this::playNextEpisode);
        nextEpisodeView.setOnClickListener(v -> {
            nextEpisodeView.forceFinish();
            stopNextEpisodeRunnable();
        });

        // Listen to scrub change on time bar
        TimeBarView timeBarView = findViewById(R.id.exo_progress);
        if (timeBarView != null) {
            timeBarView.addListener(new VodPlayerScrubListener(timeBar -> startNextEpisodeCounter()));
        }

        // Set next episode button listener
        nextEpisode.setOnClickListener(v -> playNextEpisode());

        // Set previous episode button listener
        previousEpisode.setOnClickListener(v -> playPreviousEpisode());
    }

    private void getById(int id, boolean isEpisode) {
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

    private void setupData() {
        // Set vod title
        TextView title = findViewById(R.id.title);
        if (vod != null && vod.getTitle() != null) {
            title.setText(vod.getTitle());
        } else {
            title.setVisibility(View.GONE);
        }

        // Set vod parental rating
        if (vod.getParentalRating() != null) {
            String code = vod.getParentalRating().getCode();
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

        if (vod.getMultiEventVodId() != 0) {
            // Get episodes from the same season
            int seasonId = vod.getMultiEventVodSeasonId();
            episodes = viewModel.getEpisodes(seasonId);
            // Check if next button should be shown
            int position = getEpisodePosition(vod);
            boolean hasNext = (position < episodes.size() - 1) && playAction == PLAY_MOVIE;
            nextEpisode.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
            // Check if previous button should be shown
            boolean hasPrevious = (position > 0) && playAction == PLAY_MOVIE;
            previousEpisode.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);
        } else {
            nextEpisode.setVisibility(View.INVISIBLE);
            previousEpisode.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * "NEXT EPISODES STARTS IN <TIME>"
     * should be show when video reaches last 5 seconds of playing
     */
    private void startNextEpisodeCounter() {
        if (vod.getMultiEventVodId() == 0 || playAction == PLAY_TRAILER) {
            return;
        }

        // If not other episodes or
        // only one hide next/previous button
        if (episodes == null || episodes.size() <= 1) {
            nextEpisodeView.setVisibility(View.GONE);
            stopNextEpisodeRunnable();
            return;
        }

        // If last episode is playing hide nextEpisode button
        int position = getEpisodePosition(vod);
        if (position == episodes.size() - 1) {
            nextEpisodeView.setVisibility(View.GONE);
            stopNextEpisodeRunnable();
            return;
        }

        // Stop previous delayed action
        stopNextEpisodeRunnable();

        if (weExoPlayer != null && weExoPlayer.getPlayer() != null) {
            long thumbPosition = weExoPlayer.getPlayer().getCurrentPosition();
            long duration = weExoPlayer.getPlayer().getDuration();
            long delay = (duration - thumbPosition) - 5000;
            // If user scrolls to the end of progress bar
            // automatically start next episode if possible
            if ((duration - thumbPosition) >= 5000) {
                // Start new delayed action
                nextEpisodeHandler = new Handler(Looper.myLooper());
                nextEpisodeHandler.postDelayed(nextEpisodeRunnable, delay);
            } else {
                playNextEpisode();
            }
        }
    }

    private void stopNextEpisodeRunnable() {
        if (nextEpisodeHandler != null) {
            nextEpisodeHandler.removeCallbacks(nextEpisodeRunnable);
            nextEpisodeHandler = null;
        }
    }

    private void playNextEpisode() {
        int position = getEpisodePosition(vod);
        if (position < episodes.size() - 1) {
            Vod nextEpisode = episodes.get(position + 1);
            checkParentalAccess(nextEpisode);
        }
    }

    private void playPreviousEpisode() {
        int position = getEpisodePosition(vod);
        if (position > 0) {
            Vod previousEpisode = episodes.get(position - 1);
            checkParentalAccess(previousEpisode);
        }
    }

    /**
     * If next episode is pin protected
     * show parental pin dialog
     */
    private void checkParentalAccess(Vod newEpisode) {
        if (vod.getParentalRating() != null && vod.getParentalRating().isRequirePin()) {
            playEpisode(newEpisode);
            return;
        }

        if (newEpisode.getParentalRating() != null && newEpisode.getParentalRating().isRequirePin()) {
            // Stop current streaming video
            binding.simpleExoView.getPlayer().setPlayWhenReady(false);
            // Open pin input dialog
            ParentalPinDialog dialog = ParentalPinDialog.newInstance();
            dialog.setOnPinInputListener(() -> playEpisode(newEpisode));
            dialog.show(getSupportFragmentManager(), ParentalPinDialog.TAG);
        } else {
            playEpisode(newEpisode);
        }
    }

    private void playEpisode(Vod newEpisode) {
        this.vod = newEpisode;
        checkForSingleProfile();
    }

    /**
     * If vod has only one profile for movie source
     * play that profile without opening play dialog
     */
    private void checkForSingleProfile() {
        List<VodSourceProfile> profiles = VodDetailsUtils.getSourceProfiles(vod, true);
        if (profiles != null) {
            if (profiles.size() == 1) {
                VodSourceProfile vodSourceProfile = profiles.get(0);
                if (vodSourceProfile.isSubscribed()) {
                    weExoPlayer.dispose();
                    profileId = vodSourceProfile.getId();
                    playAction = VodPlayerActivity.PLAY_MOVIE;
                    setupData();
                }
            } else {
                ScreenRouter.openVodPlayDialog(this, vod);
            }
        }
    }

    /**
     * Get episode position in list
     */
    private int getEpisodePosition(Vod vod) {
        if (episodes != null && episodes.size() > 0) {
            for (int i = 0; i < episodes.size(); i++) {
                if (episodes.get(i).getId() == vod.getId()) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * EXO PLAYER IMPLEMENTATION
     */

    private void play() {
        if (vod == null) {
            return;
        }

        switch (playAction) {
            case PLAY_TRAILER:
                playTrailer(-1);
                break;
            case PLAY_MOVIE:
                fetchMovieSource(-1);
                break;
        }

        // Setup options for audio, video and track options
        setupTrackOptions();
    }

    /**
     * If we are playing trailer we do not
     * need to do additional API call to get real url.
     */
    private void playTrailer(int maxBitrate) {
        WePlayerParams playerSource = new WePlayerParams.Builder()
                .setUrl(vod.getTrailerSource().getUrl())
                .setDrmUrl(vod.getTrailerSource().getDrmLicenseUrl())
                .setMaxBitrate(maxBitrate)
                .setBuffer(preferenceManager.getVodBuffer())
                .setSubtitles(vod.getTrailerSource().getSubtitles())
                .build();
        weExoPlayer.play(playerSource);
    }

    /**
     * Get real movie source url from server
     * and then play it in player
     */
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

    private void playSource(int maxBitrate) {
        WePlayerParams playerSource = new WePlayerParams.Builder()
                .setUrl(vod.getMovieSource().getUrl())
                .setDrmUrl(vod.getMovieSource().getDrmLicenseUrl())
                .setMaxBitrate(maxBitrate)
                .setBuffer(preferenceManager.getVodBuffer())
                .setSubtitles(vod.getMovieSource().getSubtitles())
                .build();
        weExoPlayer.play(playerSource);
    }

    /**
     * Show track option buttons visibility with delay of 3 seconds because
     * defaultTrackSelector will need some time to watch current mapped track info
     */
    private void setupTrackOptions() {
        ExoPlayerTrackSelector trackSelector = weExoPlayer.getTrackSelector();
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
        if (playAction == PLAY_TRAILER) {
            playTrailer(maxBitrate);
        } else {
            playSource(maxBitrate);
        }
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

    /**
     * Seek to selected position
     */
    private void seekToPosition() {
        if (seekTo > -1) {
            weExoPlayer.seekToPosition(seekTo);
        }
    }

    @Override
    public void onPlaybackState(int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                viewModel.setLoading(true);
                bufferTime = System.currentTimeMillis();
                seekToPosition();
                break;
            case Player.STATE_ENDED:
                trackSocketWatchedTime();
                stopNextEpisodeRunnable();
                break;
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                viewModel.setLoading(false);
                trackSocketBuffer();
                startNextEpisodeCounter();
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
        ScreenRouter.showVodPlayerError(this, this::play);
    }

    /**
     * SOCKET IMPLEMENTATION
     */

    private void trackSocketBuffer() {
        long bufferMilliseconds = (System.currentTimeMillis() - bufferTime);
        if (bufferMilliseconds < Constants.DEFAULT_MIN_BUFFER_TIME) {
            return;
        }
        int bufferSeconds = (int) bufferMilliseconds / 1000;
        socketManager.sendPlayIssueData(true, false, bufferSeconds, SocketManager.RECORD_MODEL_VOD, vod.getId(), 0);
    }

    private void trackSocketWatchedTime() {
        if (weExoPlayer == null || weExoPlayer.getPlayer() == null) {
            return;
        }

        boolean isTrailer = playAction == PLAY_TRAILER;
        long watchTime = weExoPlayer.getPlayer().getCurrentPosition();
        // Do not send event to socket if we are playing trailer
        // or user did not watch video for minimum 15 seconds
        if (!isTrailer & watchTime > Constants.DEFAULT_TRACK_TIME) {
            int duration = (int) weExoPlayer.getPlayer().getDuration() / 1000;
            int watched = (int) watchTime / 1000;
            socketManager.sendVodPlayedData(vod, profileBusinessModel, duration, watched);
        }
    }

    private void trackSocketError(String message) {
        socketManager.sendPlayIssueData(false, true, 0, SocketManager.RECORD_MODEL_VOD, vod.getId(), vod.getType());
        socketManager.sendIncidentData(message, VodPlayerActivity.class.getName(), "onPlayerError", null, null);
    }
}
