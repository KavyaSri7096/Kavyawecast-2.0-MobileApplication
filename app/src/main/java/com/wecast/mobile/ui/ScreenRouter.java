package com.wecast.mobile.ui;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.Highlighted;
import com.wecast.core.data.db.entities.HighlightedType;
import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.TVShowGenre;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.entities.VodGenre;
import com.wecast.core.data.db.entities.VodSourceProfile;
import com.wecast.core.data.db.entities.VodSourceProfilePricing;
import com.wecast.mobile.R;
import com.wecast.mobile.WeApp;
import com.wecast.mobile.ui.common.dialog.ExitDialog;
import com.wecast.mobile.ui.common.dialog.ParentalPinDialog;
import com.wecast.mobile.ui.screen.live.channel.details.ChannelDetailsActivity;
import com.wecast.mobile.ui.screen.live.channel.details.ChannelDetailsRentDialog;
import com.wecast.mobile.ui.screen.live.channel.details.progamme.details.ProgrammeDetailsActivity;
import com.wecast.mobile.ui.screen.live.channel.search.ChannelSearchActivity;
import com.wecast.mobile.ui.screen.login.LoginActivity;
import com.wecast.mobile.ui.screen.navigation.NavigationActivity;
import com.wecast.mobile.ui.screen.registration.RegistrationActivity;
import com.wecast.mobile.ui.screen.reset.ResetPasswordActivity;
import com.wecast.mobile.ui.screen.settings.SettingsActivity;
import com.wecast.mobile.ui.screen.settings.buffer.BufferActivity;
import com.wecast.mobile.ui.screen.settings.language.LanguageActivity;
import com.wecast.mobile.ui.screen.settings.logout.LogoutDialog;
import com.wecast.mobile.ui.screen.settings.membership.MembershipActivity;
import com.wecast.mobile.ui.screen.settings.profile.EditProfileActivity;
import com.wecast.mobile.ui.screen.settings.quality.VideoQualityActivity;
import com.wecast.mobile.ui.screen.show.details.TVShowDetailsActivity;
import com.wecast.mobile.ui.screen.show.genre.TVShowByGenreActivity;
import com.wecast.mobile.ui.screen.show.player.TVShowPlayerActivity;
import com.wecast.mobile.ui.screen.show.search.TVShowSearchActivity;
import com.wecast.mobile.ui.screen.splash.SplashActivity;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsActivity;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsPlayDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsPricingDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsRateDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsRentDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsRentPreviewDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsStartOverDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsUtils;
import com.wecast.mobile.ui.screen.vod.genre.VodByGenreActivity;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerActivity;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerAudioTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerErrorDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerOnTrackChangedListener;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerTextTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerVideoTrackDialog;
import com.wecast.mobile.ui.screen.vod.search.VodSearchActivity;
import com.wecast.mobile.ui.screen.welcome.WelcomeActivity;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import es.dmoral.toasty.Toasty;

/**
 * Created by ageech@live.com
 */

public class ScreenRouter {

    /**
     * On boarding
     */
    public static void openSplash(Context context) {
        SplashActivity.open(context);
    }

    public static void openWelcome(Context context) {
        WelcomeActivity.open(context);
    }

    public static void openLogin(Context context) {
        LoginActivity.open(context);
    }

    public static void openResetPassword(Context context) {
        ResetPasswordActivity.open(context);
    }

    public static void openRegistration(Context context) {
        RegistrationActivity.open(context);
    }

    public static void openNavigation(Context context) {
        NavigationActivity.open(context);
        ((AppCompatActivity) context).finish();
    }

    /**
     * Settings
     */
    public static void openSettings(Context context) {
        SettingsActivity.open(context);
    }

    public static void openMembership(AppCompatActivity activity) {
        Intent intent = new Intent(activity, MembershipActivity.class);
        activity.startActivityForResult(intent, 100);
    }

    public static void openEditInfo(AppCompatActivity activity) {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        activity.startActivityForResult(intent, 100);
    }

    public static void openLanguage(AppCompatActivity activity) {
        Intent intent = new Intent(activity, LanguageActivity.class);
        activity.startActivityForResult(intent, 101);
    }

    public static void openVideoQuality(AppCompatActivity activity) {
        Intent intent = new Intent(activity, VideoQualityActivity.class);
        activity.startActivityForResult(intent, 102);
    }

    public static void openBuffer(Context context) {
        BufferActivity.open(context);
    }

    public static void openLogout(Context context) {
        LogoutDialog dialog = LogoutDialog.newInstance();
        dialog.show(getFragmentManager(context), LogoutDialog.TAG);
    }

