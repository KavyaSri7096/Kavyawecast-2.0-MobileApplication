package com.wecast.mobile.ui.screen.vod.details;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class VodDetailsActivityViewModel extends BaseViewModel {

    private final VodRepository vodRepository;
    private final BannerRepository bannerRepository;

    VodDetailsActivityViewModel(VodRepository vodRepository, BannerRepository bannerRepository) {
        this.vodRepository = vodRepository;
        this.bannerRepository = bannerRepository;
    }

    Observable<Vod> getByID(int id, boolean isEpisode) {
        return vodRepository.getByID(id, isEpisode);
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }
}
