package com.wecast.mobile.ui.screen.show.search;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.api.manager.TVShowManager;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.ShowType;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class TVShowSearchActivityViewModel extends BaseViewModel<TVShowSearchActivityNavigator> {

    private final TVShowManager tvShowManager;
    private final BannerRepository bannerRepository;

    TVShowSearchActivityViewModel(TVShowManager tvShowManager, BannerRepository bannerRepository) {
        this.tvShowManager = tvShowManager;
        this.bannerRepository = bannerRepository;

        setLoading(false);
    }

    Observable<ResponseWrapper<List<TVShow>>> search(String query, List<ShowType> showTypeList) {
        return tvShowManager.search(query, showTypeList)
                .map(response -> {
                    if (response.isTokenExpired()) {
                        return ResponseWrapper.tokenExpired();
                    } else if (response.isSubscriptionExpired()) {
                        List<TVShow> data = response.getData().getItems();
                        return ResponseWrapper.subscriptionExpired(data);
                    } else if (response.isSuccessful()) {
                        List<TVShow> data = response.getData().getItems();
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
