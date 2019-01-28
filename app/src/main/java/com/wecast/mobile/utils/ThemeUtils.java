package com.wecast.mobile.utils;

import com.wecast.mobile.R;

/**
 * Created by ageech@live.com
 */

public final class ThemeUtils {

    public static final int THEME_MARBLE = 0;
    public static final int THEME_PRISM = 1;

    public static int getThemeId(int themeId) {
        int id;
        switch (themeId) {
            case THEME_MARBLE:
                id = R.style.AppTheme_Marble;
                break;
            case THEME_PRISM:
                id = R.style.AppTheme_Prism;
                break;
            default:
                id = R.style.AppTheme_Marble;
                break;
        }
        return id;
    }
}
