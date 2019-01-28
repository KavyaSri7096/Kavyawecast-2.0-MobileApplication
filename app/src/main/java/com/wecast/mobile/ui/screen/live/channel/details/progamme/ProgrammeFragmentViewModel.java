package com.wecast.mobile.ui.screen.live.channel.details.progamme;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.db.dao.ChannelDao;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.TVGuide;
import com.wecast.core.data.repository.TVGuideRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class ProgrammeFragmentViewModel extends BaseViewModel<ProgrammeFragmentNavigator> {

    private final TVGuideRepository tvGuideRepository;
    private final ChannelDao channelDao;

    public ProgrammeFragmentViewModel(TVGuideRepository tvGuideRepository, ChannelDao channelDao) {
        this.tvGuideRepository = tvGuideRepository;
        this.channelDao = channelDao;
    }

    Observable<ResponseWrapper<TVGuide>> getProgrammes(boolean forceRemote, int page, int channelId, String start, String end) {
        return tvGuideRepository.getProgrammes(forceRemote, page, channelId, start, end);
    }

    Channel getById(int id) {
        return channelDao.getById(id);
    }
}

