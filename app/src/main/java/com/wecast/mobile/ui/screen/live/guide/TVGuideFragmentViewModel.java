package com.wecast.mobile.ui.screen.live.guide;

import com.wecast.core.data.api.manager.TVGuideManager;
import com.wecast.core.data.api.model.PagedData;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.entities.TVGuide;
import com.wecast.mobile.ui.base.BaseViewModel;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class TVGuideFragmentViewModel extends BaseViewModel<TVGuideFragmentNavigator> {

    private final TVGuideManager tvGuideManager;

    public TVGuideFragmentViewModel(TVGuideManager tvGuideManager) {
        this.tvGuideManager  = tvGuideManager;
    }

    Observable<ResponseModel<PagedData<TVGuide>>> getCurrentProgrammes(int page) {
        return tvGuideManager.getCurrentProgrammes(page);
    }
}
