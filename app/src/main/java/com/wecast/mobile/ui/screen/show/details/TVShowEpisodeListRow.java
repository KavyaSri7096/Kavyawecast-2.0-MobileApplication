package com.wecast.mobile.ui.screen.show.details;

import android.content.Context;
import android.util.AttributeSet;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.TVShowSeason;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.repository.TVShowRepository;
import com.wecast.mobile.R;
import com.wecast.mobile.di.component.AppComponent;
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

public class TVShowEpisodeListRow extends ListRowView<Vod> {

    @Inject
    TVShowRepository tvShowRepository;

    private int tvShowId;
    private TVShowSeason tvShowSeason;

    public TVShowEpisodeListRow(@NonNull Context context, int tvShowId, TVShowSeason tvShowSeason) {
        super(context);
        this.tvShowId = tvShowId;
        this.tvShowSeason = tvShowSeason;
    }

    public TVShowEpisodeListRow(@NonNull Context context) {
        super(context);
    }

    public TVShowEpisodeListRow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TVShowEpisodeListRow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String title() {
        return null;
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
                fetchData(page, tvShowId, tvShowSeason.getId());
            }
        };
    }

    @Override
    protected ListRowAdapter adapter() {
        return new ListRowAdapter(getContext(), ListRowType.EPISODES);
    }

    @Override
    protected void inject(AppComponent appComponent) {
        appComponent.inject(this);
        fetchData(1, tvShowId, tvShowSeason.getId());
    }

    private void fetchData(int page, int tvShowId, int tvShowSeasonId) {
        Disposable disposable = tvShowRepository.getEpisodes(page, tvShowId, tvShowSeasonId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.status == ApiStatus.SUCCESS) {
                        addItems(response.data);
                        setTitle();
                    } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                        refreshToken(() -> fetchData(page, tvShowId, tvShowSeasonId));
                    } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                        addItems(response.data);
                        setTitle();
                        //snackBar(R.string.error_subscription_expired);
                    }
                }, throwable -> {
                    toast(throwable);
                    removeView();
                });
        subscribe(disposable);
    }

    private void setTitle() {
        String title = getContext().getResources().getString(R.string.season_number, tvShowSeason.getNumber());
        setTitle(title);
        showTitle();
    }
}
