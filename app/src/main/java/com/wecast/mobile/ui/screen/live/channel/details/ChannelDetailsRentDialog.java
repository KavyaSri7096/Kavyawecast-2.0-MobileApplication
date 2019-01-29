package com.wecast.mobile.ui.screen.live.channel.details;

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

import com.wecast.core.data.api.manager.ChannelManager;
import com.wecast.core.data.db.dao.ChannelDao;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.ChannelProfile;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.utils.ViewUtils;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogChannelRentBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.common.adapter.SingleChoiceAdapter;
import com.wecast.mobile.ui.common.dialog.PurchasePinDialog;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class ChannelDetailsRentDialog extends BaseDialog implements SingleChoiceAdapter.OnCheckListener<ChannelProfile> {

    public static final String TAG = ChannelDetailsRentDialog.class.getName();

    @Inject
    PreferenceManager preferenceManager;
    @Inject
    ChannelDao channelDao;
    @Inject
    ChannelManager channelManager;

    private DialogChannelRentBinding binding;
    private Channel channel;
    private ChannelProfile channelProfile = null;

    public static ChannelDetailsRentDialog newInstance(Channel channel) {
        ChannelDetailsRentDialog dialog = new ChannelDetailsRentDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("ID", channel.getId());
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_channel_rent, container, false);
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
            channel = channelDao.getById(id);
        }

        List<ChannelProfile> channelProfiles = channel.getProfiles();
        for (ChannelProfile profile : channelProfiles) {
            if (profile.isSubscribed()) {
                channelProfiles.remove(profile);
            }
        }

        binding.channelProfiles.setLayoutManager(new LinearLayoutManager(getActivity()));
        ChannelDetailsProfileAdapter adapter = new ChannelDetailsProfileAdapter(preferenceManager, channelProfiles, this);
        binding.channelProfiles.setAdapter(adapter);
    }

    private void setupListeners() {
        binding.confirm.setOnClickListener(v -> proceedToPurchase());
        binding.cancel.setOnClickListener(v -> dismiss());
    }

    private void proceedToPurchase() {
        if (channelProfile == null) {
            toast(R.string.message_select_profile);
            return;
        }

        getDialog().hide();
        // Open purchase pin dialog
        PurchasePinDialog dialog = PurchasePinDialog.newInstance();
        dialog.setOnPinInputListener(this::rent);
        dialog.show(getBaseActivity().getSupportFragmentManager(), PurchasePinDialog.TAG);
    }

    private void rent() {
        Disposable disposable = channelManager.rent(channel.getId(), channelProfile.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isTokenExpired()) {
                        refreshToken(this::rent);
                    } else if (response.isSuccessful()) {
                        channelDao.insert(response.getData());
                        ScreenRouter.openChannelDetails(getContext(), response.getData());
                        dismiss();
                    } else {
                        toast(response.getMessage());
                    }
                }, this::toast);
        subscribe(disposable);
    }

    @Override
    public void onItemChecked(ChannelProfile item) {
        this.channelProfile = item;
    }
}
