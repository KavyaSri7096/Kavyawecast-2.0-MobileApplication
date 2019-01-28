package com.wecast.mobile.ui.screen.home;

import android.content.Context;
import android.util.AttributeSet;

import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.Highlighted;
import com.wecast.core.data.repository.HighlightedRepository;
import com.wecast.mobile.di.component.AppComponent;
import com.wecast.mobile.ui.widget.listRow.ListRowPager;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class HomeHighlightedListRow extends ListRowPager {

    @Inject
    HighlightedRepository highlightedRepository;

    private HomeHighlightedAdapter adapter;

    public HomeHighlightedListRow(@NonNull Context context) {
        super(context);
    }

    public HomeHighlightedListRow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeHighlightedListRow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    protected int offScreenPageLimit() {
        return 2;
    }

    @Override
    protected ViewPager.PageTransformer pageTransformer() {
        return (view, position) -> {
            if (position >= -1 || position <= 1) {
                view.setScaleY(Math.max(0.85f, 1 - Math.abs(position)));
            }
        };
    }

    @Override
    protected PagerAdapter adapter() {
        adapter = new HomeHighlightedAdapter(getContext());
        return adapter;
    }

    @Override
    protected void inject(AppComponent appComponent) {
        appComponent.inject(this);
        fetchData();
    }

    private void fetchData() {
        Disposable disposable = highlightedRepository.getAll(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            addItems(response.data);
                        } else if (response.status == ApiStatus.ERROR) {
                            removeView();
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(this::fetchData);
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

    private void addItems(List<Highlighted> items) {
        if (items != null && items.size() > 0) {
            adapter.setItems(items);
            showTitle();
            setVisibility(VISIBLE);
        } else {
            removeView();
        }
    }
}
