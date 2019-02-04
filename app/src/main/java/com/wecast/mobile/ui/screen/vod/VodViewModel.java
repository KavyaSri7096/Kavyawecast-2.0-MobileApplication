package com.wecast.mobile.ui.screen.vod;

import androidx.databinding.ObservableField;

import com.wecast.core.data.db.entities.Vod;

/**
 * Created by ageech@live.com
 */

public class VodViewModel {

    public final ObservableField<String> title;
    public final ObservableField<String> coverUrl;
    public final ObservableField<String> bannerUrl;

    private final Vod vod;

    public VodViewModel(Vod vod) {
        this.vod = vod;

        // Set title
        this.title = new ObservableField<>(vod.getTitle());

        // Set cover url
        this.coverUrl = new ObservableField<>(vod.getCover().getPreviewAr());

        // Set banner url
        this.bannerUrl = new ObservableField<>();
        if (vod.getBanners() != null && vod.getBanners().size() > 0) {
            this.bannerUrl.set(vod.getBanners().get(0).getPreviewAr());
        }
    }
}
