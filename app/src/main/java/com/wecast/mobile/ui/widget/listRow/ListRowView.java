package com.wecast.mobile.ui.widget.listRow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.wecast.mobile.WeApp;
import com.wecast.mobile.databinding.WidgetListRowBinding;
import com.wecast.mobile.di.component.AppComponent;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public abstract class ListRowView<T> extends BaseListRow {

    private WidgetListRowBinding binding;
    private ListRowAdapter adapter;

    public ListRowView(@NonNull Context context) {
        super(context);
        initialize(context, null);
    }

    public ListRowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public ListRowView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = WidgetListRowBinding.inflate(inflater, this, true);
        setVisibility(GONE);

        // Set row title
        String title = title();
        if (title != null) {
            binding.title.setText(title);
        }

        // Set title start margin
        if (titleStartMargin() > 0) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.title.getLayoutParams();
            params.setMarginStart(titleStartMargin());
        }

        // Set content start margin
        if (dataStartMargin() > 0) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.data.getLayoutParams();
            params.setMarginStart(dataStartMargin());
        }

        // Set layout manager
        RecyclerView.LayoutManager layoutManager = layoutManager();
        if (layoutManager != null) {
            binding.data.setLayoutManager(layoutManager);
            // Set load more listener
            ListRowLoadMoreListener loadMoreListener = loadMoreListener(layoutManager);
            if (loadMoreListener != null) {
                binding.data.addOnScrollListener(loadMoreListener);
            }
        }

        // Set item decoration
        RecyclerView.ItemDecoration itemDecoration = itemDecoration();
        if (itemDecoration != null) {
            binding.data.addItemDecoration(itemDecoration);
        }

        // Set adapter
        adapter = adapter();
        if (adapter != null) {
            binding.data.setAdapter(adapter);
        }
    }

    protected abstract String title();

    protected abstract int titleStartMargin();

    protected abstract int dataStartMargin();

    protected abstract RecyclerView.LayoutManager layoutManager();

    protected abstract RecyclerView.ItemDecoration itemDecoration();

    protected abstract ListRowLoadMoreListener loadMoreListener(RecyclerView.LayoutManager layoutManager);

    protected abstract ListRowAdapter adapter();

    protected abstract void inject(AppComponent appComponent);

    /**
     * And items to adapter, and then show title
     * (Data and title will be visible at the same time)
     */
    public void addItems(List<T> items) {
        if (items != null && items.size() > 0) {
            adapter.addAll(items);
            showTitle();
            setVisibility(VISIBLE);
        } else {
            removeView();
        }
    }

    /**
     * Clear items from adapter
     */
    public void clearItems() {
        if (adapter != null) {
            adapter.clear();
        }
    }

    public void showTitle() {
        binding.title.setVisibility(VISIBLE);
    }

    public void setTitle(String title) {
        binding.title.setText(title);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        inject(WeApp.getInstance().getAppComponent());
    }
}
