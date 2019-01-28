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
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.entities.VodSourceProfile;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogVodRentBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.common.adapter.SingleItemChoiceAdapter;
import com.wecast.core.utils.ViewUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class VodDetailsRentDialog extends BaseDialog implements SingleItemChoiceAdapter.OnCheckListener<VodSourceProfile> {

    public static final String TAG = VodDetailsRentDialog.class.getName();

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    VodDao vodDao;

    private DialogVodRentBinding binding;
    private Vod vod;
    private VodSourceProfile profile = null;

    public static VodDetailsRentDialog newInstance(Vod vod) {
        VodDetailsRentDialog dialog = new VodDetailsRentDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("ID", vod.getId());
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_vod_rent, container, false);
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
            int id = bundle.getInt("ID");
            vod = vodDao.getById(id);
        }

        if (vod == null) {
            return;
        }

        List<VodSourceProfile> profiles = VodDetailsUtils.getSourceProfiles(vod, false);
        binding.profiles.setLayoutManager(new LinearLayoutManager(getActivity()));
        VodDetailsProfileAdapter adapter = new VodDetailsProfileAdapter(preferenceManager, profiles, this);
        binding.profiles.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.next.setOnClickListener(v -> proceedToPricing());
        binding.cancel.setOnClickListener(v -> dismiss());
    }

    private void proceedToPricing() {
        if (profile == null) {
            toast(R.string.message_select_profile_for_rent);
            return;
        }

        dismiss();
        ScreenRouter.openVodRentPricingDialog(getActivity(), vod, profile);
    }

    @Override
    public void onItemChecked(VodSourceProfile item) {
        this.profile = item;
    }
}
