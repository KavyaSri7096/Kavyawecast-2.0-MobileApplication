package com.wecast.mobile.ui.screen.composer;

import com.wecast.core.data.repository.ComposerRepository;

import dagger.Module;
import dagger.Provides;

/**
 * Created by ageech@live.com
 */

@Module
public class ComposerActivityModule {

    @Provides
    ComposerActivityViewModel provideComposerViewModel(ComposerRepository composerRepository) {
        return new ComposerActivityViewModel(composerRepository);
    }
}
