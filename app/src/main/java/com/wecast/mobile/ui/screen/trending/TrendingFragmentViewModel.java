package com.wecast.mobile.ui.screen.trending;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class TrendingFragmentViewModel extends BaseViewModel<TrendingFragmentNavigator> {

    private final BannerRepository bannerRepository;
    private final ChannelRepository channelRepository;
    private final VodRepository vodRepository;
    private final TVShowRepository tvShowRepository;

    public TrendingFragmentViewModel(BannerRepository bannerRepository, ChannelRepository channelRepository,
                                     VodRepository vodRepository, TVShowRepository tvShowRepository) {
        this.bannerRepository = bannerRepository;
        this.channelRepository = channelRepository;
        this.vodRepository = vodRepository;
        this.tvShowRepository = tvShowRepository;
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }

    Observable<ResponseWrapper<List<Channel>>> getTopChannels(boolean forceRemote) {
        return channelRepository.getTrending(forceRemote);
    }

    Observable<ResponseWrapper<List<Vod>>> getTopMovies(boolean forceRemote) {
        return vodRepository.getTrending(forceRemote);
    }

    Observable<ResponseWrapper<List<TVShow>>> getTopTVShows(boolean forceRemote) {
        return tvShowRepository.getTrending(forceRemote);
    }
}
