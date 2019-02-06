package com.wecast.mobile.ui.screen.live.channel;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.dao.ChannelDao;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.ChannelGenre;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.FragmentChannelBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class ChannelFragment extends BaseFragment<FragmentChannelBinding, ChannelFragmentViewModel> implements ChannelFragmentNavigator, ChannelGenreSelectListener {

    @Inject
    ChannelDao channelDao;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private FragmentChannelBinding binding;
    private ChannelFragmentViewModel viewModel;
    private int page = 1;
    private int genreId = -1;

    public static ChannelFragment newInstance() {
        return new ChannelFragment();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_channel;
    }

    @Override
    public ChannelFragmentViewModel getViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ChannelFragmentViewModel.class);
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

        setupListeners();
        getAll();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh favorite channels
        binding.favorites.clearItems();
        binding.favorites.fetchData();
    }

    private void setupListeners() {
        binding.loadMore.setOnClickListener(v -> {
            page++;
            getAll();
        });
    }

    private void getAll() {
        Disposable disposable = viewModel.getAll(true, page)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> viewModel.setLoading(false))
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.LOADING) {
                            viewModel.setLoading(true);
                        } else if (response.status == ApiStatus.SUCCESS) {
                            showChannels();
                        } else if (response.status == ApiStatus.ERROR && page == 1) {
                            noData();
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(this::getAll);
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            showChannels();
                            snackBar(R.string.error_subscription_expired);
                        }
                    }
                }, throwable -> {
                    toast(throwable);
                    noData();
                });
        subscribe(disposable);
    }

    @Override
    public void onGenreSelected(ChannelGenre channelGenre) {
        genreId = channelGenre.getId();
        binding.all.setTitle(channelGenre.getName());
        showChannels();
    }

    private void showChannels() {
        binding.all.clearItems();

        List<Channel> data = channelDao.getAllAsList();
        if (data != null && data.size() > 0) {
            if (genreId == -1) {
                binding.all.addItems(data);
                hasData();
            } else {
                filterChannels(data);
            }
        } else if (genreId == -1) {
            getAll();
        } else {
            getAllByGenreId();
        }
    }

    private void filterChannels(List<Channel> data) {
        for (Channel channel : data) {
            if (channel.getCategories() != null && channel.getCategories().size() > 0) {
                for (ChannelGenre genre : channel.getCategories()) {
                    if (genre.getId() == genreId) {
                        binding.all.addItem(channel);
                    }
                }
            }
        }

        if (binding.all.isEmpty()) {
            getAllByGenreId();
        } else {
            hasData();
        }
    }

    private void getAllByGenreId() {
        Disposable disposable = viewModel.getByGenreId(true, genreId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> viewModel.setLoading(false))
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.LOADING) {
                            viewModel.setLoading(true);
                        } else if (response.status == ApiStatus.SUCCESS) {
                            checkForData(response.data);
                        } else if (response.status == ApiStatus.ERROR) {
                            noData();
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(this::getAllByGenreId);
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            checkForData(response.data);
                            snackBar(R.string.error_subscription_expired);
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void checkForData(List<Channel> data) {
        if (data == null || data.size() == 0) {
            noData();
        } else {
            hasData();
            showChannels();
        }
    }

    private void hasData() {
        binding.loadMore.setVisibility(View.VISIBLE);
        binding.all.setVisibility(View.VISIBLE);
        binding.noData.setIsEmpty(false);
        viewModel.setLoading(false);
    }

    private void noData() {
        binding.loadMore.setVisibility(View.GONE);
        binding.all.setVisibility(View.GONE);
        binding.noData.setIsEmpty(true);
        viewModel.setLoading(false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem filter = menu.findItem(R.id.nav_filter);
        if (filter != null) {
            filter.setVisible(true);
        }
        MenuItem search = menu.findItem(R.id.nav_search);
        if (search != null) {
            search.setVisible(true);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_filter:
                openChannelGenres();
                break;
            case R.id.nav_search:
                ScreenRouter.openChannelSearch(getContext());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openChannelGenres() {
        ChannelGenresDialog dialog = ChannelGenresDialog.newInstance();
        dialog.setGenreSelectListener(this);
        dialog.show(getChildFragmentManager());
    }
}
