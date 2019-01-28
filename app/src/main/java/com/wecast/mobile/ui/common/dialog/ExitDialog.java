package com.wecast.mobile.ui.common.dialog;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.core.data.repository.ComposerRepository;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogExitBinding;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.utils.BindingUtils;
import com.wecast.core.utils.ViewUtils;

import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class ExitDialog extends BaseDialog {

    public static final String TAG = ExitDialog.class.getName();

    @Inject
    ComposerRepository composerRepository;

    private DialogExitBinding binding;

    public static ExitDialog newInstance() {
        return new ExitDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ViewUtils.dpToPx(getResources().getBoolean(R.bool.isPhone) ? 300 : 600);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_exit, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        BindingUtils.bindAppLogo(binding.logo, composerRepository.getAppLogo());
    }

    private void setupListeners() {
        binding.confirm.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finishAffinity();
            }
        });
        binding.cancel.setOnClickListener(v -> dismiss());
    }
}
