package com.wecast.mobile.ui.screen.live.guide;

import androidx.databinding.ObservableField;

import com.wecast.core.data.db.entities.TVGuide;
import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.utils.TVGuideUtils;

import java.util.Locale;

/**
 * Created by ageech@live.com
 */

public class TVGuideViewModel {

    public final ObservableField<String> title;
    public final ObservableField<String> logoUrl;
    public final ObservableField<String> time;
    public final ObservableField<Integer> max;
    public final ObservableField<Integer> progress;

    public final OnClickListener listener;

    private final TVGuide tvGuide;

    TVGuideViewModel(TVGuide tvGuide, OnClickListener listener) {
        this.tvGuide = tvGuide;
        this.listener = listener;

        // Set programme title
        this.title = new ObservableField<>(String.format(Locale.getDefault(), "%1$d - %2$s", tvGuide.getChannelNumber(), tvGuide.getTitle()));

        // Set programme logo
        this.logoUrl = new ObservableField<>(tvGuide.getLogo());

        // Set programme progress
        this.time = new ObservableField<>();
        this.max = new ObservableField<>(0);
        this.progress = new ObservableField<>(0);
        if (tvGuide.getProgrammes() != null && tvGuide.getProgrammes().size() > 0) {
            TVGuideProgramme programme = tvGuide.getProgrammes().get(0);
            if (programme != null) {
                this.time.set(String.format("%1$s - %2$s", TVGuideUtils.getStartEnd(programme), programme.getTitle()));
                this.max.set((TVGuideUtils.getMax(programme)));
                this.progress.set((TVGuideUtils.getProgress(programme)));
            }
        }
    }

    public void onItemClick() {
        listener.onItemClick(tvGuide);
    }

    public interface OnClickListener {

        void onItemClick(TVGuide item);
    }
}