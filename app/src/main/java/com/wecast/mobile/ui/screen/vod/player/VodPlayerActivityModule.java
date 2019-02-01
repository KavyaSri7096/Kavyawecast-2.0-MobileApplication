package com.wecast.mobile.ui.screen.vod.player;

import com.wecast.core.data.api.manager.VodManager;
import com.wecast.core.data.db.dao.TVShowDao;
import com.wecast.core.data.db.dao.VodDao;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.core.data.repository.VodRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class VodPlayerActivityModule {

    @Provides
    VodPlayerActivityViewModel provideVodPlayerViewModel(VodManager vodManager, VodRepository vodRepository, VodDao vodDao) {
        return new VodPlayerActivityViewModel(vodManager, vodRepository, vodDao);
    }
}
