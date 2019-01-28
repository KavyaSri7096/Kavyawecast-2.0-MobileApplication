package com.wecast.mobile.ui.screen.registration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.wecast.core.data.api.model.ErrorData;
import com.wecast.core.data.db.entities.Subscription;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityRegistrationBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class RegistrationActivity extends BaseActivity<ActivityRegistrationBinding, RegistrationActivityViewModel> implements RegistrationActivityNavigator {

    @Inject
    RegistrationActivityViewModel viewModel;

    private ActivityRegistrationBinding binding;
    private List<Subscription> subscriptionList;

    public static void open(Context context) {
        Intent intent = new Intent(context, RegistrationActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_registration;
    }

    @Override
    public RegistrationActivityViewModel getViewModel() {
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

        fetchSubscriptions();
    }

    private void fetchSubscriptions() {
        Disposable disposable = viewModel.getSubscriptions()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.isTokenExpired()) {
                            refreshToken(this::fetchSubscriptions);
                        } else if (response.isSuccessful()) {
                            setupSubscriptionSpinner(response.getData());
                        } else {
                            toast(response.getMessage());
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void setupSubscriptionSpinner(List<Subscription> data) {
        subscriptionList = data;

        List<String> spinnerArray = new ArrayList<>();
        for (Subscription subscription : subscriptionList) {
            spinnerArray.add(CommonUtils.firstCapital(subscription.getName()));
        }

        if (subscriptionList != null && subscriptionList.size() > 0) {
            ArrayAdapter<String> subAdapter = new ArrayAdapter<>(this, R.layout.component_spinner, spinnerArray);
            subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.subscription.setAdapter(subAdapter);
        }

        setupGenderSpinner();
    }

    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, R.layout.component_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.gender.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.register.setOnClickListener(v -> register());
        binding.back.setOnClickListener(v -> finish());
    }

    private void register() {
        String username = binding.username.getText().toString();
        String firstName = binding.firstName.getText().toString();
        String lastName = binding.lastName.getText().toString();
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        String age = binding.age.getText().toString();
        String pin = binding.pin.getText().toString();
        String purchasePin = binding.purchasePin.getText().toString();
        int gender = binding.gender.getSelectedItemPosition();
        String subscriptionId = subscriptionList.get(binding.subscription.getSelectedItemPosition()).getId();

        hideKeyboard();

        Disposable disposable = viewModel.register(username, firstName, lastName, email, password, age, pin, purchasePin, gender, subscriptionId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isSuccessful()) {
                        onSuccess();
                    } else {
                        onError(response.getData());
                    }
                }, throwable -> onSuccess());
        subscribe(disposable);
    }

    private void onSuccess() {
        toast(R.string.message_account_created);

        // Go to login screen
        ScreenRouter.openLogin(this);
        finish();
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
}
