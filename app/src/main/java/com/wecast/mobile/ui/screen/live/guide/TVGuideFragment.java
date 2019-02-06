package com.wecast.mobile.ui.screen.live.guide;

import android.os.Bundle;
import android.view.View;

import com.wecast.core.data.db.dao.ChannelDao;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.TVGuide;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.FragmentTvGuideBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseFragment;
import com.wecast.mobile.ui.widget.listRow.ListRowAdapter;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;
import com.wecast.mobile.ui.widget.listRow.ListRowType;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class TVGuideFragment extends BaseFragment<FragmentTvGuideBinding, TVGuideFragmentViewModel> implements TVGuideFragmentNavigator {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    ChannelDao channelDao;

    private FragmentTvGuideBinding binding;
    private TVGuideFragmentViewModel viewModel;
    private ListRowAdapter adapter;
    private int page = 1;

    public static TVGuideFragment newInstance() {
        return new TVGuideFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tv_guide;
    }

    @Override
    public TVGuideFragmentViewModel getViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TVGuideFragmentViewModel.class);
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
        binding.programmes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.programmes.setNestedScrollingEnabled(false);
        adapter = new ListRowAdapter(getBaseActivity(), ListRowType.TV_GUIDE);
        adapter.setOnClickListener((ListRowOnClickListener<TVGuide>) (item, view) -> {
            Channel channel = channelDao.getById(item.getId());
            ScreenRouter.openChannelDetails(getBaseActivity(), channel);
        });
        binding.programmes.setAdapter(adapter);

        getProgrammes(page);
    }

    private void setupListeners() {
        binding.refresh.setOnRefreshListener(() -> {
            adapter.clear();
            page = 1;
            getProgrammes(page);
        });
        binding.loadMore.setOnClickListener(v -> {
            viewModel.setLoading(true);
            page++;
            getProgrammes(page);
        });
    }

    private void getProgrammes(int page) {
        Disposable disposable = viewModel.getCurrentProgrammes(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(this::shouldShowEmpty)
                .subscribe(response -> {
                    if (response != null) {
                        if (response.isSuccessful()) {
                            showData(response.getData().getItems());
                        } else if (response.isTokenExpired()) {
                            refreshToken(() -> getProgrammes(page));
                        } else if (response.isSubscriptionExpired()) {
                            showData(response.getData().getItems());
                            snackBar(R.string.error_subscription_expired);
                        } else if (page == 1) {
                            binding.programmes.setVisibility(View.GONE);
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void showData(List<TVGuide> data) {
        adapter.addAll(data);
        viewModel.setLoading(false);
        binding.loadMore.setVisibility(View.VISIBLE);
        binding.refresh.setRefreshing(false);
    }

    private void shouldShowEmpty() {
        binding.loadMore.setVisibility(adapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
        viewModel.setEmpty(adapter.getItemCount() == 0);
        viewModel.setLoading(false);
        binding.refresh.setRefreshing(false);
    }
}
