package com.wecast.mobile.ui.screen.home;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.Highlighted;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.core.data.repository.HighlightedRepository;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class HomeFragmentViewModel extends BaseViewModel<HomeFragmentNavigator> {

    private final HighlightedRepository highlightedRepository;
    private final BannerRepository bannerRepository;
    private final ChannelRepository channelRepository;
    private final VodRepository vodRepository;
    private final TVShowRepository tvShowRepository;

    HomeFragmentViewModel(HighlightedRepository highlightedRepository, BannerRepository bannerRepository,
                          ChannelRepository channelRepository, VodRepository vodRepository, TVShowRepository tvShowRepository) {
        this.highlightedRepository = highlightedRepository;
        this.bannerRepository = bannerRepository;
        this.channelRepository = channelRepository;
        this.vodRepository = vodRepository;
        this.tvShowRepository = tvShowRepository;
    }

    Observable<ResponseWrapper<List<Highlighted>>> getHighlighted(boolean forceRemote) {
        return highlightedRepository.getAll(forceRemote);
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }

    Observable<ResponseWrapper<List<Vod>>> getContinueWatching(boolean forceRemote) {
        return vodRepository.getContinueWatching(forceRemote);
    }

    Observable<ResponseWrapper<List<Channel>>> getAllChannels(boolean forceRemote, int page) {
        return channelRepository.getAll(forceRemote, page);
    }

    Observable<ResponseWrapper<List<Vod>>> getAllMovies(boolean forceRemote, int page) {
        return vodRepository.getAll(forceRemote, page);
    }

    Observable<ResponseWrapper<List<TVShow>>> getAllTVShows(boolean forceRemote, int page) {
        return tvShowRepository.getAll(forceRemote, page);
    }
}
