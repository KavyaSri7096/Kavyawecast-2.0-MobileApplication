package com.wecast.mobile.ui.screen.gallery;

import com.wecast.core.data.db.dao.TVShowDao;
import com.wecast.core.data.db.dao.VodDao;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class GalleryActivityModule {

    @Provides
    GalleryActivityViewModel provideGalleryViewModel(VodDao vodDao, TVShowDao tvShowDao) {
        return new GalleryActivityViewModel(vodDao, tvShowDao);
    }
}
