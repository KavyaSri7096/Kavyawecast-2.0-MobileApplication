package com.wecast.mobile.ui.screen.vod.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.core.data.db.dao.VodDao;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.entities.VodSourceProfile;
import com.wecast.core.utils.ViewUtils;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogVodStartOverBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerActivity;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

/**
 * Created by ageech@live.com
 */

public class VodDetailsStartOverDialog extends BaseDialog {

    public static final String TAG = VodDetailsStartOverDialog.class.getName();

    @Inject
    VodDao vodDao;

    private DialogVodStartOverBinding binding;
    private Vod vod;
    private VodSourceProfile vodSourceProfile;

    public static VodDetailsStartOverDialog newInstance(Vod vod, VodSourceProfile vodSourceProfile) {
        VodDetailsStartOverDialog dialog = new VodDetailsStartOverDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("ID", vod.getId());
        bundle.putInt("PROFILE_ID", vodSourceProfile.getId());
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_vod_start_over, container, false);
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
            int profileId = bundle.getInt("PROFILE_ID");
            vod = vodDao.getById(id);
            vodSourceProfile = vodDao.getProfile(id, profileId);
        }

        binding.title.setText(vod.getTitle());
    }

    private void setupListeners() {
        binding.startOver.setOnClickListener(v -> play(0));
        binding.resume.setOnClickListener(v -> play(vod.getContinueWatching().getStoppedTime()));
    }

    private void play(int seekTo) {
        ScreenRouter.openVodPlayer(getBaseActivity(), vod, vodSourceProfile, VodPlayerActivity.PLAY_MOVIE, seekTo);
        dismiss();
    }
}
