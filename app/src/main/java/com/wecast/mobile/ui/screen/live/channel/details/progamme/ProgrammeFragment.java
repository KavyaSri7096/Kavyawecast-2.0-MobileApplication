package com.wecast.mobile.ui.screen.live.channel.details.progamme;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.TVGuide;
import com.wecast.core.data.db.entities.TVGuideProgramme;
import com.wecast.core.utils.DateUtils;
import com.wecast.core.utils.ReminderUtils;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.FragmentProgrammeBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseFragment;
import com.wecast.mobile.ui.widget.listRow.ListRowAdapter;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;
import com.wecast.mobile.ui.widget.listRow.ListRowType;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class ProgrammeFragment extends BaseFragment<FragmentProgrammeBinding, ProgrammeFragmentViewModel> implements ProgrammeFragmentNavigator {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    ReminderUtils reminderUtils;

    private FragmentProgrammeBinding binding;
    private ProgrammeFragmentViewModel viewModel;
    private int id;
    private Channel channel;
    private ListRowAdapter adapter;
    private ProgrammeLayoutManager layoutManager;

    public static ProgrammeFragment newInstance(Channel channel) {
        ProgrammeFragment fragment = new ProgrammeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("ID", channel.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_programme;
    }

    @Override
    public ProgrammeFragmentViewModel getViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProgrammeFragmentViewModel.class);
        return viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        setupUI();
    }

    private void setupUI() {
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getInt("ID");
            channel = viewModel.getById(id);
        }

        // Setup day picker
        binding.picker.setValues(getDaysList());
        binding.picker.setOnItemSelectedListener(this::getProgrammes);
        binding.picker.setSelectedItem(1);

        // Setup recycler view for data
        layoutManager = new ProgrammeLayoutManager(getBaseActivity());
        binding.data.setLayoutManager(layoutManager);
        adapter = new ListRowAdapter(getBaseActivity(), ListRowType.TV_GUIDE_PROGRAMME, reminderUtils);
        adapter.setOnClickListener((ListRowOnClickListener<TVGuideProgramme>) (item, view) -> ScreenRouter.openProgrammeDetails(getBaseActivity(), channel, item));
        binding.data.setAdapter(adapter);

        // Load programmes for current day
        getProgrammes(1);
    }

    private void getProgrammes(int position) {
        Date date = getSelectedDate(position);
        String start = DateUtils.beginOfDay(date);
        String end = DateUtils.endOfDay(date);

        viewModel.setLoading(true);
        adapter.clear();

        Disposable disposable = viewModel.getProgrammes(true, 1, id, start, end)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            showData(response.data);
                        } else if (response.status == ApiStatus.ERROR) {
                            showNoData();
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(() -> getProgrammes(position));
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            showData(response.data);
                            snackBar(R.string.error_subscription_expired);
                        }
                    }
                }, throwable -> {
                    toast(throwable);
                    showNoData();
                });
        subscribe(disposable);
    }

    private void showData(TVGuide data) {
        if (data == null) {
            return;
        }

        List<TVGuideProgramme> programmes = data.getProgrammes();
        if (programmes != null && programmes.size() > 0) {
            adapter.addAll(programmes);
            hideLoading();
        } else {
            showNoData();
        }
    }

    private void showNoData() {
        binding.data.setVisibility(View.GONE);
        viewModel.setLoading(false);
        binding.noData.setIsEmpty(true);
    }

    private void hideLoading() {
        binding.data.setVisibility(View.VISIBLE);
        viewModel.setLoading(false);
        binding.noData.setIsEmpty(false);
        scrollToCurrentProgramme();
    }

    private void scrollToCurrentProgramme() {
        int position = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            TVGuideProgramme programme = (TVGuideProgramme) adapter.getItem(i);
            if (programme.isCurrent()) {
                position = i;
                break;
            }
        }
        layoutManager.scrollToPosition(position);
    }

    private CharSequence[] getDaysList() {
        CharSequence[] values = new CharSequence[3];
        values[0] = DateUtils.getDay(-1);
        values[1] = getResources().getString(R.string.today);
        values[2] = DateUtils.getDay(+1);
        return values;
    }

    private Date getSelectedDate(int position) {
        Date today = new Date();
        switch (position) {
            case 0:
                Calendar before = Calendar.getInstance();
                before.setTime(today);
                before.add(Calendar.DAY_OF_WEEK, -1);
                return before.getTime();
            case 1:
                return today;
            default:
                Calendar next = Calendar.getInstance();
                next.setTime(today);
                next.add(Calendar.DAY_OF_WEEK, 1);
                return next.getTime();
        }
    }
}
