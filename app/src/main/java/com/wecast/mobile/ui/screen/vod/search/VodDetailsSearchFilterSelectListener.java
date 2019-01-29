package com.wecast.mobile.ui.screen.vod.search;

import com.wecast.core.data.db.entities.ShowType;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public interface VodDetailsSearchFilterSelectListener {

    void onFiltersSelected(List<ShowType> filters);
}
