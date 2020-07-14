package com.wecast.mobile.ui.screen.live.channel.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.wecast.core.Constants;
import com.wecast.core.analytics.SocketManager;
import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.dao.ChannelDao;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityChannelDetailsBinding;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.common.dialog.ParentalPinDialog;
import com.wecast.mobile.ui.screen.live.channel.details.progamme.ProgrammeFragment;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerAudioTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerAudioView;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerOnTrackChangedListener;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerSubtitlesTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerSubtitlesView;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerVideoTrackDialog;
import com.wecast.mobile.ui.widget.wecast.WeCastWidget;
import com.wecast.mobile.utils.LocaleUtils;
import com.wecast.player.WePlayerFactory;
import com.wecast.player.WePlayerType;
import com.wecast.player.data.model.WePlayerParams;
import com.wecast.player.data.model.WePlayerTrack;
import com.wecast.player.data.player.AbstractPlayer;
import com.wecast.player.data.player.exo.WeExoPlayer;
import com.wecast.player.data.player.exo.trackSelector.ExoPlayerTrackSelector;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.fragment.app.FragmentManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class ChannelDetailsActivity extends BaseActivity<ActivityChannelDetailsBinding, ChannelDetailsActivityViewModel> implements ChannelDetailsActivityNavigator,
        AbstractPlayer.PlaybackStateListener, WeExoPlayer.WeCastExoPlayerErrorListener, View.OnTouchListener, VodPlayerOnTrackChangedListener {

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    SocketManager socketManager;
    @Inject
    ChannelDao channelDao;
    @Inject
    ChannelDetailsActivityViewModel viewModel;

    private ActivityChannelDetailsBinding binding;
    private int id;
    private Channel channel;
    private List<Channel> channelList;
    private int channelPosition;
    private GestureDetector gestureDetector;
    private WeExoPlayer weExoPlayer;
    private DebugTextViewHelper debugViewHelper;
    private long bufferTime = 0;
    private boolean tryBackupUrl = true;
    private VodPlayerSubtitlesTrackDialog subtitlesDialog;
    private VodPlayerAudioTrackDialog audioDialog;

    public static void open(Context context, Channel channel) {
        Intent intent = new Intent(context, ChannelDetailsActivity.class);
        intent.putExtra("ID", channel.getId());
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_channel_details;
    }

    @Override
    public ChannelDetailsActivityViewModel getViewModel() {
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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt("ID");
            getByID();
        }

        // Based on user choice in setting show/hide debug view
        boolean debug = preferenceManager.getDebug();
        binding.playerView.debug.setVisibility(debug ? View.VISIBLE : View.GONE);

        // Load channels list
        getAll();
    }

    private void setupListeners() {
        // Set back button listeners
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        // Set swipe detector listener
        gestureDetector = new GestureDetector(this, new ChannelDetailsOnSwipeListener() {
            @Override
            public boolean onSwipe(Direction direction) {
                if (direction == Direction.left) goNext();
                if (direction == Direction.right) goBack();
                return true;
            }
        });

        // Set add/remove favorite listeners
        binding.controls.addToFavorites.setOnClickListener(v -> addToFavorites());
        binding.controls.removeFromFavorites.setOnClickListener(v -> removeFromFavorites());
        VodPlayerSubtitlesView subtitlesButton = findViewById(R.id.subtitles_button);
        VodPlayerAudioView audioButton = findViewById(R.id.audio_button);


        if (subtitlesButton != null) {
            subtitlesButton.setOnClickListener(v -> subtitlesButton.openSubtitlesDialog(this));
        }

        if (audioButton != null) {
            audioButton.setOnClickListener(v -> audioButton.openAudioDialog(this));
        }


        // Set up/dow buttons listener
        binding.controls.up.setOnClickListener(view -> goNext());
        binding.controls.down.setOnClickListener(view -> goBack());

        // Set timeshift button listener
        binding.controls.timeShift.setOnClickListener(v -> openTimeshiftDialog());

        // Update controls visibility depending on screen orientation
        binding.playerView.root.setOnClickListener(view -> {
            boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
            boolean controlsHidden = binding.controls.root.getVisibility() == View.GONE;
            binding.controls.root.setVisibility(isLandscape && controlsHidden ? View.VISIBLE : View.GONE);
        });

        // Update controls visibility depending on screen orientation
        binding.playerView.simpleExoView.setControllerVisibilityListener(visibility -> {
            boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
            binding.controls.root.setVisibility(isLandscape ? visibility : View.GONE);
        });

        // Set retry button listener
        binding.playerView.error.retry.setOnClickListener(view -> buildParams());
    }

    private void getByID() {
        Disposable disposable = viewModel.getByID(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        showData(response);
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void showData(Channel channel) {
        this.channel = channel;

        if (channel == null) {
            return;
        }

        // Set toolbar title
        binding.toolbar.title.setText(channel.getTitle());

        // Update channel title and isFavorite in controls layout
        binding.controls.setTitle(channel.getTitle());
        binding.controls.setIsFavorite(channel.isFavorite());

        // Set timeshift
        binding.controls.timeShift.setVisibility(channel.isCatchupEnabled() ? View.VISIBLE : View.GONE);

        // Setup tv guide
        setupProgrammes();

        // Setup configuration
        Configuration config = new Configuration();
        config.orientation = getResources().getConfiguration().orientation;
        onConfigurationChanged(config);

        // Initialize player
        weExoPlayer = (WeExoPlayer) WePlayerFactory.get(WePlayerType.EXO_PLAYER, this, binding.playerView.simpleExoView);
        if (weExoPlayer != null) {
            weExoPlayer.setPlaybackStateListener(this);
            weExoPlayer.setErrorListener(this);
            weExoPlayer.setUseController(false);
            buildParams();
        }
        tryBackupUrl= true;
    }

    private void getAll() {
        Disposable disposable = viewModel.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            addRentedChannelsToList(response.data);
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(this::getAll);
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            addRentedChannelsToList(response.data);
                            snackBar(R.string.error_subscription_expired);
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }

    /**
     * To prevent showing rent dialog while left/right swipe
     * remove all channels that are not rented from list
     */
    private void addRentedChannelsToList(List<Channel> data) {
        channelList = new ArrayList<>();

        if (data != null && data.size() > 0) {
            for (Channel item : data) {
                if (item.isSubscribed()) {
                    channelList.add(item);
                }
            }
            setupUpDownButtons();
        }
    }

    private void addToFavorites() {
        Disposable disposable = viewModel.addToFavorites(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    toast(response.getMessage());
                    if (response.isTokenExpired()) {
                        refreshToken(this::addToFavorites);
                    } else if (response.isSuccessful()) {
                        binding.controls.setIsFavorite(true);
                        // Save data to database
                        channel.setFavorite(true);
                        channelDao.insert(channel);
                        // Refresh widget data
                        WeCastWidget.sendRefreshBroadcast(this);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    toast(throwable.getMessage());
                });
        subscribe(disposable);
    }

    private void removeFromFavorites() {
        Disposable disposable = viewModel.removeFromFavorites(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    toast(response.getMessage());
                    if (response.isTokenExpired()) {
                        refreshToken(this::removeFromFavorites);
                    } else if (response.isSuccessful()) {
                        binding.controls.setIsFavorite(false);
                        // Save data to database
                        channel.setFavorite(false);
                        channelDao.insert(channel);
                        // Refresh widget data
                        WeCastWidget.sendRefreshBroadcast(this);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    toast(throwable.getMessage());
                });
        subscribe(disposable);
    }

    public void goNext() {
        getChannelPosition(channel);
        if (channelPosition < channelList.size() - 1) {
            Channel nextChannel = channelList.get(channelPosition + 1);
            checkParentalAccess(nextChannel);
        }
    }

    public void goBack() {
        getChannelPosition(channel);
        if (channelPosition > 0) {
            Channel previousChannel = channelList.get(channelPosition - 1);
            checkParentalAccess(previousChannel);
        }
    }

    /**
     * Get channel position in list
     */
    private void getChannelPosition(Channel channel) {
        if (channel == null || channelList == null || channelList.size() == 0) {
            return;
        }

        for (int i = 0; i < channelList.size(); i++) {
            if (channelList.get(i).getId() == channel.getId()) {
                channelPosition = i;
                break;
            }
        }
    }

    /**
     * If current channel is pin protected
     * while switching channel if next channel is also pin protected
     * play that channel without asking for pin
     */
    private void checkParentalAccess(Channel channel) {
        if (this.channel.isPinProtected()) {
            playChannel(channel);
            return;
        }

        if (channel.isPinProtected()) {
            ParentalPinDialog dialog = ParentalPinDialog.newInstance();
            dialog.setOnPinInputListener(() -> playChannel(channel));
            dialog.show(getSupportFragmentManager(), ParentalPinDialog.TAG);
        } else {
            playChannel(channel);
        }
    }

    /**
     * Stop playing current channel and play new one
     * also reload programmes for new channels
     */
    private void playChannel(Channel newChannel) {
        channel = newChannel;
        id = channel.getId();
        weExoPlayer.dispose();
        weExoPlayer = null;
        showData(channel);
        setupProgrammes();
        setupUpDownButtons();
    }

    private void setupProgrammes() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, ProgrammeFragment.newInstance(channel))
                .commit();
    }

    /**
     * If current channel is first channel in list hide down button,
     * if current channel is the last channel in list then hide up button
     */
    public void setupUpDownButtons() {
        getChannelPosition(channel);
        if (channelPosition == 0) {
            binding.controls.down.setVisibility(View.GONE);
        } else if (channelPosition == channelList.size() - 1) {
            binding.controls.up.setVisibility(View.GONE);
        } else {
            binding.controls.down.setVisibility(View.VISIBLE);
            binding.controls.up.setVisibility(View.VISIBLE);
        }
    }

    private void openTimeshiftDialog() {
        ChannelDetailsTimeshiftDialog dialog = ChannelDetailsTimeshiftDialog.newInstance(channel);
        dialog.setTimeshiftSelectListener(timeShiftStream -> buildParams(timeShiftStream.getStreamUrl()));
        dialog.show(getSupportFragmentManager(), ChannelDetailsTimeshiftDialog.TAG);
    }

    /**
     * Show/hide player controls based on scree orientation
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLandscapeMode();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setPortraitMode();
        }
    }

    private void setPortraitMode() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int playerHeight = getResources().getDimensionPixelSize(R.dimen.channel_player_height);
        binding.playerView.root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, playerHeight));
        binding.toolbar.root.setVisibility(View.VISIBLE);
        binding.controls.root.setVisibility(View.GONE);
        binding.playerView.root.setOnTouchListener(this);
        setupProgrammes();
    }

    private void setLandscapeMode() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding.playerView.root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        binding.toolbar.root.setVisibility(View.GONE);
        binding.playerView.root.setOnTouchListener(null);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        gestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 104 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bundle bundle = data.getExtras();
                if (bundle.containsKey("OVERRIDE_URL")) {
                    String url = data.getStringExtra("OVERRIDE_URL");
                    buildParams(url);
                } else if (bundle.containsKey("REFRESH_PROGRAMMES")) {
                    boolean shouldRefreshProgrammes = bundle.getBoolean("REFRESH_PROGRAMMES");
                    if (shouldRefreshProgrammes) {
                        setupProgrammes();
                    }
                }
            }
        }
    }

    /**
     * EXO PLAYER IMPLEMENTATION
     */

    private void buildParams() {
        if (channel != null && channel.getPrimaryUrl() != null) {
            WePlayerParams params = new WePlayerParams.Builder()
                    .setUrl(channel.getPrimaryUrl())
                    .setDrmUrl(channel.getPrimaryDrmLicenseUrl())
                    .setBackupUrl(channel.isBackupEnabled() ? channel.getBackupUrl() : null)
                    .setMaxBitrate(-1)
                    .setBuffer(preferenceManager.getLiveTVBuffer())
                    .build();
            play(params);
        }
    }

    private void buildParams(String url) {
        if (url != null && url.length() > 0) {
            WePlayerParams params = new WePlayerParams.Builder()
                    .setUrl(url)
                    .setDrmUrl(channel.getPrimaryDrmLicenseUrl())
                    .setBackupUrl(channel.isBackupEnabled() ? channel.getBackupUrl() : null)
                    .setMaxBitrate(-1)
                    .setBuffer(preferenceManager.getLiveTVBuffer())
                    .build();
            play(params);
        }
    }

    private void play(WePlayerParams params) {
        closeDialogBox();
        releaseDebugViewHelper();
        weExoPlayer.play(params);
        Log.e("M3h", "Channel to play is " + channel.getTitle());
        startDebugViewHelper();
    }

    @Override
    public void onResume() {
        if (weExoPlayer != null) {
            weExoPlayer.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (weExoPlayer != null) {
            weExoPlayer.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (weExoPlayer != null) {
            weExoPlayer.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (weExoPlayer != null) {
            weExoPlayer.onDestroy();
            releaseDebugViewHelper();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        trackSocketWatchedTime();
        super.onBackPressed();
    }

    /**
     * Since user has option to show debug in settings
     * we have to start or stop default exo player debug view
     */
    private void startDebugViewHelper() {
        debugViewHelper = new DebugTextViewHelper(weExoPlayer.getPlayer(), binding.playerView.debug);
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
                binding.playerView.error.root.setVisibility(View.GONE);
                binding.playerView.loader.setVisibility(View.VISIBLE);
                bufferTime = System.currentTimeMillis();
                break;
            case Player.STATE_ENDED:
                trackSocketWatchedTime();
                break;
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                    binding.playerView.error.root.setVisibility(View.GONE);
                    binding.playerView.loader.setVisibility(View.GONE);
                trackSocketBuffer();
                break;
        }
    }

    @Override
    public void onError(ExoPlaybackException exception) {
        exception.printStackTrace();

        // Show playback error message
        if(tryBackupUrl){
            trackSocketError(exception.getLocalizedMessage());
            weExoPlayer.play(channel.getBackupUrl());
            tryBackupUrl = false;
        }else {
            binding.playerView.error.root.setVisibility(View.VISIBLE);
            binding.playerView.loader.setVisibility(View.GONE);
        }
    }

    /**
     * SOCKET IMPLEMENTATION
     */

    private void trackSocketBuffer() {
        long buffer = (System.currentTimeMillis() - bufferTime);
        if (buffer >= Constants.DEFAULT_MIN_BUFFER_TIME) {
            socketManager.sendPlayIssueData(true, false, (int) (buffer / 1000), SocketManager.RECORD_MODEL_CHANNEL, channel.getId(), 0);
        }
    }

    private void trackSocketWatchedTime() {
        if (weExoPlayer == null || weExoPlayer.getPlayer() == null) {
            return;
        }

        long watchTime = weExoPlayer.getPlayer().getCurrentPosition();
        // Do not send event to socket if user did not watch video for minimum 15 seconds
        if (watchTime > Constants.DEFAULT_TRACK_TIME) {
            int watched = (int) watchTime / 1000;
            socketManager.sendChannelPlayedData(channel, watched);
        }
    }

    private void trackSocketError(String message) {
        socketManager.sendPlayIssueData(false, true, 0, SocketManager.RECORD_MODEL_CHANNEL, channel.getId(), 0);
        socketManager.sendIncidentData(message, ChannelDetailsActivity.class.getName(), "onPlayerError", null, null);
    }


    @Override
    public void onTrackDialogCreated(BaseDialog dialog) {
        if (dialog instanceof VodPlayerVideoTrackDialog) {
            VodPlayerVideoTrackDialog dialog1 = (VodPlayerVideoTrackDialog) dialog;
            dialog1.setTrackSelector(weExoPlayer.getTrackSelector());
        } else if (dialog instanceof VodPlayerAudioTrackDialog) {
            VodPlayerAudioTrackDialog dialog1 = (VodPlayerAudioTrackDialog) dialog;
            dialog1.setTrackSelector(weExoPlayer.getTrackSelector());
        } else if (dialog instanceof VodPlayerSubtitlesTrackDialog) {
            VodPlayerSubtitlesTrackDialog dialog1 = (VodPlayerSubtitlesTrackDialog) dialog;
            dialog1.setTrackSelector(weExoPlayer.getTrackSelector());
        }
    }

    @Override
    public void onTrackChanged(WePlayerTrack track) {
        ArrayList<WePlayerTrack> subtitles = weExoPlayer.getTrackSelector().getSubtitleTracks();
        closeDialogBox();

        switch (track.getTrackType()) {
            case ExoPlayerTrackSelector.TRACK_TYPE_VIDEO:
                preferenceManager.setLastVideoTrack(track.getName());
                break;
            case ExoPlayerTrackSelector.TRACK_TYPE_AUDIO:
                preferenceManager.setLastAudioTrack(track.getName());
                weExoPlayer.getTrackSelector().changeTrack(track);
                break;
            case ExoPlayerTrackSelector.TRACK_TYPE_TEXT:
                String savedSubtitleName = LocaleUtils.getInstance().getString("subtitlesPrefLabel");
                Log.e("m3h", "Saved subtitle Channel " + savedSubtitleName);
               /* if (track.isOff()) {
                    weExoPlayer.updateSubtitleVisibility(false);
                } else {*/
//                if (subtitles != null && subtitles.size() > 1) {
//                    for (WePlayerTrack newTrack : subtitles){
                if(track.getName() != null && track.getName().equals(savedSubtitleName != null ? savedSubtitleName : "")){
                    weExoPlayer.getTrackSelector().changeTrack(track);
                    weExoPlayer.updateSubtitleVisibility(true);
//                        }
//                    }
                }
                   /* if (!weExoPlayer.isSubtitleViewVisible()) {
                        weExoPlayer.updateSubtitleVisibility(true);
                    }
                }*/
                break;
        }
    }

    public void closeDialogBox(){
        closeAudioDialog();
        closeSubtitlesDialog();
    }

    private void closeSubtitlesDialog(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (subtitlesDialog != null) {
                subtitlesDialog.dismiss();
            }
        }, 1000);
    }

    private void closeAudioDialog(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (audioDialog != null) {
                audioDialog.dismiss();
            }
        }, 1000);
    }


    @Override
    public void openSubtitlesDialogBox() {
        if(audioDialog != null){
            audioDialog.dismiss();
            audioDialog = null;
        }

        if(subtitlesDialog!= null) {
            subtitlesDialog.dismiss();
            subtitlesDialog = null;
        }else{

            subtitlesDialog = new VodPlayerSubtitlesTrackDialog();
            subtitlesDialog.setTrackSelectedListener(this);
            subtitlesDialog.show(getSupportFragmentManager(), VodPlayerSubtitlesTrackDialog.TAG);
        }

    }

    @Override
    public void openAudioDialogBox() {
        if(subtitlesDialog != null){
            subtitlesDialog.dismiss();
            subtitlesDialog = null;
        }

        if(audioDialog!= null){
            audioDialog.dismiss();
            audioDialog = null;
        }else{
            audioDialog = new VodPlayerAudioTrackDialog();
            audioDialog.setTrackSelectedListener(this);
            audioDialog.show(getSupportFragmentManager(), VodPlayerAudioTrackDialog.TAG);

        }
    }
}
