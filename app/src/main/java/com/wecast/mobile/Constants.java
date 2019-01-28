package com.wecast.mobile;

/**
 * Created by ageech@live.com
 */

public interface Constants {

    String BASE_API_URL = BuildConfig.API_URL;

    String DB_NAME = "wecast.db";

    // Reminders sync timeout (15 minutes)
    long REMINDERS_SYNC_TIMEOUT = 15 * 60 & 1000;

    // Advertisement banner config for Google ADS
    String ADMOB_APP_ID = "ca-app-pub-3940256099942544/6300978111";
}
