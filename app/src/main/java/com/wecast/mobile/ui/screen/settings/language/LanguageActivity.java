package com.wecast.mobile.ui.screen.settings.language;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wecast.core.data.db.entities.Language;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.WeApp;
import com.wecast.mobile.databinding.ActivityLanguageBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.common.adapter.SingleChoiceAdapter;
import com.wecast.mobile.utils.LocaleUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class LanguageActivity extends BaseActivity<ActivityLanguageBinding, LanguageActivityViewModel> implements
        LanguageActivityNavigator, SingleChoiceAdapter.OnCheckListener<Language> {

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    LanguageActivityViewModel viewModel;

    private ActivityLanguageBinding binding;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_language;
    }

    @Override
    public LanguageActivityViewModel getViewModel() {
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
        setStatusTranslucent(false);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        // Set toolbar title
        binding.toolbar.title.setText(getText(R.string.language_title));

        // Create languages list
        Language english = new Language(1, getResources().getString(R.string.english), "en");
        Language spanish = new Language(2, getResources().getString(R.string.spanish), "es");
        Language brazilian = new Language(3, getResources().getString(R.string.brazilian), "pt");

        List<Language> items = new ArrayList<>();
        items.add(english);
        items.add(spanish);
        items.add(brazilian);

        // Setup recycler view for languages
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.languages.setLayoutManager(layoutManager);
        LanguageAdapter adapter = new LanguageAdapter(preferenceManager, items, this);
        binding.languages.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onItemChecked(Language item) {
        preferenceManager.setLanguage(item);
        changeLanguage();
    }

    private void changeLanguage() {
        String code = preferenceManager.getLanguage().getShortCode();
        Locale locale = new Locale(code);
        Configuration config = new Configuration();
        LocaleUtils.setLocale(locale);
        LocaleUtils.updateConfig(WeApp.getInstance(), config);
        // Refresh activity
        ScreenRouter.openNavigation(this);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}
