package com.wecast.mobile.ui.screen.show.genre;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class TVShowByGenreActivityViewModel extends BaseViewModel {

    private final TVShowRepository tvShowRepository;
    private final BannerRepository bannerRepository;

    TVShowByGenreActivityViewModel(TVShowRepository vodRepository, BannerRepository bannerRepository) {
        this.tvShowRepository = vodRepository;
        this.bannerRepository = bannerRepository;
    }

    Observable<ResponseWrapper<List<TVShow>>> getByGenreID(boolean forceRemote, int page, int genreId) {
        return tvShowRepository.getByGenreID(forceRemote, page, genreId);
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }
}
