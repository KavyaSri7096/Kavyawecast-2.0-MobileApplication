package com.wecast.mobile.ui.screen.show;

import androidx.databinding.ObservableField;

import com.wecast.core.data.db.entities.TVShow;

/**
 * Created by ageech@live.com
 */

public class TVShowViewModel {

    public final ObservableField<String> title;
    public final ObservableField<String> coverUrl;
    public final OnClickListener listener;

    private final TVShow tvShow;

    TVShowViewModel(TVShow tvShow, OnClickListener listener) {
        this.tvShow = tvShow;
        this.listener = listener;

        // Set tv show title
        this.title = new ObservableField<>(tvShow.getTitle());

        // Set tv show cover
        this.coverUrl = new ObservableField<>("");
        if (tvShow.getCovers() != null && tvShow.getCovers().size() > 0) {
            this.coverUrl.set(tvShow.getCovers().get(0).getPreviewAr());
        }
    }

    public void onItemClick() {
        listener.onItemClick(tvShow);
    }

    public interface OnClickListener {

        void onItemClick(TVShow item);
    }
}
