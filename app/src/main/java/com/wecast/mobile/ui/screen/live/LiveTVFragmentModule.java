package com.wecast.mobile.ui.screen.live;

import androidx.lifecycle.ViewModelProvider;

import com.wecast.core.data.repository.ComposerRepository;
import com.wecast.mobile.factory.ViewModelProviderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class LiveTVFragmentModule {

    @Provides
    LiveTVFragmentViewModel liveTVFragmentViewModel(ComposerRepository composerRepository) {
        return new LiveTVFragmentViewModel(composerRepository);
    }

    @Provides
    ViewModelProvider.Factory provideLiveTVFragmentViewModel(LiveTVFragmentViewModel viewModel) {
        return new ViewModelProviderFactory<>(viewModel);
    }
}
