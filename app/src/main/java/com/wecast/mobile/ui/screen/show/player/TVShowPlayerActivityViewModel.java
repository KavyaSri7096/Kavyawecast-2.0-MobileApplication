package com.wecast.mobile.ui.screen.show.player;

import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class TVShowPlayerActivityViewModel extends BaseViewModel<TVShowPlayerActivityNavigator> {

    private final TVShowRepository tvShowRepository;

    public TVShowPlayerActivityViewModel(TVShowRepository tvShowRepository) {
        this.tvShowRepository = tvShowRepository;
    }

    Observable<TVShow> getByID(int id) {
        return tvShowRepository.getByID(id);
    }
}
