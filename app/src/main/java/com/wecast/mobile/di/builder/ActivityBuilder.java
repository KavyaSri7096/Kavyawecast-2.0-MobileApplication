package com.wecast.mobile.di.builder;

import com.wecast.mobile.ui.common.dialog.ExitDialog;
import com.wecast.mobile.ui.common.dialog.LanguageDialog;
import com.wecast.mobile.ui.screen.composer.ComposerActivity;
import com.wecast.mobile.ui.screen.composer.ComposerActivityModule;
import com.wecast.mobile.ui.screen.gallery.GalleryActivity;
import com.wecast.mobile.ui.screen.gallery.GalleryActivityModule;
import com.wecast.mobile.ui.screen.home.HomeFragmentProvider;
import com.wecast.mobile.ui.screen.live.LiveTVFragmentProvider;
import com.wecast.mobile.ui.screen.live.channel.ChannelFragmentProvider;
import com.wecast.mobile.ui.screen.live.channel.details.ChannelDetailsActivity;
import com.wecast.mobile.ui.screen.live.channel.details.ChannelDetailsActivityModule;
import com.wecast.mobile.ui.screen.live.channel.details.progamme.ProgrammeFragmentProvider;
import com.wecast.mobile.ui.screen.live.channel.details.progamme.details.ProgrammeDetailsActivity;
import com.wecast.mobile.ui.screen.live.channel.details.progamme.details.ProgrammeDetailsActivityModule;
import com.wecast.mobile.ui.screen.live.channel.search.ChannelSearchActivity;
import com.wecast.mobile.ui.screen.live.channel.search.ChannelSearchActivityModule;
import com.wecast.mobile.ui.screen.live.guide.TVGuideFragmentProvider;
import com.wecast.mobile.ui.screen.login.LoginActivity;
import com.wecast.mobile.ui.screen.login.LoginActivityModule;
import com.wecast.mobile.ui.screen.navigation.NavigationActivity;
import com.wecast.mobile.ui.screen.navigation.NavigationActivityModule;
import com.wecast.mobile.ui.screen.registration.RegistrationActivity;
import com.wecast.mobile.ui.screen.registration.RegistrationActivityModule;
import com.wecast.mobile.ui.screen.reset.ResetPasswordActivity;
import com.wecast.mobile.ui.screen.reset.ResetPasswordModule;
import com.wecast.mobile.ui.screen.settings.SettingsActivity;
import com.wecast.mobile.ui.screen.settings.SettingsActivityModule;
import com.wecast.mobile.ui.screen.settings.buffer.BufferActivity;
import com.wecast.mobile.ui.screen.settings.buffer.BufferActivityModule;
import com.wecast.mobile.ui.screen.settings.language.LanguageActivity;
import com.wecast.mobile.ui.screen.settings.language.LanguageActivityModule;
import com.wecast.mobile.ui.screen.settings.logout.LogoutDialog;
import com.wecast.mobile.ui.screen.settings.profile.EditProfileActivity;
import com.wecast.mobile.ui.screen.settings.profile.EditProfileActivityModule;
import com.wecast.mobile.ui.screen.settings.quality.VideoQualityActivity;
import com.wecast.mobile.ui.screen.settings.quality.VideoQualityActivityModule;
import com.wecast.mobile.ui.screen.show.TVShowFragmentProvider;
import com.wecast.mobile.ui.screen.show.details.TVShowDetailsActivity;
import com.wecast.mobile.ui.screen.show.details.TVShowDetailsActivityModule;
import com.wecast.mobile.ui.screen.show.genre.TVShowByGenreActivity;
import com.wecast.mobile.ui.screen.show.genre.TVShowByGenreActivityModule;
import com.wecast.mobile.ui.screen.show.player.TVShowPlayerActivity;
import com.wecast.mobile.ui.screen.show.player.TVShowPlayerActivityModule;
import com.wecast.mobile.ui.screen.show.search.TVShowSearchActivity;
import com.wecast.mobile.ui.screen.show.search.TVShowSearchActivityModule;
import com.wecast.mobile.ui.screen.splash.SplashActivity;
import com.wecast.mobile.ui.screen.splash.SplashActivityModule;
import com.wecast.mobile.ui.screen.trending.TrendingFragmentProvider;
import com.wecast.mobile.ui.screen.vod.VodFragmentProvider;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsActivity;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsActivityModule;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsPlayDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsPricingDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsRateDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsRentDialog;
import com.wecast.mobile.ui.screen.vod.details.VodDetailsRentPreviewDialog;
import com.wecast.mobile.ui.screen.vod.genre.VodByGenreActivity;
import com.wecast.mobile.ui.screen.vod.genre.VodByGenreActivityModule;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerErrorDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerActivity;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerActivityModule;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerAudioTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerTextTrackDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerVideoTrackDialog;
import com.wecast.mobile.ui.screen.vod.search.VodSearchActivity;
import com.wecast.mobile.ui.screen.vod.search.VodSearchActivityModule;
import com.wecast.mobile.ui.screen.welcome.WelcomeActivity;
import com.wecast.mobile.ui.screen.welcome.WelcomeActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by ageech@live.com
 */

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = {ComposerActivityModule.class})
    abstract ComposerActivity bindComposerActivity();

    @ContributesAndroidInjector(modules = {WelcomeActivityModule.class})
    abstract WelcomeActivity bindWelcomeActivity();

    @ContributesAndroidInjector(modules = {RegistrationActivityModule.class})
    abstract RegistrationActivity bindRegistrationActivity();

    @ContributesAndroidInjector(modules = {LoginActivityModule.class})
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector(modules = {ResetPasswordModule.class})
    abstract ResetPasswordActivity bindResetPasswordActivity();

    @ContributesAndroidInjector(modules = {SplashActivityModule.class})
    abstract SplashActivity bindSplashActivity();

    @ContributesAndroidInjector(modules = {
            NavigationActivityModule.class,
            HomeFragmentProvider.class,
            TrendingFragmentProvider.class,
            LiveTVFragmentProvider.class,
            ChannelFragmentProvider.class,
            TVGuideFragmentProvider.class,
            VodFragmentProvider.class,
            TVShowFragmentProvider.class
    })
    abstract NavigationActivity bindNavigationActivity();

    @ContributesAndroidInjector(modules = {
            ChannelDetailsActivityModule.class,
            ProgrammeFragmentProvider.class
    })
    abstract ChannelDetailsActivity bindChannelDetailsActivity();

    @ContributesAndroidInjector(modules = {ChannelSearchActivityModule.class})
    abstract ChannelSearchActivity bindChannelSearchActivity();

    @ContributesAndroidInjector(modules = {ProgrammeDetailsActivityModule.class})
    abstract ProgrammeDetailsActivity bindProgrammeDetailsActivity();

    @ContributesAndroidInjector(modules = {VodDetailsActivityModule.class})
    abstract VodDetailsActivity bindVodDetailsActivity();

    @ContributesAndroidInjector(modules = {VodByGenreActivityModule.class})
    abstract VodByGenreActivity bindVodByGenreActivity();

    @ContributesAndroidInjector(modules = {VodSearchActivityModule.class})
    abstract VodSearchActivity bindVodSearchActivity();

    @ContributesAndroidInjector(modules = {VodPlayerActivityModule.class})
    abstract VodPlayerActivity bindVodPlayerActivity();

    @ContributesAndroidInjector(modules = {TVShowDetailsActivityModule.class})
    abstract TVShowDetailsActivity bindTVShowDetailsActivity();

    @ContributesAndroidInjector(modules = {TVShowByGenreActivityModule.class})
    abstract TVShowByGenreActivity bindTVShowByGenreActivity();

    @ContributesAndroidInjector(modules = {TVShowSearchActivityModule.class})
    abstract TVShowSearchActivity bindTVShowSearchActivity();

    @ContributesAndroidInjector(modules = {TVShowPlayerActivityModule.class})
    abstract TVShowPlayerActivity bindTVShowPlayerActivity();

    @ContributesAndroidInjector(modules = {GalleryActivityModule.class})
    abstract GalleryActivity bindGalleryActivity();

    @ContributesAndroidInjector(modules = {SettingsActivityModule.class})
    abstract SettingsActivity bindSettingsActivity();

    @ContributesAndroidInjector(modules = {EditProfileActivityModule.class})
    abstract EditProfileActivity bindEditProfileActivity();

    @ContributesAndroidInjector(modules = {BufferActivityModule.class})
    abstract BufferActivity bindBufferActivity();

    @ContributesAndroidInjector(modules = {LanguageActivityModule.class})
    abstract LanguageActivity bindLanguageActivity();

    @ContributesAndroidInjector(modules = {VideoQualityActivityModule.class})
    abstract VideoQualityActivity bindVideoQualityActivity();
}
