package com.wecast.mobile.ui.screen.live.channel.details.progamme;

import android.content.Context;
import androidx.recyclerview.widget.LinearSmoothScroller;

/**
 * Created by ageech@live.com
 */

public class ProgrammeSmoothScroller extends LinearSmoothScroller {

    ProgrammeSmoothScroller(Context context) {
        super(context);
    }

    @Override
    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
        return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
    }
}