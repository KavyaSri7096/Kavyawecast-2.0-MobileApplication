package com.wecast.mobile.ui.screen.gallery;

import com.wecast.core.data.db.dao.TVShowDao;
import com.wecast.core.data.db.dao.VodDao;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.mobile.ui.base.BaseViewModel;

/**
 * Created by ageech@live.com
 */

public class GalleryActivityViewModel extends BaseViewModel<GalleryActivityNavigator> {

    private final VodDao vodDao;
    private final TVShowDao tvShowDao;

    public GalleryActivityViewModel(VodDao vodDao, TVShowDao tvShowDao) {
        this.vodDao = vodDao;
        this.tvShowDao = tvShowDao;
    }

    Vod getVodById(int id) {
        return vodDao.getById(id);
    }

    TVShow getTVShowById(int id) {
        return tvShowDao.getById(id);
    }
}
