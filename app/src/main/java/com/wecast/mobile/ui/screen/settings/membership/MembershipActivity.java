package com.wecast.mobile.ui.screen.settings.membership;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.wecast.core.data.db.entities.Authentication;
import com.wecast.core.data.db.entities.PaymentHistory;
import com.wecast.core.data.db.entities.Subscription;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityMembershipBinding;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.widget.listRow.ListRowAdapter;
import com.wecast.mobile.ui.widget.listRow.ListRowType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class MembershipActivity extends BaseActivity<ActivityMembershipBinding, MembershipActivityViewModel> implements MembershipActivityNavigator {

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    MembershipActivityViewModel viewModel;

    private SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private ActivityMembershipBinding binding;
    private ListRowAdapter adapter;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_membership;
    }

    @Override
    public MembershipActivityViewModel getViewModel() {
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

        // Setup toolbar title
        binding.toolbar.title.setText(getString(R.string.membership_title));

        // Get subscription info from server
        getSubscriptionInfo();

        // Set recycler view for payment history
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.paymentHistory.setLayoutManager(layoutManager);
        adapter = new ListRowAdapter(this, ListRowType.PAYMENT_HISTORY);
        binding.paymentHistory.setAdapter(adapter);

        // Get payment history from server
        getPaymentHistory(1);
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
        Subscription subscription = preferenceManager.getAuthentication().getSubscription();
        // Set current subscription name
        binding.currentProfile.setText(subscription.getName());
        // Subscription expiration date is not set
        if (authentication.getAccount().getExpire() == null) {
            binding.expireDate.setText(getString(R.string.unlimited));
        } else {
            // Check if subscription is valid
            Date expirationDate;
            try {
                expirationDate = parser.parse(authentication.getAccount().getExpire());
                if (authentication.getAccount().isSubscriptionExpired()) {
                    showSubscriptionExpired(getString(R.string.expired));
                } else {
                    binding.expireDate.setText(format.format(expirationDate));
                    binding.expireDate.setTextColor(getResources().getColor(getColorTextActive()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
                showSubscriptionExpired(getResources().getString(R.string.no_data));
            }
        }
    }

    private void showSubscriptionExpired(String message) {
        binding.expireDate.setText(message);
        binding.expireDate.setTextColor(Color.RED);
    }

    private int getColorTextActive() {
        int[] attrs = {R.attr.colorTextActive};
        TypedArray ta = obtainStyledAttributes(attrs);
        int color = ta.getResourceId(0, android.R.color.black);
        ta.recycle();
        return color;
    }

    private void setupListeners() {
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
    }

    private void getPaymentHistory(int page) {
        Disposable disposable = viewModel.getPaymentHistory(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.isSuccessful()) {
                            showData(response.getData().getItems());
                        } else if (response.isTokenExpired()) {
                            refreshToken(() -> getPaymentHistory(page));
                        } else if (response.isSubscriptionExpired()) {
                            showData(response.getData().getItems());
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void showData(List<PaymentHistory> data) {
        if (data == null || data.size() == 0) {
            binding.paymentHistoryTitle.setVisibility(View.GONE);
            return;
        }

        adapter.addAll(data);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }
}
