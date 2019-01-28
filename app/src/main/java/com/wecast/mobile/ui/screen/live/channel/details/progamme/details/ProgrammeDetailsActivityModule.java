package com.wecast.mobile.ui.screen.live.channel.details.progamme.details;

import com.wecast.core.data.api.manager.ChannelManager;
import com.wecast.core.data.api.manager.TVGuideManager;
import com.wecast.core.data.db.dao.ReminderDao;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.core.data.repository.TVGuideRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class ProgrammeDetailsActivityModule {

    @Provides
    ProgrammeDetailsActivityViewModel provideProgrammeDetailsActivityViewModel(TVGuideRepository tvGuideRepository, TVGuideManager tvGuideManager,
                                                                               ChannelRepository channelRepository, ChannelManager channelManager, ReminderDao reminderDao) {
        return new ProgrammeDetailsActivityViewModel(tvGuideRepository, tvGuideManager, channelRepository, channelManager, reminderDao);
    }
}
