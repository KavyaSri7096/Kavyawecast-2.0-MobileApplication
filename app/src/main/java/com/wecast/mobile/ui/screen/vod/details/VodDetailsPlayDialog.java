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
import com.wecast.core.data.db.entities.VodSourceProfilePricing;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogVodPlayBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.common.adapter.ItemSingleChoiceAdapter;
import com.wecast.mobile.ui.screen.vod.VodType;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerActivity;
import com.wecast.core.utils.ViewUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by ageech@live.com
 */

public class VodDetailsPlayDialog extends BaseDialog implements ItemSingleChoiceAdapter.OnCheckListener<VodSourceProfile> {

    public static final String TAG = VodDetailsPlayDialog.class.getName();

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    VodDao vodDao;

    private DialogVodPlayBinding binding;
    private Vod vod;
    private VodSourceProfile profile = null;

    public static VodDetailsPlayDialog newInstance(Vod item) {
        VodDetailsPlayDialog dialog = new VodDetailsPlayDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("ID", item.getId());
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_vod_play, container, false);
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

        List<VodSourceProfile> profiles = VodDetailsUtils.getSourceProfiles(vod, true);
        for (VodSourceProfile profile : profiles) {
            if (!profile.isSubscribed()) {
                profiles.remove(profile);
            }
        }

        binding.profiles.setLayoutManager(new LinearLayoutManager(getActivity()));
        VodDetailsProfileAdapter adapter = new VodDetailsProfileAdapter(preferenceManager, profiles, this);
        binding.profiles.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.confirm.setOnClickListener(v -> playVod());
        binding.cancel.setOnClickListener(v -> dismiss());
    }

    private void playVod() {
        // No selected profile
        if (profile == null || profile.getPricing() == null || profile.getPricing().isEmpty()) {
            toast(R.string.message_select_profile);
            return;
        }

        // Movie is not available
        VodSourceProfilePricing pricingInfo = profile.getPricing().get(0);
        if (pricingInfo != null && profile.getBusinessModel().equals(VodType.S_VOD) && !pricingInfo.isAvailable()) {
            toast(getString(R.string.message_content_will_be_available) + pricingInfo.getAvailableDate());
            return;
        }

        // Play selected profile
        ScreenRouter.openVodPlayer(getActivity(), vod, profile, VodPlayerActivity.PLAY_MOVIE);
        dismiss();
    }

    @Override
    public void onItemChecked(VodSourceProfile item) {
        this.profile = item;
    }
}
