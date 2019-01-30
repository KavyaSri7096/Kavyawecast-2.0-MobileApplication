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
import com.wecast.mobile.ui.screen.vod.genre.VodByGenreActivity;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerActivity;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerAudioTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerErrorDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerOnTrackChangedListener;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerTextTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerVideoTrackDialog;
import com.wecast.mobile.ui.screen.vod.search.VodSearchActivity;
import com.wecast.mobile.ui.screen.welcome.WelcomeActivity;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by ageech@live.com
 */

public class ScreenRouter {

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
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), LogoutDialog.TAG);
    }

    public static void openNavigation(Context context) {
        NavigationActivity.open(context);
        ((AppCompatActivity) context).finish();
    }

    public static void openHighlighted(Context context, Highlighted highlighted) {
        if (highlighted.getType() == HighlightedType.CHANNEL) {
            openChannelDetails(context, highlighted.getChannelModel());
        } else if (highlighted.getType() == HighlightedType.MOVIE || highlighted.getType() == HighlightedType.EPISODE) {
            openVodDetails(context, highlighted.getMovieModel());
        } else if (highlighted.getType() == HighlightedType.TV_SHOW) {
            openTVShowDetails(context, highlighted.getTVShowModel());
        }
    }

    public static void openChannelDetails(Context context, Channel channel) {
        if (WeApp.SUBSCRIPTION_EXPIRED) {
            Toast.makeText(context, R.string.error_subscription_expired, Toast.LENGTH_SHORT).show();
        } else {
            if (channel.isNotRented()) {
                ChannelDetailsRentDialog dialog = ChannelDetailsRentDialog.newInstance(channel);
                dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), ChannelDetailsRentDialog.TAG);
            } else {
                ChannelDetailsActivity.open(context, channel);
            }
        }
    }

    public static void openChannelDetails(Context context, int id) {
        ChannelDetailsActivity.open(context, id);
    }

    public static void openChannelSearch(Context context) {
        ChannelSearchActivity.open(context);
    }

    public static void openProgrammeDetails(AppCompatActivity activity, Channel channel, TVGuideProgramme programme) {
        ProgrammeDetailsActivity.open(activity, channel, programme);
    }

    public static void openVodDetails(Context context, Vod vod) {
        if (vod.getParentalRating() != null && vod.getParentalRating().isRequirePin()) {
            ParentalPinDialog dialog = ParentalPinDialog.newInstance();
            dialog.setOnPinInputListener(() -> openVod(context, vod));
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), ParentalPinDialog.TAG);
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
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VodDetailsRentDialog.TAG);
    }


    public static void openVodRentPricingDialog(Context context, Vod vod, VodSourceProfile profile) {
        VodDetailsPricingDialog dialog = VodDetailsPricingDialog.newInstance(vod, profile);
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VodDetailsPricingDialog.TAG);
    }

    public static void openVodRentPreviewDialog(Context context, Vod vod, VodSourceProfile profile, VodSourceProfilePricing pricing) {
        VodDetailsRentPreviewDialog dialog = VodDetailsRentPreviewDialog.newInstance(vod, profile, pricing);
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VodDetailsRentPreviewDialog.TAG);
    }

    public static void openVodPlayDialog(Context context, Vod vod) {
        if (WeApp.SUBSCRIPTION_EXPIRED) {
            Toast.makeText(context, R.string.error_subscription_expired, Toast.LENGTH_SHORT).show();
        } else {
            VodDetailsPlayDialog dialog = VodDetailsPlayDialog.newInstance(vod);
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VodDetailsPlayDialog.TAG);
        }
    }

    public static void openVodPlayer(Context context, Vod vod, VodSourceProfile profile, long playType) {
        if (WeApp.SUBSCRIPTION_EXPIRED) {
            Toast.makeText(context, R.string.error_subscription_expired, Toast.LENGTH_SHORT).show();
        } else {
            VodPlayerActivity.open(context, vod, profile, playType);
        }
    }

    public static void openVodVideoTrackDialog(Context context, VodPlayerOnTrackChangedListener listener) {
        VodPlayerVideoTrackDialog dialog = new VodPlayerVideoTrackDialog();
        dialog.setTrackSelectedListener(listener);
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VodPlayerVideoTrackDialog.TAG);
    }

    public static void openVodAudioTrackDialog(Context context, VodPlayerOnTrackChangedListener listener) {
        VodPlayerAudioTrackDialog dialog = new VodPlayerAudioTrackDialog();
        dialog.setTrackSelectedListener(listener);
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VodPlayerAudioTrackDialog.TAG);
    }

    public static void openVodTextTrackDialog(Context context, VodPlayerOnTrackChangedListener listener) {
        VodPlayerTextTrackDialog dialog = new VodPlayerTextTrackDialog();
        dialog.setTrackSelectedListener(listener);
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VodPlayerTextTrackDialog.TAG);
    }

    public static void openVodPlayerError(Context context, VodPlayerErrorDialog.OnRetryListener listener) {
        VodPlayerErrorDialog dialog = new VodPlayerErrorDialog();
        dialog.setOnRetryListener(listener);
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VodPlayerErrorDialog.TAG);
    }

    public static void continuePlaying(Context context, Vod vod) {
        if (WeApp.SUBSCRIPTION_EXPIRED) {
            Toast.makeText(context, R.string.error_subscription_expired, Toast.LENGTH_SHORT).show();
        } else {
            VodPlayerActivity.open(context, vod);
        }
    }

    public static void openVodRateDialog(Context context, Vod vod) {
        VodDetailsRateDialog dialog = VodDetailsRateDialog.newInstance(vod);
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), VodDetailsRateDialog.TAG);
    }

    public static void openVodByGenre(Context context, VodGenre vodGenre) {
        VodByGenreActivity.open(context, vodGenre);
    }

    public static void openVodSearch(Context context) {
        VodSearchActivity.open(context);
    }

    public static void openTVShowDetails(Context context, TVShow tvShow) {
        if (tvShow.getParentalRating() != null && tvShow.getParentalRating().isRequirePin()) {
            ParentalPinDialog dialog = ParentalPinDialog.newInstance();
            dialog.setOnPinInputListener(() -> openTVShow(context, tvShow));
            dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), ParentalPinDialog.TAG);
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
            Toast.makeText(context, R.string.error_subscription_expired, Toast.LENGTH_SHORT).show();
        } else {
            TVShowPlayerActivity.open(context, tvShow);
        }
    }

    public static void openExit(Context context) {
        ExitDialog dialog = ExitDialog.newInstance();
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), ExitDialog.TAG);
    }
}

