package com.wecast.mobile.ui.screen.vod.search;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.ShowType;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.DialogSearchFilterBinding;
import com.wecast.mobile.ui.base.BaseDialog;
import com.wecast.mobile.ui.common.adapter.ItemMultiChoiceAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class VodSearchFilterDialog extends BaseDialog implements ItemMultiChoiceAdapter.OnItemClickListener {

    public static final String TAG = VodSearchFilterDialog.class.getName();

    @Inject
    VodRepository vodRepository;

    private DialogSearchFilterBinding binding;
    private VodDetailsSearchFilterSelectListener filterSelectListener;
    private ItemMultiChoiceAdapter adapter;
    private List<ShowType> showTypeList;

    public static VodSearchFilterDialog newInstance() {
        return new VodSearchFilterDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_search_filter, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        showTypeList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.filters.setLayoutManager(layoutManager);
        adapter = new ItemMultiChoiceAdapter();
        adapter.setOnItemClickListener(this);
        binding.filters.setAdapter(adapter);

        getShowTypes();
    }

    private void setupListeners() {
        binding.confirm.setOnClickListener(v -> {
            if (filterSelectListener != null) {
                filterSelectListener.onFiltersSelected(showTypeList);
                dismiss();
            }
        });
    }

    private void getShowTypes() {
        Disposable disposable = vodRepository.getShowTypes()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            adapter.setItems(response.data);
                        } else if (response.status == ApiStatus.ERROR) {
                            toast(response.message);
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(this::getShowTypes);
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            adapter.setItems(response.data);
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }

    @Override
    public void onClick(ShowType showType, boolean isChecked) {
        if (isChecked) {
            showTypeList.add(showType);
        } else {
            showTypeList.remove(showType);
        }
    }

    void setFilterSelectListener(VodDetailsSearchFilterSelectListener filterSelectListener) {
        this.filterSelectListener = filterSelectListener;
    }
}
