package com.wecast.mobile.ui.screen.show.details;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class TVShowDetailsActivityViewModel extends BaseViewModel {

    private final TVShowRepository tvShowRepository;
    private final BannerRepository bannerRepository;

    TVShowDetailsActivityViewModel(TVShowRepository tvShowRepository, BannerRepository bannerRepository) {
        this.tvShowRepository = tvShowRepository;
        this.bannerRepository = bannerRepository;
    }

    Observable<TVShow> getByID(int id) {
        return tvShowRepository.getByID(id);
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }

    Observable<ResponseWrapper<List<Vod>>> getEpisodes(int page, int tvShowId, int seasonId) {
        return tvShowRepository.getEpisodes(page, tvShowId, seasonId);
    }
}
