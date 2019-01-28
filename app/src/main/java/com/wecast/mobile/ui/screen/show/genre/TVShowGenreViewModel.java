package com.wecast.mobile.ui.screen.show.genre;

import androidx.databinding.ObservableField;

import com.wecast.core.data.db.entities.TVShowGenre;

/**
 * Created by ageech@live.com
 */

public class TVShowGenreViewModel {

    public final ObservableField<String> name;
    public final OnClickListener listener;

    private final TVShowGenre tvShowGenre;

    public TVShowGenreViewModel(TVShowGenre tvShowGenre, OnClickListener listener) {
        this.tvShowGenre = tvShowGenre;
        this.listener = listener;

        // Set tv show genre name
        this.name = new ObservableField<>(tvShowGenre.getName());
    }

    public void onItemClick() {
        listener.onItemClick(tvShowGenre);
    }

    public interface OnClickListener {

        void onItemClick(TVShowGenre item);
    }
}
