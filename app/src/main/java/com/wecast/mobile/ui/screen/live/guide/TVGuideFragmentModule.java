package com.wecast.mobile.ui.screen.live.guide;

import androidx.lifecycle.ViewModelProvider;

import com.wecast.core.data.api.manager.TVGuideManager;
import com.wecast.mobile.factory.ViewModelProviderFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class TVGuideFragmentModule {

    @Provides
    TVGuideFragmentViewModel tvGuideFragmentViewModel(TVGuideManager tvGuideManager) {
        return new TVGuideFragmentViewModel(tvGuideManager);
    }

    @Provides
    ViewModelProvider.Factory provideTVGuideFragmentViewModel(TVGuideFragmentViewModel viewModel) {
        return new ViewModelProviderFactory<>(viewModel);
    }
}
