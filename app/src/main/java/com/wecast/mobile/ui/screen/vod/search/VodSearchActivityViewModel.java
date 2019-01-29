package com.wecast.mobile.ui.screen.vod.search;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.api.manager.VodManager;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.ShowType;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class VodSearchActivityViewModel extends BaseViewModel<VodSearchActivityNavigator> {

    private final VodManager vodManager;
    private final BannerRepository bannerRepository;

    VodSearchActivityViewModel(VodManager vodManager, BannerRepository bannerRepository) {
        this.vodManager = vodManager;
        this.bannerRepository = bannerRepository;

        setLoading(false);
    }

    Observable<ResponseWrapper<List<Vod>>> search(String query, List<ShowType> showTypeList) {
        return vodManager.search(query, showTypeList)
                .map(response -> {
                    if (response.isTokenExpired()) {
                        return ResponseWrapper.tokenExpired();
                    } else if (response.isSubscriptionExpired()) {
                        List<Vod> data = response.getData().getItems();
                        return ResponseWrapper.subscriptionExpired(data);
                    } else if (response.isSuccessful()) {
                        List<Vod> data = response.getData().getItems();
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