    public static void openExit(Context context) {
        ExitDialog dialog = ExitDialog.newInstance();
        dialog.show(getFragmentManager(context), ExitDialog.TAG);
    }

    /**
     * Highlighted
     */
    public static void openHighlighted(Context context, Highlighted highlighted) {
        if (highlighted.getType() == HighlightedType.CHANNEL) {
            openChannelDetails(context, highlighted.getChannelModel());
        } else if (highlighted.getType() == HighlightedType.MOVIE
                || highlighted.getType() == HighlightedType.EPISODE) {
            openVodDetails(context, highlighted.getMovieModel());
        } else if (highlighted.getType() == HighlightedType.TV_SHOW) {
            openTVShowDetails(context, highlighted.getTVShowModel());
        }
    }

    /**
     * Channel
     */
    public static void openChannelDetails(Context context, Channel channel) {
        if (WeApp.SUBSCRIPTION_EXPIRED) {
            Toasty.warning(context, R.string.error_subscription_expired, Toast.LENGTH_SHORT).show();
        } else {
            if (channel.isNotRented()) {
                ChannelDetailsRentDialog dialog = ChannelDetailsRentDialog.newInstance(channel);
                dialog.show(getFragmentManager(context), ChannelDetailsRentDialog.TAG);
            } else if (channel.isPinProtected()) {
                ParentalPinDialog dialog = ParentalPinDialog.newInstance();
                dialog.setOnPinInputListener(() -> openChannel(context, channel));
                dialog.show(getFragmentManager(context), ParentalPinDialog.TAG);
            } else {
                openChannel(context, channel);
            }
        }
    }

    private static void openChannel(Context context, Channel channel) {
        ChannelDetailsActivity.open(context, channel);
        // If function is triggered from the same context, finish previous one
        if (context instanceof ChannelDetailsActivity) {
            ((ChannelDetailsActivity) context).finish();
        }
    }

    public static void openChannelSearch(Context context) {
        ChannelSearchActivity.open(context);
    }

    public static void openProgrammeDetails(AppCompatActivity activity, Channel channel, TVGuideProgramme programme) {
        ProgrammeDetailsActivity.open(activity, channel, programme);
    }

    /**
     * Vod
     */
    public static void openVodDetails(Context context, Vod vod) {
        if (vod.getParentalRating() != null && vod.getParentalRating().isRequirePin()) {
            ParentalPinDialog dialog = ParentalPinDialog.newInstance();
            dialog.setOnPinInputListener(() -> openVod(context, vod));
            dialog.show(getFragmentManager(context), ParentalPinDialog.TAG);
        } else {
            openVod(context, vod);
        }
    }

    private static void openVod(Context context, Vod vod) {
        VodDetailsActivity.open(context, vod);
        // If function is triggered from the same context, finish previous one
        if (context instanceof VodDetailsActivity) {
            ((VodDetailsActivity) context).finish();
        }
    }

    public static void openVodRentDialog(Context context, Vod vod) {
        VodDetailsRentDialog dialog = VodDetailsRentDialog.newInstance(vod);
        dialog.show(getFragmentManager(context), VodDetailsRentDialog.TAG);
    }


    public static void openVodRentPricingDialog(Context context, Vod vod, VodSourceProfile profile) {
        VodDetailsPricingDialog dialog = VodDetailsPricingDialog.newInstance(vod, profile);
        dialog.show(getFragmentManager(context), VodDetailsPricingDialog.TAG);
    }

    public static void openVodRentPreviewDialog(Context context, Vod vod, VodSourceProfile profile, VodSourceProfilePricing pricing) {
        VodDetailsRentPreviewDialog dialog = VodDetailsRentPreviewDialog.newInstance(vod, profile, pricing);
        dialog.show(getFragmentManager(context), VodDetailsRentPreviewDialog.TAG);
    }

    public static void openVodPlayDialog(Context context, Vod vod) {
        if (WeApp.SUBSCRIPTION_EXPIRED) {
            Toasty.warning(context, R.string.error_subscription_expired, Toast.LENGTH_SHORT).show();
            return;
        }

        VodDetailsPlayDialog dialog = VodDetailsPlayDialog.newInstance(vod);
        dialog.show(getFragmentManager(context), VodDetailsPlayDialog.TAG);

    }

    public static void openVodPlayer(Context context, Vod vod, VodSourceProfile profile, int playType, float seekTo) {
        if (WeApp.SUBSCRIPTION_EXPIRED) {
            Toasty.warning(context, R.string.error_subscription_expired, Toast.LENGTH_SHORT).show();
            return;
        }

        VodPlayerActivity.open(context, vod, profile, playType, seekTo);
    }

