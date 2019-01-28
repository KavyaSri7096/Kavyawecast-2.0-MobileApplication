package com.wecast.mobile.ui.screen.live;

import com.wecast.core.data.db.entities.composer.Modules;
import com.wecast.core.data.repository.ComposerRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

/**
 * Created by ageech@live.com
 */

public class LiveTVFragmentViewModel extends BaseViewModel<LiveTVFragmentNavigator> {

    private final ComposerRepository composerRepository;

    LiveTVFragmentViewModel(ComposerRepository composerRepository) {
        this.composerRepository = composerRepository;
    }

    Modules getAppModules() {
        return composerRepository.getAppModules();
    }
}
