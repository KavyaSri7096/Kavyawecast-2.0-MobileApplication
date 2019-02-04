package com.wecast.mobile.ui.screen.show.genre;

import androidx.databinding.ObservableField;

import com.wecast.core.data.db.entities.TVShowGenre;

/**
 * Created by ageech@live.com
 */

public class TVShowGenreViewModel {

    public final ObservableField<String> name;

    private final TVShowGenre tvShowGenre;

    public TVShowGenreViewModel(TVShowGenre tvShowGenre) {
        this.tvShowGenre = tvShowGenre;

        // Set tv show genre name
        this.name = new ObservableField<>(tvShowGenre.getName());
    }
}