    public static void showVodVideoTracks(Context context, VodPlayerOnTrackChangedListener listener) {
        VodPlayerVideoTrackDialog dialog = new VodPlayerVideoTrackDialog();
        dialog.setTrackSelectedListener(listener);
        dialog.show(getFragmentManager(context), VodPlayerVideoTrackDialog.TAG);
    }

    public static void showVodAudioTracks(Context context, VodPlayerOnTrackChangedListener listener) {
        VodPlayerAudioTrackDialog dialog = new VodPlayerAudioTrackDialog();
        dialog.setTrackSelectedListener(listener);
        dialog.show(getFragmentManager(context), VodPlayerAudioTrackDialog.TAG);
    }

    public static void showVodTextTrack(Context context, VodPlayerOnTrackChangedListener listener) {
        VodPlayerTextTrackDialog dialog = new VodPlayerTextTrackDialog();
        dialog.setTrackSelectedListener(listener);
        dialog.show(getFragmentManager(context), VodPlayerTextTrackDialog.TAG);
    }

    public static void showVodPlayerError(Context context, VodPlayerErrorDialog.OnRetryListener listener) {
        VodPlayerErrorDialog dialog = new VodPlayerErrorDialog();
        dialog.setOnRetryListener(listener);
        dialog.show(getFragmentManager(context), VodPlayerErrorDialog.TAG);
    }

    public static void openVodStartOverDialog(Context context, Vod vod, VodSourceProfile vodSourceProfile) {
        VodDetailsStartOverDialog dialog = VodDetailsStartOverDialog.newInstance(vod, vodSourceProfile);
        dialog.show(getFragmentManager(context), VodDetailsStartOverDialog.TAG);
    }

    public static void continuePlaying(Context context, Vod vod) {
        if (WeApp.SUBSCRIPTION_EXPIRED) {
            Toasty.warning(context, R.string.error_subscription_expired, Toast.LENGTH_SHORT).show();
            return;
        }

        List<VodSourceProfile> profiles = VodDetailsUtils.getSourceProfiles(vod, true);
        if (profiles.size() == 1) {
            VodSourceProfile vodSourceProfile = profiles.get(0);
            if (vodSourceProfile.isSubscribed()) {
                if (vod.getContinueWatching() != null) {
                    openVodStartOverDialog(context, vod, vodSourceProfile);
                } else {
                    openVodPlayer(context, vod, vodSourceProfile, VodPlayerActivity.PLAY_MOVIE, 0);
                }
            }
        } else {
            openVodPlayDialog(context, vod);
        }
    }

    public static void openVodRateDialog(Context context, Vod vod) {
        VodDetailsRateDialog dialog = VodDetailsRateDialog.newInstance(vod);
        dialog.show(getFragmentManager(context), VodDetailsRateDialog.TAG);
    }

    public static void openVodByGenre(Context context, VodGenre vodGenre) {
        VodByGenreActivity.open(context, vodGenre);
    }

    public static void openVodSearch(Context context) {
        VodSearchActivity.open(context);
    }

    /**
     * TV Show
     */
    public static void openTVShowDetails(Context context, TVShow tvShow) {
        if (tvShow.getParentalRating() != null && tvShow.getParentalRating().isRequirePin()) {
            ParentalPinDialog dialog = ParentalPinDialog.newInstance();
            dialog.setOnPinInputListener(() -> openTVShow(context, tvShow));
            dialog.show(getFragmentManager(context), ParentalPinDialog.TAG);
        } else {
            openTVShow(context, tvShow);
        }
    }

    private static void openTVShow(Context context, TVShow tvShow) {
        TVShowDetailsActivity.open(context, tvShow);
        // If function is triggered from the same context, finish previous one
        if (context instanceof TVShowDetailsActivity) {
            ((TVShowDetailsActivity) context).finish();
        }
    }

    public static void openTVShowByGenre(Context context, TVShowGenre tvShowGenre) {
        TVShowByGenreActivity.open(context, tvShowGenre);
    }

    public static void openTVShowSearch(Context context) {
        TVShowSearchActivity.open(context);
    }

    public static void openTVShowPlayer(Context context, TVShow tvShow) {
        if (WeApp.SUBSCRIPTION_EXPIRED) {
            Toasty.warning(context, R.string.error_subscription_expired, Toast.LENGTH_SHORT).show();
            return;
        }

        TVShowPlayerActivity.open(context, tvShow);
    }

    /**
     * Get support fragment manager from context
     */
    private static FragmentManager getFragmentManager(Context context) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) context;
        return appCompatActivity.getSupportFragmentManager();
    }
}

