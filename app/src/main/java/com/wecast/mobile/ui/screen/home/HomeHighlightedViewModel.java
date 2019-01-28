package com.wecast.mobile.ui.screen.home;

import androidx.databinding.ObservableField;

import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.Highlighted;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.mobile.utils.CommonUtils;

import java.util.Locale;

/**
 * Created by ageech@live.com
 */

public class HomeHighlightedViewModel {

    public final ObservableField<String> title;
    public final ObservableField<String> info;
    public final ObservableField<String> imageUrl;
    public final OnClickListener listener;

    private final Highlighted highlighted;

    HomeHighlightedViewModel(Highlighted highlighted, OnClickListener listener) {
        this.highlighted = highlighted;
        this.listener = listener;

        // Set highlighted title
        this.title = new ObservableField<>(highlighted.getTitle());

        // Set highlighted info
        this.info = new ObservableField<>(getInfo());

        // Set highlighted image
        this.imageUrl = new ObservableField<>(highlighted.getImage());
    }

    private String getInfo() {
        Vod itemVod = highlighted.getMovieModel();
        if (itemVod != null) {
            if (itemVod.getYear() != null && itemVod.getRuntime() != null) {
                return String.format("%1$s | %2$s", itemVod.getYear(), CommonUtils.getRuntime(itemVod.getRuntime()));
            } else if (itemVod.getYear() != null) {
                return itemVod.getYear();
            } else if (itemVod.getRuntime() != null) {
                return CommonUtils.getRuntime(itemVod.getRuntime());
            }
        }
        TVShow itemTVShow = highlighted.getTVShowModel();
        if (itemTVShow != null) {
            if (itemTVShow.getYear() != null) {
                return String.format(Locale.getDefault(), "%1$s | Seasons : %2$d", itemTVShow.getYear(), itemTVShow.getSeasonCount());
            } else {
                return String.format(Locale.getDefault(), "%1$d Season(s)", itemTVShow.getSeasonCount());
            }
        }
        Channel itemChannel = highlighted.getChannelModel();
        if (itemChannel != null) {
            imageUrl.set(itemChannel.getScreenShotUrl());
            return itemChannel.getTitle();
        }
        return "";
    }

    public void onItemClick() {
        listener.onItemClick(highlighted);
    }

    public interface OnClickListener {

        void onItemClick(Highlighted item);
    }
}
