package com.wecast.mobile.ui.screen.live.channel.details.progamme;

import androidx.databinding.ObservableField;

import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.utils.TVGuideUtils;

/**
 * Created by ageech@live.com
 */

public class ProgrammeViewModel {

    public final ObservableField<String> title;
    public final ObservableField<String> description;
    public final ObservableField<String> time;

    private final TVGuideProgramme tvGuideProgramme;

    ProgrammeViewModel(TVGuideProgramme tvGuideProgramme) {
        this.tvGuideProgramme = tvGuideProgramme;

        // Set programme title
        this.title = new ObservableField<>(tvGuideProgramme.getTitle());

        // Set programme description
        this.description = new ObservableField<>(tvGuideProgramme.getDescription());

        // Set programme time
        this.time = new ObservableField<>(TVGuideUtils.getStart(tvGuideProgramme));
    }
}
