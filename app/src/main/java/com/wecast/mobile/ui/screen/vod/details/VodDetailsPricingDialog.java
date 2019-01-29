package com.wecast.mobile.ui.screen.vod.details;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.core.data.db.dao.VodDao;
import com.wecast.core.data.db.entities.Subscription;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.entities.VodSourceProfile;
import com.wecast.core.data.db.entities.VodSourceProfilePricing;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogVodRentPricingBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.common.adapter.SingleChoiceAdapter;
import com.wecast.mobile.ui.screen.vod.VodType;
import com.wecast.core.utils.ViewUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class VodDetailsPricingDialog extends BaseDialog implements SingleChoiceAdapter.OnCheckListener<VodSourceProfilePricing> {

    public static final String TAG = VodDetailsPricingDialog.class.getName();

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    VodDao vodDao;

    private DialogVodRentPricingBinding binding;
    private Vod vod;
    private VodSourceProfile profile;
    private VodSourceProfilePricing pricing = null;

    public static VodDetailsPricingDialog newInstance(Vod vod, VodSourceProfile profile) {
        VodDetailsPricingDialog dialog = new VodDetailsPricingDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("ID", vod.getId());
        bundle.putInt("PROFILE_ID", profile.getId());
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_vod_rent_pricing, container, false);
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
            vod = vodDao.getById(vodId);
            profile = vodDao.getProfile(vodId, profileId);
        }

        if (vod == null || profile == null) {
            return;
        }

        // Get currency code
        Subscription subscription = preferenceManager.getAuthentication().getSubscription();
        String currencyCode = subscription.getCurrencyCode();

        // Setup pricing list
        List<VodSourceProfilePricing> pricing = new ArrayList<>();
        if (profile != null && profile.getBusinessModel().equals(VodType.EST_VOD)) {
            if (getActivity() != null) {
                binding.title.setText(getActivity().getString(R.string.estimation_available_date));
            }
            for (final VodSourceProfilePricing price : profile.getPricing()) {
                if (price.isAvailable()) {
                    price.setName(String.format("<b>%1s</b> - %2s%3s", getDate(price.getAvailableDate()), price.getCalculatedPrice(), currencyCode));
                    pricing.add(price);
                }
            }
        } else if (profile != null && profile.getBusinessModel().equals(VodType.DTR_VOD)) {
            for (final VodSourceProfilePricing price : profile.getPricing()) {
                price.setName(String.format("<b>%1s DAYS</b> %2s%3s", price.getDuration(), currencyCode, price.getPrice()));
                pricing.add(price);
            }
        }

        // Add pricing list to adapter
        binding.profiles.setLayoutManager(new LinearLayoutManager(getActivity()));
        VodDetailsPricingAdapter adapter = new VodDetailsPricingAdapter(preferenceManager, pricing, this);
        binding.profiles.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.next.setOnClickListener(v -> proceedToPurchase());
        binding.cancel.setOnClickListener(v -> dismiss());
    }

    private void proceedToPurchase() {
        // No selected pricing
        if (pricing == null) {
            toast(R.string.message_select_pricing_for_rent);
            return;
        }

        dismiss();
        ScreenRouter.openVodRentPreviewDialog(getActivity(), vod, profile, pricing);
    }

    @Override
    public void onItemChecked(VodSourceProfilePricing item) {
        this.pricing = item;
    }

    private String getDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(date);
    }
}
