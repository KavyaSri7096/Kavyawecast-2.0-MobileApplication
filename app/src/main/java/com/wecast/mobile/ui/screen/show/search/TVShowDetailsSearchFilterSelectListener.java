package com.wecast.mobile.ui.screen.show.search;

import com.wecast.core.data.db.entities.ShowType;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public interface TVShowDetailsSearchFilterSelectListener {

    void onFiltersSelected(List<ShowType> filters);
}
