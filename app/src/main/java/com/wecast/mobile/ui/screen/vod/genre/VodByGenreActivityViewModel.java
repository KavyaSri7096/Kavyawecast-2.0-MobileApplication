package com.wecast.mobile.ui.screen.vod.genre;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class VodByGenreActivityViewModel extends BaseViewModel<VodByGenreActivityNavigator> {

    private final VodRepository vodRepository;
    private final BannerRepository bannerRepository;

    VodByGenreActivityViewModel(VodRepository vodRepository, BannerRepository bannerRepository) {
        this.vodRepository = vodRepository;
        this.bannerRepository = bannerRepository;
    }

    Observable<ResponseWrapper<List<Vod>>> getByGenreID(boolean forceRemote, int page, int genreId) {
        return vodRepository.getByGenreID(forceRemote, page, genreId);
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }
}
