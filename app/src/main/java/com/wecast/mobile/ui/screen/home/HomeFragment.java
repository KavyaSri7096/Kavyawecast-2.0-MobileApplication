package com.wecast.mobile.ui.screen.home;

import android.os.Bundle;
import android.view.View;

import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.FragmentHomeBinding;
import com.wecast.mobile.ui.base.BaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

/**
 * Created by ageech@live.com
 */

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeFragmentViewModel> implements HomeFragmentNavigator {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentHomeBinding binding;
    private HomeFragmentViewModel viewModel;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public HomeFragmentViewModel getViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeFragmentViewModel.class);
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
    }
}
