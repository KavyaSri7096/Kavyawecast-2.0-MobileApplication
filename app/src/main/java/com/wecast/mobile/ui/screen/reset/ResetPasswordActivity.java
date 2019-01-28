package com.wecast.mobile.ui.screen.reset;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityResetPasswordBinding;
import com.wecast.mobile.ui.base.BaseActivity;

import javax.inject.Inject;

import androidx.annotation.Nullable;

/**
 * Created by ageech@live.com
 */

public class ResetPasswordActivity extends BaseActivity<ActivityResetPasswordBinding, ResetPasswordViewModel> implements  ResetPasswordNavigator {

    @Inject
    ResetPasswordViewModel viewModel;

    private ActivityResetPasswordBinding binding;

    public static void open(Context context) {
        Intent intent = new Intent(context, ResetPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_reset_password;
    }

    @Override
    public ResetPasswordViewModel getViewModel() {
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
    }

    private void setupListeners() {
        binding.back.setOnClickListener(v -> finish());
        binding.reset.setOnClickListener(v -> reset());
    }

    private void reset() {
        if (!isNetworkConnected()) {
            toast(R.string.error_no_internet_connection);
            return;
        }

        String email = binding.email.getText().toString();

        hideKeyboard();

        if (TextUtils.isEmpty(email)) {
            toast(R.string.error_email_can_not_be_empty);
            return;
        }

        viewModel.doReset(email);
    }

    @Override
    public void onSuccess() {
        toast(R.string.message_password_reset_successful);
        finish();
    }

    @Override
    public void onError(String message) {
        toast(message);
    }
}
