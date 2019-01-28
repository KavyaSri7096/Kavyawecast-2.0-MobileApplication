package com.wecast.mobile.ui.screen.show.details;

import com.wecast.core.data.db.entities.ShowType;

import androidx.databinding.ObservableField;

/**
 * Created by ageech@live.com
 */

public class TVShowDetailsShowTypeViewModel {

    public final ObservableField<String> name;
    public final OnClickListener listener;

    private final ShowType showType;

    public TVShowDetailsShowTypeViewModel(ShowType showType, OnClickListener listener) {
        this.showType = showType;
        this.listener = listener;

        // Set name
        this.name = new ObservableField<>(showType.getName());
    }

    public void onItemClick() {
        listener.onItemClick(showType);
    }

    public interface OnClickListener {

        void onItemClick(ShowType item);
    }
}
