package com.wecast.mobile.ui.screen.composer;

import com.wecast.core.data.db.entities.composer.Composer;
import com.wecast.core.data.repository.ComposerRepository;
import com.wecast.mobile.ui.base.BaseViewModel;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class ComposerActivityViewModel extends BaseViewModel<ComposerActivityNavigator> {

    private final ComposerRepository composerRepository;

    ComposerActivityViewModel(ComposerRepository composerRepository) {
        this.composerRepository = composerRepository;
    }

    public Observable<Composer> fetchComposer() {
        return composerRepository.getComposer();
    }
}
