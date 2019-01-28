package com.wecast.mobile.ui.common.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.shawnlin.numberpicker.NumberPicker;
import com.wecast.core.data.db.entities.Language;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogLanguageBinding;
import com.wecast.mobile.ui.base.BaseDialog;

import java.lang.reflect.Field;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

/**
 * Created by ageech@live.com
 */

public class LanguageDialog extends BaseDialog {

    public static final String TAG = LanguageDialog.class.getName();

    @Inject
    PreferenceManager preferenceManager;

    private DialogLanguageBinding binding;
    private OnLanguageChangeListener onLanguageChangeListener;

    public static LanguageDialog newInstance() {
        return new LanguageDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_language, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        // Set language picker
        binding.languages.setMinValue(0);
        binding.languages.setMaxValue(2);
        String[] languages = getResources().getStringArray(R.array.languages);
        binding.languages.setDisplayedValues(languages);

        // Remove divider from language picker
        try {
            Field field = NumberPicker.class.getDeclaredField("mSelectionDivider");
            field.setAccessible(true);
            ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
            field.set(binding.languages, colorDrawable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Select current language
        for (int i = 0; i < languages.length; i++) {
            String language = languages[i];
            if (preferenceManager.getLanguage().getName().equals(language)) {
                binding.languages.setValue(i);
                break;
            }
        }
    }

    private void setupListeners() {
        binding.confirm.setOnClickListener(v -> setLanguage());
    }

    private void setLanguage() {
        String[] languages = getResources().getStringArray(R.array.languages);
        int position = binding.languages.getValue();

        String shortCode;
        switch (position) {
            case 0:
                shortCode = "en";
                break;
            case 1:
                shortCode = "es";
                break;
            case 2:
                shortCode = "pt";
                break;
            default:
                shortCode = "en";
                break;
        }

        // Save selected language to shared preferences
        Language language = new Language(position, languages[position], shortCode);
        if (onLanguageChangeListener != null) {
            onLanguageChangeListener.onLanguageChanged(language);
            dismiss();
        }
    }

    public void setOnLanguageChangeListener(OnLanguageChangeListener onLanguageChangeListener) {
        this.onLanguageChangeListener = onLanguageChangeListener;
    }

    public interface OnLanguageChangeListener {

        void onLanguageChanged(Language language);
    }
}
