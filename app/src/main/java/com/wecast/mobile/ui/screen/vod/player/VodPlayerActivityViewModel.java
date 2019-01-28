package com.wecast.mobile.ui.screen.vod.player;

import com.wecast.core.data.api.manager.VodManager;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class VodPlayerActivityViewModel extends BaseViewModel<VodPlayerActivityNavigator> {

    private final VodManager vodManager;
    private final VodRepository vodRepository;

    public VodPlayerActivityViewModel(VodManager vodManager, VodRepository vodRepository) {
        this.vodManager = vodManager;
        this.vodRepository = vodRepository;
    }

    Observable<Vod> getByID(int id, boolean isEpisode) {
        return vodRepository.getByID(id, isEpisode);
    }

    Observable<ResponseModel<Vod>> getSource(int id, int profileId) {
        return vodManager.getSource(id, profileId);
    }
}
