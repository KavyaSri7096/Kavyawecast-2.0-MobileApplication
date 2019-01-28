package com.wecast.mobile.ui.screen.trending;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.FragmentTrendingBinding;
import com.wecast.mobile.ui.base.BaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

/**
 * Created by ageech@live.com
 */

public class TrendingFragment extends BaseFragment<FragmentTrendingBinding, TrendingFragmentViewModel> implements TrendingFragmentNavigator {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentTrendingBinding binding;
    private TrendingFragmentViewModel viewModel;

    public static TrendingFragment newInstance() {
        return new TrendingFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_trending;
    }

    @Override
    public TrendingFragmentViewModel getViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TrendingFragmentViewModel.class);
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
    }

    private void setupUI() {
        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(() -> viewModel.setLoading(false), 4000);
    }
}
