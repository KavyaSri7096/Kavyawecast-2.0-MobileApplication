package com.wecast.mobile.utils;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;

import com.wecast.mobile.WeApp;

import java.util.Locale;

/**
 * Created by ageech@live.com
 */

public final class LocaleUtils {

    private static Locale locale;
    private static LocaleUtils INSTANCE;
    private final SharedPreferences mSharedPreferences;


    private LocaleUtils() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeApp.getInstance().getApplicationContext());
    }


    public static LocaleUtils getInstance() {
        if (INSTANCE == null)
            INSTANCE = new LocaleUtils();
        return INSTANCE;
    }

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

    public void persist(String key, Object object) {
        if (object == null) {
            mSharedPreferences.edit().remove(key).apply();
        } else if (object instanceof String) {
            mSharedPreferences.edit().putString(key, (String) object).apply();
        } else if (object instanceof Boolean) {
            mSharedPreferences.edit().putBoolean(key, (Boolean) object).apply();
        } else if (object instanceof Integer) {
            mSharedPreferences.edit().putInt(key, (Integer) object).apply();
        }
    }


    public String getString(String key) {
        return mSharedPreferences.getString(key, null);
    }

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }
}