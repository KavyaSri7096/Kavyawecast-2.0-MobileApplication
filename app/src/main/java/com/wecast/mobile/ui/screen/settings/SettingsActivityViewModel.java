package com.wecast.mobile.ui.screen.settings;

import android.view.View;

import com.wecast.core.data.api.ResponseWrapper;
import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.api.model.ResponseModel;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.mobile.BuildConfig;
import com.wecast.core.data.db.entities.Authentication;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.ui.base.BaseViewModel;
import com.wecast.mobile.utils.ThemeUtils;

import io.reactivex.Observable;

/**
 * Created by ageech@live.com
 */

public class SettingsActivityViewModel extends BaseViewModel<SettingsActivityNavigator> {

    private final PreferenceManager preferenceManager;
    private final BannerRepository bannerRepository;
    private final AccountManager accountManager;

    // User login data
    private Authentication authentication;

    SettingsActivityViewModel(PreferenceManager preferenceManager, BannerRepository bannerRepository, AccountManager accountManager) {
        this.preferenceManager = preferenceManager;
        this.bannerRepository = bannerRepository;
        this.accountManager = accountManager;

        // Set user authentication
        this.authentication = preferenceManager.getAuthentication();
    }

    Observable<ResponseWrapper<Banner>> getBanner(String boxPosition) {
        return bannerRepository.getBanner(boxPosition);
    }

    Observable<ResponseModel<Authentication>> checkSubscription() {
        return accountManager.checkSubscription();
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public String getFullName() {
        if (authentication != null && authentication.getProfile() != null) {
            return authentication.getProfile().getFirstName() + " " + authentication.getProfile().getLastName();
        }
        return null;
    }

    public String getEmail() {
        if (authentication != null && authentication.getProfile() != null) {
            return authentication.getProfile().getEmail();
        } else {
            return null;
        }
    }

    public String getAge() {
        if (authentication != null && authentication.getProfile() != null) {
            return authentication.getProfile().getAge();
        } else {
            return null;
        }
    }

    public String getGender() {
        if (authentication != null && authentication.getProfile() != null) {
            return authentication.getProfile().getSex() ? "Female" : "Male";
        } else {
            return null;
        }
    }

    public String getCountry() {
        if (authentication != null && authentication.getDevice() != null) {
            return authentication.getDevice().getCountryName();
        } else {
            return null;
        }
    }

    public String getCity() {
        if (authentication != null && authentication.getDevice() != null) {
            return authentication.getDevice().getCity();
        } else {
            return null;
        }
    }

    public String getLanguage() {
        return preferenceManager.getLanguage().getName();
    }

    public String getVideoQuality() {
        return preferenceManager.getVideoQuality().getName();
    }

    public boolean getDebug() {
        return preferenceManager.getDebug();
    }

    public boolean is24hTimeFormat() {
        return preferenceManager.is24hTimeFormat();
    }

    public boolean getTheme() {
        return preferenceManager.getTheme() != ThemeUtils.THEME_MARBLE;
    }

    public boolean getRTL() {
        return preferenceManager.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public String getVersion() {
        return "v" + BuildConfig.VERSION_NAME;
    }
}
