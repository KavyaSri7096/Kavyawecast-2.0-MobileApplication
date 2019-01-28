package com.wecast.mobile.di.component;

import com.wecast.core.di.component.CoreComponent;
import com.wecast.mobile.WeApp;
import com.wecast.mobile.di.AppScope;
import com.wecast.mobile.di.builder.ActivityBuilder;
import com.wecast.mobile.di.builder.DialogBuilder;
import com.wecast.mobile.di.builder.ServiceBuilder;
import com.wecast.mobile.di.module.AppModule;
import com.wecast.mobile.ui.screen.home.HomeContinueListRow;
import com.wecast.mobile.ui.screen.home.HomeHighlightedListRow;
import com.wecast.mobile.ui.screen.home.HomeLiveTVListRow;
import com.wecast.mobile.ui.screen.home.HomeMoviesListRow;
import com.wecast.mobile.ui.screen.home.HomeTVShowsListRow;
import com.wecast.mobile.ui.screen.live.channel.ChannelFavoriteListRow;
import com.wecast.mobile.ui.screen.live.channel.ChannelListRow;
import com.wecast.mobile.ui.screen.show.details.TVShowEpisodeListRow;
import com.wecast.mobile.ui.screen.show.genre.TVShowGenreListRow;
import com.wecast.mobile.ui.screen.show.TVShowRecentlyAddedListRow;
import com.wecast.mobile.ui.screen.show.TVShowRecommendedListRow;
import com.wecast.mobile.ui.screen.trending.TrendingChannelListRow;
import com.wecast.mobile.ui.screen.trending.TrendingTVShowListRow;
import com.wecast.mobile.ui.screen.trending.TrendingVodListRow;
import com.wecast.mobile.ui.screen.vod.genre.VodGenreListRow;
import com.wecast.mobile.ui.screen.vod.VodRecentlyAddedListRow;
import com.wecast.mobile.ui.screen.vod.VodRecommendedListRow;
import com.wecast.mobile.ui.widget.banner.BannerView;
import com.wecast.mobile.ui.widget.wecast.WeCastRemoteViewsFactory;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by ageech@live.com
 */

@AppScope
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                AppModule.class,
                ActivityBuilder.class,
                DialogBuilder.class,
                ServiceBuilder.class
        },
        dependencies = {
                CoreComponent.class
        }
)
public interface AppComponent {

    void inject(WeApp app);

    void inject(WeCastRemoteViewsFactory weCastWidget);

    void inject(BannerView bannerView);

    void inject(HomeContinueListRow view);

    void inject(HomeHighlightedListRow view);

    void inject(HomeLiveTVListRow view);

    void inject(HomeMoviesListRow view);

    void inject(HomeTVShowsListRow view);

    void inject(TrendingChannelListRow view);

    void inject(TrendingVodListRow view);

    void inject(TrendingTVShowListRow view);

    void inject(ChannelFavoriteListRow view);

    void inject(ChannelListRow view);

    void inject(VodRecommendedListRow view);

    void inject(VodGenreListRow view);

    void inject(VodRecentlyAddedListRow view);

    void inject(TVShowRecommendedListRow view);

    void inject(TVShowGenreListRow view);

    void inject(TVShowRecentlyAddedListRow view);

    void inject(TVShowEpisodeListRow view);
}
