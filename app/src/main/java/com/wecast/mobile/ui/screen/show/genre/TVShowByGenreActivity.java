package com.wecast.mobile.ui.screen.show.genre;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.TVShowGenre;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityTvShowByGenreBinding;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.screen.vod.genre.VodByGenreActivityNavigator;
import com.wecast.mobile.ui.widget.listRow.ListRowAdapter;
import com.wecast.mobile.ui.widget.listRow.ListRowLoadMoreListener;
import com.wecast.mobile.ui.widget.listRow.ListRowType;
import com.wecast.mobile.utils.CommonUtils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class TVShowByGenreActivity extends BaseActivity<ActivityTvShowByGenreBinding, TVShowByGenreActivityViewModel> implements VodByGenreActivityNavigator {

    @Inject
    TVShowByGenreActivityViewModel viewModel;

    private ActivityTvShowByGenreBinding binding;
    private int id;
    private ListRowAdapter adapter;
    private LinearLayoutManager layoutManager;
    private int currentPage = 1;

    public static void open(Context context, TVShowGenre tvShowGenre) {
        Intent intent = new Intent(context, TVShowByGenreActivity.class);
        intent.putExtra("ID", tvShowGenre.getId());
        intent.putExtra("NAME", tvShowGenre.getName());
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tv_show_by_genre;
    }

    @Override
    public TVShowByGenreActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        setStatusTransparent(this);
        setDarkMode(this);
        binding = getViewDataBinding();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt("ID");
            String name = bundle.getString("NAME");
            binding.toolbar.title.setText(name);
        }

        int columns = CommonUtils.calculateColumns(this, R.dimen.vod_card_width);
        layoutManager = new GridLayoutManager(this, columns);
        binding.data.setLayoutManager(layoutManager);
        binding.data.setHasFixedSize(true);
        binding.data.setNestedScrollingEnabled(false);
        adapter = new ListRowAdapter(this, ListRowType.TV_SHOWS);
        binding.data.setAdapter(adapter);

        getByGenreID();
    }

    private void setupListeners() {
        binding.toolbar.back.setOnClickListener(v -> finish());
        binding.data.addOnScrollListener(new ListRowLoadMoreListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                currentPage = currentPage + 1;
                getByGenreID();
            }
        });
    }

    private void getByGenreID() {
        Disposable disposable = viewModel.getByGenreID(true, currentPage, id)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(this::shouldShowEmpty)
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            adapter.setItems(response.data);
                        } else if (response.status == ApiStatus.ERROR && currentPage == 1) {
                            binding.data.setVisibility(View.GONE);
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(this::getByGenreID);
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            adapter.setItems(response.data);
                            snackBar(R.string.error_subscription_expired);
                        }
                    }
                }, throwable -> {
                    toast(throwable);
                    shouldShowEmpty();
                });
        subscribe(disposable);
    }

    private void shouldShowEmpty() {
        if (adapter.getItemCount() == 0) {
            binding.noData.setIsEmpty(true);
        } else {
            binding.noData.setIsEmpty(false);
        }
        viewModel.setLoading(false);
    }
}
