package com.wecast.mobile.ui.screen.vod.player;

import com.wecast.core.data.api.manager.VodManager;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.dao.TVShowDao;
import com.wecast.core.data.db.dao.VodDao;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class VodPlayerActivityViewModel extends BaseViewModel<VodPlayerActivityNavigator> {

    private final VodManager vodManager;
    private final VodRepository vodRepository;
    private final VodDao vodDao;

    VodPlayerActivityViewModel(VodManager vodManager, VodRepository vodRepository, VodDao vodDao) {
        this.vodManager = vodManager;
        this.vodRepository = vodRepository;
        this.vodDao = vodDao;
    }

    Observable<Vod> getByID(int id, boolean isEpisode) {
        return vodRepository.getByID(id, isEpisode);
    }

    Observable<ResponseModel<Vod>> getSource(int id, int profileId) {
        return vodManager.getSource(id, profileId);
    }

    List<Vod> getEpisodes(int seasonId) {
        return vodDao.getBySeasonId(seasonId);
    }
}
