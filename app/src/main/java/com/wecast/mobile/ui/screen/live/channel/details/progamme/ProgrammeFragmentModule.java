package com.wecast.mobile.ui.screen.live.channel.details.progamme;

import androidx.lifecycle.ViewModelProvider;

import com.wecast.core.data.db.dao.ChannelDao;
import com.wecast.core.data.repository.TVGuideRepository;
import com.wecast.mobile.factory.ViewModelProviderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class ProgrammeFragmentModule {

    @Provides
    ProgrammeFragmentViewModel programmeFragmentViewModel(TVGuideRepository tvGuideRepository, ChannelDao channelDao) {
        return new ProgrammeFragmentViewModel(tvGuideRepository, channelDao);
    }

    @Provides
    ViewModelProvider.Factory provideProgrammeFragmentViewModel(ProgrammeFragmentViewModel viewModel) {
        return new ViewModelProviderFactory<>(viewModel);
    }
}
