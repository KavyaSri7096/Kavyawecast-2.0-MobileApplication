package com.wecast.mobile.ui.screen.settings.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wecast.core.data.api.model.ErrorData;
import com.wecast.core.data.db.entities.Authentication;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityEditProfileBinding;
import com.wecast.mobile.ui.base.BaseActivity;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class EditProfileActivity extends BaseActivity<ActivityEditProfileBinding, EditProfileActivityViewModel> implements EditProfileActivityNavigator {

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    EditProfileActivityViewModel viewModel;

    private ActivityEditProfileBinding binding;
    private boolean isMaster;

    public static void open(Context context) {
        Intent intent = new Intent(context, EditProfileActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    public EditProfileActivityViewModel getViewModel() {
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

        // Set toolbar
        setSupportActionBar(binding.toolbar.toolbar);

        // Set toolbar title
        binding.toolbar.title.setText(getString(R.string.edit_info));

        // Hide purchase pin edit text for non master account
        Authentication authentication = preferenceManager.getAuthentication();
        isMaster = authentication.getProfile().isMaster();
        binding.purchaseRoot.setVisibility(isMaster ? View.VISIBLE : View.GONE);

        // Set focus on current password exit text
        binding.etCurrentPassword.requestFocus();
    }

    private void setupListeners() {
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_confirm:
                validatePassword();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void validatePassword() {
        String curPassword = binding.etCurrentPassword.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString().trim();
        String confPassword = binding.etConfirmPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(curPassword)) {
            if (TextUtils.isEmpty(newPassword)) {
                toast(R.string.enter_new_password);
                return;
            }
            if (TextUtils.isEmpty(confPassword)) {
                toast(R.string.confirm_new_password);
                return;
            }
            if (!(newPassword.equals(confPassword))) {
                toast(R.string.passwords_not_match);
                return;
            }
        }

        updateInfo();
    }

    private void updateInfo() {
        String email = binding.etEmail.getText().toString().trim();
        String firstName = binding.etFirstName.getText().toString().trim();
        String lastName = binding.etLastName.getText().toString().trim();
        String curPassword = TextUtils.isEmpty(binding.etCurrentPassword.getText().toString().trim()) ? null : binding.etCurrentPassword.getText().toString().trim();
        String newPassword = TextUtils.isEmpty(binding.etNewPassword.getText().toString().trim()) ? null : binding.etConfirmPassword.getText().toString().trim();
        String purchasePin = isMaster ? binding.etPurchasePin.getText().toString().trim() : null;
        String pin = binding.etPin.getText().toString().trim();

        Disposable disposable = viewModel.updateInfo(email, firstName, lastName, curPassword, newPassword, newPassword, purchasePin, pin)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        onResponse(null);
                    } else if (response.isTokenExpired()) {
                        refreshToken(this::updateInfo);
                    } else {
                        onError(response.getData());
                    }
                }, this::onResponse);
        subscribe(disposable);
    }

    private void onResponse(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }

        // Notify user that profile is updated
        toast(R.string.message_profile_updated);

        // Update cached authentication model
        Authentication authentication = preferenceManager.getAuthentication();
        if (authentication != null) {
            authentication.getProfile().setEmail(binding.etEmail.getText().toString().trim());
            authentication.getProfile().setFirstName(binding.etFirstName.getText().toString().trim());
            authentication.getProfile().setLastName(binding.etLastName.getText().toString().trim());
            authentication.getAccount().setPurchasePin(binding.etPurchasePin.getText().toString().trim());
            authentication.getProfile().setPin(binding.etPin.getText().toString().trim());
            preferenceManager.setAuthentication(authentication);
        }

        // Close edit info screen
        onBackPressed();
    }

    private void onError(ErrorData errorData) {
        String message = "";
        if (errorData.getUsername() != null && errorData.getUsername().size() > 0) {
            message = "Username : " + errorData.getUsername().get(0);
        } else if (errorData.getFirstName() != null && errorData.getFirstName().size() > 0) {
            message = "First Name : " + errorData.getFirstName().get(0);
        } else if (errorData.getLastName() != null && errorData.getLastName().size() > 0) {
            message = "Last Name : " + errorData.getLastName().get(0);
        } else if (errorData.getEmail() != null && errorData.getEmail().size() > 0) {
            message = "Email : " + errorData.getEmail().get(0);
        } else if (errorData.getPassword() != null && errorData.getPassword().size() > 0) {
            message = "Password : " + errorData.getPassword().get(0);
        } else if (errorData.getPin() != null && errorData.getPin().size() > 0) {
            message = "PIN : " + errorData.getPin().get(0);
        } else if (errorData.getPurchasePin() != null && errorData.getPurchasePin().size() > 0) {
            message = "Purchase PIN : " + errorData.getPurchasePin().get(0);
        } else if (errorData.getAge() != null && errorData.getAge().size() > 0) {
            message = "Age : " + errorData.getAge().get(0);
        }
        toast(message);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}
