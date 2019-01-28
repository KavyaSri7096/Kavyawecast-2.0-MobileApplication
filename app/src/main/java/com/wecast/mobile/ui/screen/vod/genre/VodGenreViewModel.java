package com.wecast.mobile.ui.screen.vod.genre;

import com.wecast.core.data.db.entities.VodGenre;

import androidx.databinding.ObservableField;

/**
 * Created by ageech@live.com
 */

public class VodGenreViewModel {

    public final ObservableField<String> name;
    public final OnClickListener listener;

    private final VodGenre vodGenre;

    public VodGenreViewModel(VodGenre vodGenre, OnClickListener listener) {
        this.vodGenre = vodGenre;
        this.listener = listener;

        // Set vod genre name
        this.name = new ObservableField<>(vodGenre.getName());
    }

    public void onItemClick() {
        listener.onItemClick(vodGenre);
    }

    public interface OnClickListener {

        void onItemClick(VodGenre item);
    }
}
