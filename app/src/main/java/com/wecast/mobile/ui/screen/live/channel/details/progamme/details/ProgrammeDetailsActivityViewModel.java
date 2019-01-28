package com.wecast.mobile.ui.screen.live.channel.details.progamme.details;

import com.wecast.core.data.api.manager.ChannelManager;
import com.wecast.core.data.api.manager.TVGuideManager;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.dao.ReminderDao;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.Favorite;
import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.data.db.entities.TVGuideReminder;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.core.data.repository.TVGuideRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class ProgrammeDetailsActivityViewModel extends BaseViewModel<ProgrammeDetailsActivityNavigator> {

    private final TVGuideRepository tvGuideRepository;
    private final TVGuideManager tvGuideManager;
    private final ChannelRepository channelRepository;
    private final ChannelManager channelManager;
    private final ReminderDao reminderDao;

    public ProgrammeDetailsActivityViewModel(TVGuideRepository tvGuideRepository, TVGuideManager tvGuideManager,
                                             ChannelRepository channelRepository, ChannelManager channelManager, ReminderDao reminderDao) {
        this.tvGuideRepository = tvGuideRepository;
        this.tvGuideManager = tvGuideManager;
        this.channelRepository = channelRepository;
        this.channelManager = channelManager;
        this.reminderDao = reminderDao;
    }

    TVGuideProgramme getProgrammeByID(String id) {
        return tvGuideRepository.getProgrammeById(id);
    }

    Observable<ResponseModel<TVGuideReminder>> addReminder(int channelId, int epgChannelId, String epgProgrammeStringId) {
        return tvGuideManager.addReminder(channelId, epgChannelId, epgProgrammeStringId);
    }

    Observable<ResponseModel> removeReminder(int id) {
        return tvGuideManager.removeReminder(id);
    }

    Observable<ResponseModel<TVGuideReminder>> editReminder(int id, String remindMe) {
        return tvGuideManager.editReminder(id, remindMe);
    }

    Observable<Channel> getById(int id) {
        return channelRepository.getById(id);
    }

    Observable<ResponseModel<Favorite>> addToFavorites(int id) {
        return channelManager.addToFavorites(id);
    }

    Observable<ResponseModel<Favorite>> removeFromFavorites(int id) {
        return channelManager.removeFromFavorites(id);
    }

    TVGuideReminder getReminderById(String stringId) {
        return reminderDao.getByStringId(stringId);
    }

    void addReminderToDatabase(TVGuideReminder data) {
        reminderDao.insert(data);
    }

    void removeReminderFromDatabase(int id) {
        reminderDao.remove(id);
    }
}
