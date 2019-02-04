package com.wecast.mobile.ui.screen.vod.genre;

import com.wecast.core.data.db.entities.VodGenre;

import androidx.databinding.ObservableField;

/**
 * Created by ageech@live.com
 */

public class VodGenreViewModel {

    public final ObservableField<String> name;

    private final VodGenre vodGenre;

    public VodGenreViewModel(VodGenre vodGenre) {
        this.vodGenre = vodGenre;

        // Set vod genre name
        this.name = new ObservableField<>(vodGenre.getName());
    }
}
