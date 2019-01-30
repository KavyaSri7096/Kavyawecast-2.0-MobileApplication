package com.wecast.mobile.ui.screen.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.wecast.core.data.db.entities.Authentication;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.BuildConfig;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivitySettingsBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.utils.ThemeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding, SettingsActivityViewModel> implements SettingsActivityNavigator {

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    SettingsActivityViewModel viewModel;

    private SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat format = new SimpleDateFormat("MMMM dd'TH' yyyy", Locale.getDefault());

    private ActivitySettingsBinding binding;

    public static void open(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public SettingsActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        setStatusTransparent(this);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        // Set title
        binding.toolbar.title.setText(getString(R.string.account));

        // Night mode is only available in debug mode
        binding.theme.root.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        binding.rtl.root.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);

        getSubscriptionInfo();
    }

    private void setupListeners() {
        binding.toolbar.back.setOnClickListener(v -> finish());
        binding.name.setOnClickListener(view -> ScreenRouter.openEditInfo(this));
        binding.subscription.root.setOnClickListener(view -> ScreenRouter.openMembership(this));
        binding.language.root.setOnClickListener(view -> ScreenRouter.openLanguage(this));
        binding.quality.root.setOnClickListener(view -> ScreenRouter.openVideoQuality(this));
        binding.buffer.root.setOnClickListener(view -> ScreenRouter.openBuffer(this));
        binding.debug.switcher.setOnCheckedChangeListener((compoundButton, checked) -> getPreferenceManager().setDebug(checked));
        binding.theme.switcher.setOnCheckedChangeListener((compoundButton, checked) -> switchTheme());
        binding.rtl.switcher.setOnCheckedChangeListener((compoundButton, checked) -> switchLayoutDirection());
        binding.logOut.setOnClickListener(view -> ScreenRouter.openLogout(this));
    }

    private void switchTheme() {
        if (getPreferenceManager().getTheme() == ThemeUtils.THEME_MARBLE) {
            getPreferenceManager().setTheme(ThemeUtils.THEME_PRISM);
        } else {
            getPreferenceManager().setTheme(ThemeUtils.THEME_MARBLE);
        }
        ScreenRouter.openNavigation(this);
        finishAffinity();
    }

    private void switchLayoutDirection() {
        if (preferenceManager.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            preferenceManager.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        } else {
            preferenceManager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        ScreenRouter.openNavigation(this);
        finishAffinity();
    }

    private void getSubscriptionInfo() {
        Disposable disposable = viewModel.checkSubscription()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.isSuccessful()) {
                            showSubscriptionInfo(response.getData());
                        } else if (response.isTokenExpired()) {
                            refreshToken(this::getSubscriptionInfo);
                        } else {
                            toast(response.getMessage());
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void showSubscriptionInfo(Authentication authentication) {
        // Subscription expiration date is not set
        if (authentication.getAccount().getExpire() == null) {
            binding.subscription.setSubtitle(getString(R.string.unlimited));
            return;
        }
        // Check if subscription is valid
        Date expirationDate;
        try {
            expirationDate = parser.parse(authentication.getAccount().getExpire());
            long current = System.currentTimeMillis();
            long expiration = expirationDate.getTime();
            if (current <= expiration) {
                binding.subscription.setSubtitle(String.format(getString(R.string.valid_until), format.format(expirationDate)));
                binding.subscription.subtitle.setTextColor(getResources().getColor(getColorTextActive()));
            } else {
                showSubscriptionExpired(String.format(getString(R.string.expired_on), format.format(expirationDate)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            showSubscriptionExpired(getResources().getString(R.string.no_data));
        }
    }

    private void showSubscriptionExpired(String message) {
        binding.subscription.setSubtitle(message);
        binding.subscription.subtitle.setTextColor(Color.RED);
    }

    private int getColorTextActive() {
        int[] attrs = {R.attr.colorTextActive};
        TypedArray ta = obtainStyledAttributes(attrs);
        int color = ta.getResourceId(0, android.R.color.black);
        ta.recycle();
        return color;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            // Profile info updated
            Authentication authentication = preferenceManager.getAuthentication();
            viewModel.setAuthentication(authentication);
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        } else if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            // Language changed
            String language = preferenceManager.getLanguage().getName();
            binding.language.subtitle.setText(language);
        } else if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            // Video quality changed
            String quality = preferenceManager.getVideoQuality().getName();
            binding.quality.subtitle.setText(quality);
        }
    }
}
