package com.wecast.mobile.di.builder;

import com.wecast.mobile.ui.common.dialog.ExitDialog;
import com.wecast.mobile.ui.common.dialog.LanguageDialog;
import com.wecast.mobile.ui.common.dialog.ParentalPinDialog;
import com.wecast.mobile.ui.common.dialog.PurchasePinDialog;
import com.wecast.mobile.ui.screen.live.channel.ChannelGenresChildrenDialog;
import com.wecast.mobile.ui.screen.live.channel.ChannelGenresDialog;
import com.wecast.mobile.ui.screen.live.channel.details.ChannelDetailsRentDialog;
import com.wecast.mobile.ui.screen.settings.logout.LogoutDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsPlayDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsPricingDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsRateDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsRentDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsRentPreviewDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerAudioTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerErrorDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerTextTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerVideoTrackDialog;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract class DialogBuilder {

    @ContributesAndroidInjector
    abstract LanguageDialog bindLoginLanguageDialog();

    @ContributesAndroidInjector
    abstract ParentalPinDialog bindParentalPinDialog();

    @ContributesAndroidInjector
    abstract PurchasePinDialog bindPurchasePinDialog();

    @ContributesAndroidInjector
    abstract ChannelGenresDialog bindChannelGenresDialog();

    @ContributesAndroidInjector
    abstract ChannelGenresChildrenDialog bindChannelGenresChildrenDialog();

    @ContributesAndroidInjector
    abstract ChannelDetailsRentDialog bindChannelDetailsRentDialog();

    @ContributesAndroidInjector
    abstract VodDetailsRentDialog bindVodDetailsRentDialog();

    @ContributesAndroidInjector
    abstract VodDetailsPricingDialog bindVodDetailsPricingDialog();

    @ContributesAndroidInjector
    abstract VodDetailsRentPreviewDialog bindVodDetailsRentPreviewDialog();

    @ContributesAndroidInjector
    abstract VodDetailsPlayDialog bindVodDetailsPlayDialog();

    @ContributesAndroidInjector
    abstract VodDetailsRateDialog bindVodRateDialog();

    @ContributesAndroidInjector
    abstract VodPlayerVideoTrackDialog bindVodPlayerVideoTrackDialog();

    @ContributesAndroidInjector
    abstract VodPlayerAudioTrackDialog bindVodPlayerAudioTrackDialog();

    @ContributesAndroidInjector
    abstract VodPlayerTextTrackDialog bindVodPlayerSubtitleTrackDialog();

    @ContributesAndroidInjector
    abstract VodPlayerErrorDialog bindVoDPlayerErrorDialog();

    @ContributesAndroidInjector
    abstract ExitDialog bindExitdialog();

    @ContributesAndroidInjector
    abstract LogoutDialog bindLogoutDialog();
}
