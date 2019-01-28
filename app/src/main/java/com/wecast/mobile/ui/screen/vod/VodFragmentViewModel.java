package com.wecast.mobile.ui.screen.vod;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.entities.VodGenre;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.VodGenreRepository;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class VodFragmentViewModel extends BaseViewModel<VodFragmentNavigator> {

    private final VodRepository vodRepository;
    private final BannerRepository bannerRepository;
    private final VodGenreRepository vodGenreRepository;

    VodFragmentViewModel(VodRepository vodRepository, BannerRepository bannerRepository, VodGenreRepository vodGenreRepository) {
        this.vodRepository = vodRepository;
        this.bannerRepository = bannerRepository;
        this.vodGenreRepository = vodGenreRepository;
    }

    Observable<ResponseWrapper<List<Vod>>> getRecommended(boolean forceRemote) {
        return vodRepository.getRecommended(forceRemote);
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }

    Observable<ResponseWrapper<List<VodGenre>>> getGenres(boolean forceRemote) {
        return vodGenreRepository.getGenres(forceRemote);
    }

    Observable<ResponseWrapper<List<Vod>>> getRecentlyAdded(boolean forceRemote, int page) {
        return vodRepository.getRecentlyAdded(forceRemote, page);
    }
}
