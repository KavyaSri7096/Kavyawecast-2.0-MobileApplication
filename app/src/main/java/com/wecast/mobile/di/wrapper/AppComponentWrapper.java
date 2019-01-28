package com.wecast.mobile.di.wrapper;

import android.app.Application;

import com.wecast.core.di.component.CoreComponent;
import com.wecast.core.di.wrapper.CoreComponentWrapper;
import com.wecast.mobile.di.component.AppComponent;
import com.wecast.mobile.di.component.DaggerAppComponent;

/**
 * Created by ageech@live.com
 */

public class AppComponentWrapper {

    private static AppComponentWrapper componentWrapper;

    private static AppComponentWrapper getInstance(Application application) {
        if (componentWrapper == null) {
            synchronized (AppComponentWrapper.class) {
                if (componentWrapper == null) {
                    componentWrapper = new AppComponentWrapper();
                    CoreComponent component = CoreComponentWrapper.getBaseComponent(application);
                    componentWrapper.initializeComponent(component);
                }
            }
        }
        return componentWrapper;
    }

    private AppComponent component;

    public static AppComponent getAppComponent(Application application) {
        AppComponentWrapper appComponentWrapper = getInstance(application);
        return appComponentWrapper.component;
    }

    private void initializeComponent(CoreComponent coreComponent) {
        component = DaggerAppComponent.builder()
                .coreComponent(coreComponent)
                .build();
    }
}
