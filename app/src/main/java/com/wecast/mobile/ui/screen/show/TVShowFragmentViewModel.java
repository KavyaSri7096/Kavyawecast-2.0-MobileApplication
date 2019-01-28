package com.wecast.mobile.ui.screen.show;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.TVShowGenre;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.TVShowGenreRepository;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class TVShowFragmentViewModel extends BaseViewModel<TVShowNavigator> {

    private final TVShowRepository tvShowRepository;
    private final BannerRepository bannerRepository;
    private final TVShowGenreRepository tvShowGenreRepository;

    TVShowFragmentViewModel(TVShowRepository tvShowRepository, BannerRepository bannerRepository, TVShowGenreRepository tvShowGenreRepository) {
        this.tvShowRepository = tvShowRepository;
        this.bannerRepository = bannerRepository;
        this.tvShowGenreRepository = tvShowGenreRepository;
    }

    Observable<ResponseWrapper<List<TVShow>>> getRecentlyAdded(boolean forceRemote) {
        return tvShowRepository.getRecommended(forceRemote);
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }

    Observable<ResponseWrapper<List<TVShowGenre>>> getGenres(boolean forceRemote) {
        return tvShowGenreRepository.getGenres(forceRemote);
    }

    Observable<ResponseWrapper<List<TVShow>>> getRecentlyAdded(boolean forceRemote, int page) {
        return tvShowRepository.getRecentlyAdded(forceRemote, page);
    }
}
