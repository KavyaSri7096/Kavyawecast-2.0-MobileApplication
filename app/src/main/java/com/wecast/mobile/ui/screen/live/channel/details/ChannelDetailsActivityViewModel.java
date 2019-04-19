package com.wecast.mobile.ui.screen.live.channel.details;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.api.manager.ChannelManager;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.Favorite;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class ChannelDetailsActivityViewModel extends BaseViewModel<ChannelDetailsActivityNavigator> {

    private final ChannelRepository channelRepository;
    private final ChannelManager channelManager;

    public ChannelDetailsActivityViewModel(ChannelRepository channelRepository, ChannelManager channelManager) {
        this.channelRepository = channelRepository;
        this.channelManager = channelManager;
    }

    Observable<Channel> getByID(int id) {
        return channelRepository.getById(id);
    }

    Observable<ResponseWrapper<List<Channel>>> getAll() {
        return channelRepository.getAll(false, 1);
    }

    Observable<ResponseModel<Favorite>> addToFavorites(int id) {
        return channelManager.addToFavorites(id);
    }

    Observable<ResponseModel<Favorite>> removeFromFavorites(int id) {
        return channelManager.removeFromFavorites(id);
    }
}
