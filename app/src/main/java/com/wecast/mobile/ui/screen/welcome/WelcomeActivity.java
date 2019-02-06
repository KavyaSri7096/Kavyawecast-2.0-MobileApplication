package com.wecast.mobile.ui.screen.welcome;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.WeApp;
import com.wecast.core.data.db.entities.Language;
import com.wecast.core.data.repository.ComposerRepository;
import com.wecast.mobile.databinding.ActivityWelcomeBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.common.dialog.LanguageDialog;
import com.wecast.mobile.utils.BindingUtils;
import com.wecast.mobile.utils.LocaleUtils;

import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.Nullable;

/**
 * Created by ageech@live.com
 */

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding, WelcomeActivityViewModel> implements WelcomeActivityNavigator {

    @Inject
    WelcomeActivityViewModel viewModel;
    @Inject
    ComposerRepository composerRepository;

    private ActivityWelcomeBinding binding;

    public static void open(Context context) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    public WelcomeActivityViewModel getViewModel() {
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

        // Set app logo
        BindingUtils.bindAppLogo(binding.logo, composerRepository.getAppLogo());

        // Set selected language
        Language language = getPreferenceManager().getLanguage();
        binding.language.setText(language.getShortCode());

        // Show/hide registration button based on composer configuration
        if (!composerRepository.getAppModules().hasRegistration()) {
            binding.register.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        binding.language.setOnClickListener(v -> {
            LanguageDialog dialog = LanguageDialog.newInstance();
            dialog.setOnLanguageChangeListener(this::changeLanguage);
            dialog.show(getSupportFragmentManager(), LanguageDialog.TAG);
        });
        binding.login.setOnClickListener(v -> ScreenRouter.openLogin(this));
        binding.register.setOnClickListener(v -> ScreenRouter.openRegistration(this));
    }

    private void changeLanguage(Language language) {
        binding.language.setText(language.getShortCode());
        getPreferenceManager().setLanguage(language);
        // Setup language
        LocaleUtils.setLocale(new Locale(language.getShortCode()));
        LocaleUtils.updateConfig(WeApp.getInstance(), new Configuration());
        recreate();
    }
}
