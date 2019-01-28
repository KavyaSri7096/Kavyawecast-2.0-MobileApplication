package com.wecast.mobile.ui.screen.vod.details;

import androidx.databinding.ObservableField;

import com.wecast.core.data.db.entities.VodGenre;

/**
 * Created by ageech@live.com
 */

public class VodDetailsGenreViewModel {

    public final ObservableField<String> name;
    public final OnClickListener listener;

    private final VodGenre vodGenre;

    public VodDetailsGenreViewModel(VodGenre vodGenre, OnClickListener listener) {
        this.vodGenre = vodGenre;
        this.listener = listener;
        // Vod genre info
        this.name = new ObservableField<>(vodGenre.getName());
    }

    public void onItemClick() {
        listener.onItemClick(vodGenre);
    }

    public interface OnClickListener {

        void onItemClick(VodGenre item);
    }
}
