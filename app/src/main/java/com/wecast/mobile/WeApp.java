package com.wecast.mobile;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.wecast.core.logger.Logger;
import com.wecast.core.WeCore;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.di.component.AppComponent;
import com.wecast.mobile.di.wrapper.AppComponentWrapper;
import com.wecast.mobile.utils.CommonUtils;
import com.wecast.mobile.utils.LocaleUtils;

import java.util.Locale;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by ageech@live.com
 */

public class WeApp extends Application implements HasActivityInjector, HasSupportFragmentInjector, HasServiceInjector {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject
    DispatchingAndroidInjector<Service> serviceInjector;
    @Inject
    PreferenceManager preferenceManager;

    public static boolean SUBSCRIPTION_EXPIRED = false;
    private AppComponent appComponent;
    private static WeApp weApp;

    @Override
    public void onCreate() {
        super.onCreate();

        weApp = this;

        // Initialize leak canary
        //if (LeakCanary.isInAnalyzerProcess(this)) {
        //    return;
        //}
        //LeakCanary.install(this);

        // Setup dependency injection
        appComponent = AppComponentWrapper.getAppComponent(this);
        appComponent.inject(this);

        // Initialize Fabric to track incidents
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCrashlyticsCollectionEnabled(true);
//        Fabric.with(this, new Crashlytics());

        // Initialize ADMob
        MobileAds.initialize(this, Constants.ADMOB_APP_ID);

        // Initialize app logger
        Logger.init();

        // Initialize api client
        WeCore.init(Constants.BASE_API_URL, CommonUtils.getDeviceType(weApp), BuildConfig.VERSION_NAME);

        // Initialize database
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .name(Constants.DB_NAME)
                .deleteRealmIfMigrationNeeded()
                .build());

        // Setup default font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/helvetica_regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());

        // Set default app language
        setupLanguage();



    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return serviceInjector;
    }

    public static WeApp getInstance() {
        return weApp;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    /**
     * Get default language from shared preferences
     */

    private void setupLanguage() {
        // Setup language
        String code = preferenceManager.getLanguage().getShortCode();
        LocaleUtils.setLocale(new Locale(code));
        LocaleUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());

//                Log.e("M3h", "ERROR ERROR ERROR");
//        throw new RuntimeException("Test Crash"); // Force a crash
    }
}
