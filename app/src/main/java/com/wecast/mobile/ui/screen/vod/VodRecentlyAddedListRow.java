package com.wecast.mobile.ui.screen.vod;

import android.content.Context;
import android.util.AttributeSet;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.repository.VodRepository;
import com.wecast.mobile.R;
import com.wecast.mobile.di.component.AppComponent;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;
import com.wecast.mobile.ui.widget.listRow.ListRowView;
import com.wecast.mobile.ui.widget.listRow.ListRowAdapter;
import com.wecast.mobile.ui.widget.listRow.ListRowItemDecoration;
import com.wecast.mobile.ui.widget.listRow.ListRowLoadMoreListener;
import com.wecast.mobile.ui.widget.listRow.ListRowType;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class VodRecentlyAddedListRow extends ListRowView<Vod> {

    @Inject
    VodRepository vodRepository;

    public VodRecentlyAddedListRow(@NonNull Context context) {
        super(context);
    }

    public VodRecentlyAddedListRow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VodRecentlyAddedListRow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String title() {
        return getContext().getResources().getString(R.string.recently_added);
    }

    @Override
    protected int titleStartMargin() {
        return 0;
    }

    @Override
    protected int dataStartMargin() {
        return 0;
    }

    @Override
    protected RecyclerView.LayoutManager layoutManager() {
        return new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
    }

    @Override
    protected RecyclerView.ItemDecoration itemDecoration() {
        return new ListRowItemDecoration();
    }

    @Override
    protected ListRowLoadMoreListener loadMoreListener(RecyclerView.LayoutManager layoutManager) {
        return new ListRowLoadMoreListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchData(page);
            }
        };
    }

    @Override
    protected ListRowAdapter adapter() {
        ListRowAdapter adapter = new ListRowAdapter(getContext(), ListRowType.MOVIES);
        adapter.setOnClickListener((ListRowOnClickListener<Vod>) (item, view) -> ScreenRouter.openVodDetails(getContext(), item));
        return adapter;
    }

    @Override
    protected void inject(AppComponent appComponent) {
        appComponent.inject(this);

        if (getAppModules().hasVod()) {
            fetchData(1);
        } else {
            removeView();
        }
    }

    private void fetchData(int page) {
        Disposable disposable = vodRepository.getRecentlyAdded(true, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            addItems(response.data);
                        } else if (response.status == ApiStatus.ERROR && page == 1) {
                            removeView();
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(() -> fetchData(page));
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            addItems(response.data);
                            snackBar(R.string.error_subscription_expired);
                        }
                    }
                }, throwable -> {
                    toast(throwable);
                    removeView();
                });
        subscribe(disposable);
    }
}
