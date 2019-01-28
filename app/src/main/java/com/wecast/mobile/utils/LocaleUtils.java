package com.wecast.mobile.utils;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;

import com.wecast.mobile.WeApp;

import java.util.Locale;

/**
 * Created by ageech@live.com
 */

public final class LocaleUtils {

    private static Locale locale;

    public static void setLocale(Locale locale) {
        LocaleUtils.locale = locale;
        if (LocaleUtils.locale != null) {
            Locale.setDefault(LocaleUtils.locale);
        }
    }

    public static void updateConfig(ContextThemeWrapper wrapper) {
        if (locale != null) {
            Configuration configuration = new Configuration();
            configuration.setLocale(locale);
            wrapper.applyOverrideConfiguration(configuration);
        }
    }

    public static void updateConfig(WeApp app, Configuration configuration) {
        if (locale != null) {
            // Wrapping the configuration to avoid Activity endless loop
            Configuration config = new Configuration(configuration);
            // Use the now-deprecated config.locale and res.updateConfiguration here,
            // because the replacements aren't available till API level 24 and 17 respectively.
            config.locale = locale;
            Resources res = app.getBaseContext().getResources();
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
    }
}