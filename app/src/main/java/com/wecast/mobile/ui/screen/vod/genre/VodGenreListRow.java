package com.wecast.mobile.ui.screen.vod.genre;

import android.content.Context;
import android.util.AttributeSet;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.VodGenre;
import com.wecast.core.data.repository.VodGenreRepository;
import com.wecast.mobile.R;
import com.wecast.mobile.di.component.AppComponent;
import com.wecast.mobile.ui.widget.listRow.ListRowView;
import com.wecast.mobile.ui.widget.listRow.ListRowAdapter;
import com.wecast.mobile.ui.widget.listRow.ListRowLoadMoreListener;
import com.wecast.mobile.ui.widget.listRow.ListRowType;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class VodGenreListRow extends ListRowView<VodGenre> {

    @Inject
    VodGenreRepository vodGenreRepository;

    public VodGenreListRow(@NonNull Context context) {
        super(context);
    }

    public VodGenreListRow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VodGenreListRow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String title() {
        return getContext().getResources().getString(R.string.genres);
    }

    @Override
    protected int titleStartMargin() {
        return 0;
    }

    @Override
    protected int dataStartMargin() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.standard);
    }

    @Override
    protected RecyclerView.LayoutManager layoutManager() {
        return new StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL);
    }

    @Override
    protected RecyclerView.ItemDecoration itemDecoration() {
        return null;
    }

    @Override
    protected ListRowLoadMoreListener loadMoreListener(RecyclerView.LayoutManager layoutManager) {
        return null;
    }

    @Override
    protected ListRowAdapter adapter() {
        return new ListRowAdapter(getContext(), ListRowType.MOVIE_GENRES);
    }

    @Override
    protected void inject(AppComponent appComponent) {
        appComponent.inject(this);
        fetchData(1);
    }

    private void fetchData(int page) {
        Disposable disposable = vodGenreRepository.getGenres(true)
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
