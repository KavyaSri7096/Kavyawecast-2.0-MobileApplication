package com.wecast.mobile.ui.screen.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.wecast.core.Constants;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.core.data.repository.ComposerRepository;
import com.wecast.mobile.databinding.ActivityLoginBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.utils.PermissionUtils;

import javax.inject.Inject;

import androidx.annotation.NonNull;

/**
 * Created by ageech@live.com
 */

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginActivityViewModel> implements LoginActivityNavigator {

    @Inject
    LoginActivityViewModel viewModel;
    @Inject
    ComposerRepository composerRepository;
    @Inject
    PermissionUtils permissionUtils;

    private ActivityLoginBinding binding;

    public static void open(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public LoginActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        setStatusTransparent(this);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        // Request for READ/WRITE calendar permission
        String[] permissions = new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};
        permissionUtils.request(this, permissions, 1, new PermissionUtils.PermissionListener() {
            @Override
            public void onAllowed() {
                // Do nothing.
            }

            @Override
            public void onDeclined() {
                toast(R.string.message_calendar_permission_denied);
            }
        });
    }

    private void setupListeners() {
        binding.back.setOnClickListener(v -> onBackPressed());
        binding.login.setOnClickListener(view -> login());
        binding.reset.setOnClickListener(view -> ScreenRouter.openResetPassword(this));
    }

    private void login() {
        if (!isNetworkConnected()) {
            toast(R.string.error_no_internet_connection);
            return;
        }

        String email = binding.username.getText().toString();
        String password = binding.password.getText().toString();

        hideKeyboard();

        if (TextUtils.isEmpty(email)) {
            toast(R.string.error_email_can_not_be_empty);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            toast(R.string.error_password_can_not_be_empty);
            return;
        }

        if (password.length() < Constants.MIN_LENGTH_PASSWORD) {
            toast(String.format(getApplication().getString(R.string.error_password_min_length), Constants.MIN_LENGTH_PASSWORD));
            return;
        }

        viewModel.doLogin(email, password);
    }

    @Override
    public void openSplashActivity() {
        ScreenRouter.openSplash(this);
        finish();
    }

    @Override
    public void onError(String message) {
        toast(message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        ScreenRouter.openWelcome(this);
    }
}