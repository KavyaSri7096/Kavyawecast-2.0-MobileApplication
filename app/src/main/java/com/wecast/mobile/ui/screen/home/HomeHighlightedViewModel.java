package com.wecast.mobile.ui.screen.home;

import android.text.TextUtils;

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
        this.imageUrl = new ObservableField<>(getImage());
    }

    private String getImage() {
        if (!TextUtils.isEmpty(highlighted.getImage())) {
            return highlighted.getImage();
        } else if (highlighted.getChannelModel() != null) {
            // If highlighted is channel then background should be screenshot
            Channel itemChannel = highlighted.getChannelModel();
            return itemChannel.getScreenShotUrl();
        } else if (highlighted.getMovieModel() != null) {
            // If highlighted is move/episode background should be banner or gallery
            Vod itemVod = highlighted.getMovieModel();
            if (itemVod.getBanners() != null && itemVod.getBanners().size() > 0) {
                return itemVod.getBanners().get(0).getPreviewAr();
            } else if (itemVod.getGallery() != null && itemVod.getGallery().size() > 0) {
                return itemVod.getGallery().get(0).getPreviewAr();
            }
        } else if (highlighted.getTVShowModel() != null) {
            // If highlighted is tv show background should be banner or gallery
            TVShow itemTVShow = highlighted.getTVShowModel();
            if (itemTVShow.getBanners() != null && itemTVShow.getBanners().size() > 0) {
                return itemTVShow.getBanners().get(0).getPreviewAr();
            } else if (itemTVShow.getGallery() != null && itemTVShow.getGallery().size() > 0) {
                return itemTVShow.getGallery().get(0).getPreviewAr();
            }
        }
        return null;
    }

    private String getInfo() {
        if (highlighted.getMovieModel() != null) {
            Vod itemVod = highlighted.getMovieModel();
            if (itemVod.getYear() != null && itemVod.getRuntime() != null) {
                return String.format("%1$s | %2$s", itemVod.getYear(), CommonUtils.getRuntime(itemVod.getRuntime()));
            } else if (itemVod.getYear() != null) {
                return itemVod.getYear();
            } else if (itemVod.getRuntime() != null) {
                return CommonUtils.getRuntime(itemVod.getRuntime());
            }
        } else if (highlighted.getTVShowModel() != null) {
            TVShow itemTVShow = highlighted.getTVShowModel();
            if (itemTVShow.getYear() != null) {
                return String.format(Locale.getDefault(), "%1$s | Seasons : %2$d", itemTVShow.getYear(), itemTVShow.getSeasonCount());
            } else {
                return String.format(Locale.getDefault(), "%1$d Season(s)", itemTVShow.getSeasonCount());
            }
        } else if (highlighted.getChannelModel() != null) {
            Channel itemChannel = highlighted.getChannelModel();
            return itemChannel.getTitle();
        }
        return null;
    }

    public void onItemClick() {
        listener.onItemClick(highlighted);
    }

    public interface OnClickListener {

        void onItemClick(Highlighted item);
    }
}
