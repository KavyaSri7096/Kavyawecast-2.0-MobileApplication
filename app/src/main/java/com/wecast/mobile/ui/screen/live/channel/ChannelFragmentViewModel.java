package com.wecast.mobile.ui.screen.live.channel;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class ChannelFragmentViewModel extends BaseViewModel<ChannelFragmentNavigator> {

    private final ChannelRepository channelRepository;
    private final BannerRepository bannerRepository;

    public ChannelFragmentViewModel(ChannelRepository channelRepository, BannerRepository bannerRepository) {
        this.channelRepository = channelRepository;
        this.bannerRepository = bannerRepository;
    }

    Observable<ResponseWrapper<List<Channel>>> getFavorites(boolean forceRemote) {
        return channelRepository.getFavorites(forceRemote);
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }

    Observable<ResponseWrapper<List<Channel>>> getAll(boolean forceRemote, int page) {
        return channelRepository.getAll(forceRemote, page);
    }

    Observable<ResponseWrapper<List<Channel>>> getByGenreId(boolean forceRemote, int genreId) {
        return channelRepository.getByGenreId(forceRemote, genreId);
    }
}
