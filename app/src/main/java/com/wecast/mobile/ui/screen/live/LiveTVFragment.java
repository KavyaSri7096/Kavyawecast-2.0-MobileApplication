package com.wecast.mobile.ui.screen.live;

import android.os.Bundle;
import android.view.View;

import com.wecast.core.data.db.entities.composer.Modules;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.FragmentLiveTvBinding;
import com.wecast.mobile.ui.base.BaseFragment;
import com.wecast.mobile.ui.utils.FragmentStateManager;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

/**
 * Created by ageech@live.com
 */

public class LiveTVFragment extends BaseFragment<FragmentLiveTvBinding, LiveTVFragmentViewModel> implements LiveTVFragmentNavigator {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentLiveTvBinding binding;
    private LiveTVFragmentViewModel viewModel;
    private FragmentStateManager fragmentStateManager;

    public static LiveTVFragment newInstance() {
        return new LiveTVFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_live_tv;
    }

    @Override
    public LiveTVFragmentViewModel getViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LiveTVFragmentViewModel.class);
        return viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.setNavigator(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getViewDataBinding();

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        fragmentStateManager = new FragmentStateManager(getChildFragmentManager(), binding.container);

        Modules appModules = viewModel.getAppModules();
        if (appModules != null) {
            if (appModules.hasChannels()) {
                binding.channels.setVisibility(View.VISIBLE);
                fragmentStateManager.goTo(R.string.watch_live);
            } else {
                binding.channels.setVisibility(View.GONE);
            }
            binding.tvGuide.setVisibility(appModules.hasEpg() ? View.VISIBLE : View.GONE);
        }
    }

    private void setupListeners() {
        binding.channels.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fragmentStateManager.goTo(R.string.watch_live);
            }
        });
        binding.tvGuide.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                fragmentStateManager.goTo(R.string.tv_guide);
            }
        });
    }
}
