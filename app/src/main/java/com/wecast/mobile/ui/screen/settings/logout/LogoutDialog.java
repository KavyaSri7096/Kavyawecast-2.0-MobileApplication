package com.wecast.mobile.ui.screen.settings.logout;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.core.analytics.SocketManager;
import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.db.DatabaseManager;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.data.repository.ComposerRepository;
import com.wecast.core.utils.ReminderUtils;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogLogOutBinding;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.common.dialog.ExitDialog;
import com.wecast.mobile.utils.BindingUtils;
import com.wecast.core.utils.ViewUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class LogoutDialog extends BaseDialog {

    public static final String TAG = ExitDialog.class.getName();

    @Inject
    ComposerRepository composerRepository;
    @Inject
    AccountManager accountManager;
    @Inject
    SocketManager socketManager;
    @Inject
    PreferenceManager preferenceManager;
    @Inject
    DatabaseManager databaseManager;
    @Inject
    ReminderUtils reminderUtils;

    private DialogLogOutBinding binding;

    public static LogoutDialog newInstance() {
        return new LogoutDialog();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_log_out, container, false);
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
        binding.confirm.setOnClickListener(v -> logout());
        binding.cancel.setOnClickListener(v -> dismiss());
    }

    private void logout() {
        Disposable disposable = accountManager.logout()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.isTokenExpired()) {
                            refreshToken(this::logout);
                        } else if (response.isSuccessful()) {
                            clearData();
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void clearData() {
        socketManager.disconnect();
        preferenceManager.clear();
        databaseManager.clear();
        reminderUtils.removeCalendar();
        // Go to login screen
        openActivityOnTokenExpire();
        dismiss();
    }
}
