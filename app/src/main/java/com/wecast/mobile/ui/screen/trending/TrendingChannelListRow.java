package com.wecast.mobile.ui.screen.trending;

import android.content.Context;
import android.util.AttributeSet;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.Channel;
import com.wecast.core.data.repository.ChannelRepository;
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

public class TrendingChannelListRow extends ListRowView<Channel> {

    @Inject
    ChannelRepository channelRepository;

    public TrendingChannelListRow(@NonNull Context context) {
        super(context);
    }

    public TrendingChannelListRow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TrendingChannelListRow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String title() {
        return getContext().getResources().getString(R.string.top_channels);
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
        return null;
    }

    @Override
    protected ListRowAdapter adapter() {
        ListRowAdapter adapter = new ListRowAdapter(getContext(), ListRowType.FAVORITE_CHANNELS);
        adapter.setOnClickListener((ListRowOnClickListener<Channel>) (item, view) -> ScreenRouter.openChannelDetails(getContext(), item));
        return adapter;
    }

    @Override
    protected void inject(AppComponent appComponent) {
        appComponent.inject(this);

        if (getAppModules().hasChannels()) {
            fetchData(1);
        } else {
            removeView();
        }
    }

    private void fetchData(int page) {
        Disposable disposable = channelRepository.getTrending(true)
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
                            //snackBar(R.string.error_subscription_expired);
                        }
                    }
                }, throwable -> {
                    toast(throwable);
                    removeView();
                });
        subscribe(disposable);
    }
}
