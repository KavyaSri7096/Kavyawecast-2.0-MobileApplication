package com.wecast.mobile.ui.screen.vod.details;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.core.data.api.manager.VodManager;
import com.wecast.core.data.db.dao.VodDao;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.entities.VodSourceProfile;
import com.wecast.core.data.db.entities.VodSourceProfilePricing;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogVodRentPreviewBinding;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.utils.BindingUtils;
import com.wecast.mobile.utils.CommonUtils;
import com.wecast.core.utils.ViewUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class VodDetailsRentPreviewDialog extends BaseDialog {

    public static final String TAG = VodDetailsRentPreviewDialog.class.getName();

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    VodDao vodDao;
    @Inject
    VodManager vodManager;

    private DialogVodRentPreviewBinding binding;
    private Vod vod;
    private VodSourceProfile profile;
    private VodSourceProfilePricing pricing;

    public static VodDetailsRentPreviewDialog newInstance(Vod vod, VodSourceProfile profile, VodSourceProfilePricing pricing) {
        VodDetailsRentPreviewDialog dialog = new VodDetailsRentPreviewDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("ID", vod.getId());
        bundle.putInt("PROFILE_ID", profile.getId());
        bundle.putString("PRICING_DATE", pricing.getAvailableDate());
        dialog.setArguments(bundle);
        return dialog;
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_vod_rent_preview, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int vodId = bundle.getInt("ID");
            int profileId = bundle.getInt("PROFILE_ID");
            String pricingDate = bundle.getString("PRICING_DATE");
            vod = vodDao.getById(vodId);
            profile = vodDao.getProfile(vodId, profileId);
            pricing = vodDao.getPricing(vodId, profileId, pricingDate);
        }

        if (vod == null || profile == null || pricing == null) {
            return;
        }

        // Set cover
        if (vod.getCover() != null) {
            BindingUtils.bindCover(binding.cover, vod.getCover().getPreviewAr());
        } else {
            binding.cover.setVisibility(View.GONE);
        }

        // Set title
        if (!TextUtils.isEmpty(vod.getTitle())) {
            binding.title.setText(vod.getTitle());
        } else {
            binding.title.setVisibility(View.GONE);
        }

        // Set year
        if (!TextUtils.isEmpty(vod.getYear())) {
            binding.info.setText(vod.getYear());
        }

        // Set vod duration in HH:mm format
        if (!TextUtils.isEmpty(vod.getRuntime())) {
            binding.info.setText(binding.info.getText() + " | " + CommonUtils.getRuntime(vod.getRuntime()));
        }

        // Set rating
        if (!TextUtils.isEmpty(vod.getRating())) {
            binding.info.setText(binding.info.getText() + " | " + vod.getRating() + "/10");
        }

        // Set selected profile name
        if (profile.getName() != null) {
            binding.profile.setText(profile.getName());
        } else {
            binding.profile.setVisibility(View.GONE);
            binding.profileDivider.setVisibility(View.GONE);
        }

        // Set selected pricing name
        if (pricing.getName() != null) {
            binding.pricing.setText(Html.fromHtml(pricing.getName()));
        } else {
            binding.pricing.setVisibility(View.GONE);
            binding.pricingDivider.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        binding.confirm.setOnClickListener(v -> rentVod());
        binding.cancel.setOnClickListener(v -> dismiss());
    }

    private void rentVod() {
        // Vod, profile or pricing not provided
        if (vod == null || profile == null || pricing == null) {
            return;
        }

        String enteredPIN = binding.pin.getText().toString().trim();
        String currentPIN = preferenceManager.getPurchasePIN();

        // Wrong pin entered
        if (!enteredPIN.equals(currentPIN)) {
            toast(R.string.message_wrong_pin);
            return;
        }

        // Rent selected vod
        Disposable disposable = vodManager.rent(vod.getId(), profile.getId(), pricing.getDuration())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isTokenExpired()) {
                        refreshToken(this::rentVod);
                    } else if (response.isSuccessful()) {
                        Vod vod = response.getData();
                        vodDao.insert(vod);
                        toast(getBaseActivity().getString(R.string.message_content_rented, vod.getTitle()));
                        refreshUI();
                    } else {
                        toast(response.getMessage());
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void refreshUI() {
        if (getActivity() != null) {
            getActivity().recreate();
            dismiss();
        }
    }
}
