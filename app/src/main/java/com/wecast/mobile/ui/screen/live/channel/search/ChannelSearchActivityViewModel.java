package com.wecast.mobile.ui.screen.live.channel.search;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.api.manager.ChannelManager;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class ChannelSearchActivityViewModel extends BaseViewModel<ChannelSearchActivityNavigator> {

    private final ChannelManager channelManager;
    private final BannerRepository bannerRepository;

    ChannelSearchActivityViewModel(ChannelManager channelManager, BannerRepository bannerRepository) {
        this.channelManager = channelManager;
        this.bannerRepository = bannerRepository;

        setLoading(false);
    }

    Observable<ResponseWrapper<List<Channel>>> search(String query) {
        return channelManager.search(query)
                .map(response -> {
                    if (response.isTokenExpired()) {
                        return ResponseWrapper.tokenExpired();
                    } else if (response.isSubscriptionExpired()) {
                        List<Channel> data = response.getData();
                        return ResponseWrapper.subscriptionExpired(data);
                    } else if (response.isSuccessful()) {
                        List<Channel> data = response.getData();
                        return ResponseWrapper.success(data);
                    } else {
                        return ResponseWrapper.error(response.getMessage());
                    }
                });
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }
}
