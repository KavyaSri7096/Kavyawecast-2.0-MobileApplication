package com.wecast.mobile.ui.screen.vod.details;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.core.data.api.manager.VodManager;
import com.wecast.core.data.db.dao.VodDao;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogVodRateBinding;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.core.utils.ViewUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class VodDetailsRateDialog extends BaseDialog {

    public static final String TAG = VodDetailsRateDialog.class.getName();

    @Inject
    VodManager vodManager;
    @Inject
    VodDao vodDao;

    private DialogVodRateBinding binding;
    private Vod vod;

    public static VodDetailsRateDialog newInstance(Vod vod) {
        VodDetailsRateDialog dialog = new VodDetailsRateDialog();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_vod_rate, container, false);
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

        binding.rating.setStarsSeparation(ViewUtils.dpToPx(16));
    }

    private void setupListeners() {
        binding.confirm.setOnClickListener(v -> {
            int rate = binding.rating.getNumberOfStars();
            rateVod(rate);
        });
        binding.cancel.setOnClickListener(v -> dismiss());
    }

    private void rateVod(int rate) {
        if (vod == null) {
            return;
        }

        Disposable disposable = vodManager.rate(vod.getId(), rate, vod.getMultiEventVodId() != 0)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isTokenExpired()) {
                        refreshToken(() -> rateVod(binding.rating.getNumberOfStars()));
                    } else if (response.isSuccessful()) {
                        toast(R.string.message_content_rated);
                        dismiss();
                    } else {
                        toast(response.getMessage());
                    }
                }, this::toast);
        subscribe(disposable);
    }
}
