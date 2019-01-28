package com.wecast.mobile.ui.screen.splash;

import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.data.repository.ChannelRepository;
import com.wecast.core.data.repository.HighlightedRepository;
import com.wecast.core.data.repository.ReminderRepository;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.core.data.repository.VodRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class SplashActivityModule {

    @Provides
    SplashActivityViewModel provideSplashViewModel(PreferenceManager preferenceManager, AccountManager accountManager, ReminderRepository reminderRepository, HighlightedRepository highlightedRepository,
                                                   ChannelRepository channelRepository, VodRepository vodRepository, TVShowRepository tvShowRepository) {
        return new SplashActivityViewModel(preferenceManager, accountManager, reminderRepository, highlightedRepository, channelRepository, vodRepository, tvShowRepository);
    }
}
