package com.wecast.mobile.ui.screen.show;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.FragmentTvShowBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

/**
 * Created by ageech@live.com
 */

public class TVShowFragment extends BaseFragment<FragmentTvShowBinding, TVShowFragmentViewModel> implements TVShowNavigator {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentTvShowBinding binding;
    private TVShowFragmentViewModel viewModel;

    public static TVShowFragment newInstance() {
        return new TVShowFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tv_show;
    }

    @Override
    public TVShowFragmentViewModel getViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TVShowFragmentViewModel.class);
        return viewModel;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
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
        handler.postDelayed(() -> viewModel.setLoading(false), 2000);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.nav_search);
        if (search != null) {
            search.setVisible(true);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_search:
                ScreenRouter.openTVShowSearch(getContext());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
