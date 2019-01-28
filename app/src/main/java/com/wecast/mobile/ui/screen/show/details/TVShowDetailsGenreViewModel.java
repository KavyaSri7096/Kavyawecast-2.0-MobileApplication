package com.wecast.mobile.ui.screen.show.details;

import com.wecast.core.data.db.entities.TVShowGenre;

import androidx.databinding.ObservableField;

/**
 * Created by ageech@live.com
 */

public class TVShowDetailsGenreViewModel {

    public final ObservableField<String> name;
    public final OnClickListener listener;

    private final TVShowGenre tvShowGenre;

    public TVShowDetailsGenreViewModel(TVShowGenre tvShowGenre, OnClickListener listener) {
        this.tvShowGenre = tvShowGenre;
        this.listener = listener;

        // Set name
        this.name = new ObservableField<>(tvShowGenre.getName());
    }

    public void onItemClick() {
        listener.onItemClick(tvShowGenre);
    }

    public interface OnClickListener {

        void onItemClick(TVShowGenre item);
    }
}
