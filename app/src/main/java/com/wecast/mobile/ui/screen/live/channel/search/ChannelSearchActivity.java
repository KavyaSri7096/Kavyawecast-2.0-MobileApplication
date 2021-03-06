package com.wecast.mobile.ui.screen.live.channel.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.db.entities.ChannelGenre;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityChannelSearchBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.common.adapter.SortingFiltersAdapter;
import com.wecast.mobile.ui.common.listener.OnItemSelectListener;
import com.wecast.mobile.ui.common.listener.OnTextInputListener;
import com.wecast.mobile.ui.widget.listRow.ListRowAdapter;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;
import com.wecast.mobile.ui.widget.listRow.ListRowType;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class ChannelSearchActivity extends BaseActivity<ActivityChannelSearchBinding, ChannelSearchActivityViewModel> implements ChannelSearchActivityNavigator {

    @Inject
    ChannelSearchActivityViewModel viewModel;

    private ActivityChannelSearchBinding binding;
    private ListRowAdapter adapter;
    private String query;

    public static void open(Context context) {
        Intent intent = new Intent(context, ChannelSearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_channel_search;
    }

    @Override
    public ChannelSearchActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupListeners();
        setupSearchDisposable();
    }

    private void setupUI() {
        setStatusTransparent(this);
        setLightMode(this);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        // Set custom toolbar
        setSupportActionBar(binding.toolbar.toolbar);

        // Set toolbar title
        binding.toolbar.title.setText(getString(R.string.search));

        // Setup recycler view for data
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.data.setLayoutManager(layoutManager);
        binding.data.setHasFixedSize(true);
        binding.data.setNestedScrollingEnabled(false);
        adapter = new ListRowAdapter(this, ListRowType.CHANNELS);
        adapter.setOnClickListener((ListRowOnClickListener<Channel>) (item, view) -> ScreenRouter.openChannelDetails(this, item));
        binding.data.setAdapter(adapter);

        // Setup filters
        String[] filters = getResources().getStringArray(R.array.filter);
        SortingFiltersAdapter arrayAdapter = new SortingFiltersAdapter(this, filters);
        binding.filter.setAdapter(arrayAdapter);
    }

    private void setupListeners() {
        binding.toolbar.back.setOnClickListener(view -> onBackPressed());
        binding.clear.setOnClickListener(v -> {
            binding.query.setText("");
            binding.query.requestFocus();
            clear();
        });
        binding.filter.setOnItemSelectedListener(new OnItemSelectListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sort(position == 1);
            }
        });
    }

    private void setupSearchDisposable() {
        Disposable disposable = Observable.create(OnTextInputListener.watch(binding.query))
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.isEmpty()) {
                        clear();
                    } else {
                        query = response;
                        getChannels();
                    }
                });
        subscribe(disposable);
    }

    private void getChannels() {
        viewModel.setLoading(true);

        Disposable disposable = viewModel.search(query, null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            showData(response.data);
                        } else if (response.status == ApiStatus.ERROR) {
                            clear();
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(this::getChannels);
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            showData(response.data);
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }

    public void showData(List<Channel> data) {
        if (data == null || data.size() == 0) {
            showNoData();
            return;
        }

        binding.title.setText(getString(R.string.search_result_for, query));
        binding.data.setVisibility(View.VISIBLE);
        binding.filter.setVisibility(View.VISIBLE);

        // Add data to adapter
        adapter.clear();
        adapter.addAll(data);

        // Hide loader
        viewModel.setLoading(false);
    }

    private void showNoData() {
        binding.title.setText(getString(R.string.search_result));
        binding.data.setVisibility(View.GONE);
        binding.filter.setVisibility(View.GONE);
        viewModel.setLoading(false);
    }

    private void clear() {
        binding.filter.setVisibility(View.GONE);
        binding.filter.setSelection(0);
        binding.title.setText("");
        adapter.clear();
    }

    private void sort(boolean ascending) {
        Collections.sort((List<Channel>) adapter.getItems(), (o1, o2) -> {
            if (ascending) {
                return o1.getTitle().compareToIgnoreCase(o2.getTitle());
            } else {
                return o2.getTitle().compareToIgnoreCase(o1.getTitle());
            }
        });
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_filter:
                openSearchFilter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSearchFilter() {
        ChannelSearchFilterDialog dialog = ChannelSearchFilterDialog.newInstance();
        dialog.setFilterSelectListener(this::filterSearch);
        dialog.show(getSupportFragmentManager());
    }

    private void filterSearch(List<ChannelGenre> channelGenreList) {
        if (channelGenreList == null || channelGenreList.size() == 0
                || query == null || query.isEmpty()) {
            return;
        }

        viewModel.setLoading(true);

        Disposable disposable = viewModel.search(query, channelGenreList)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            showData(response.data);
                        } else if (response.status == ApiStatus.ERROR) {
                            clear();
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(() -> filterSearch(channelGenreList));
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            showData(response.data);
                        }
                    }
                }, this::toast);
        subscribe(disposable);
    }
}
