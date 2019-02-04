package com.wecast.mobile.ui.screen.live.channel;

import android.content.Context;
import android.util.AttributeSet;

import com.wecast.core.data.db.entities.Channel;
import com.wecast.mobile.R;
import com.wecast.mobile.di.component.AppComponent;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;
import com.wecast.mobile.ui.widget.listRow.ListRowView;
import com.wecast.mobile.ui.widget.listRow.ListRowAdapter;
import com.wecast.mobile.ui.widget.listRow.ListRowLoadMoreListener;
import com.wecast.mobile.ui.widget.listRow.ListRowType;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public class ChannelListRow extends ListRowView<Channel> {

    private ListRowAdapter adapter;

    public ChannelListRow(@NonNull Context context) {
        super(context);
    }

    public ChannelListRow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChannelListRow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected String title() {
        return getContext().getResources().getString(R.string.all_channels);
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
        return new GridLayoutManager(getContext(), 2);
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
        adapter = new ListRowAdapter(getContext(), ListRowType.CHANNELS);
        adapter.setOnClickListener((ListRowOnClickListener<Channel>) (item, view) -> ScreenRouter.openChannelDetails(getContext(), item));
        return adapter;
    }

    @Override
    protected void inject(AppComponent appComponent) {
        appComponent.inject(this);
    }

    public void addItems(List<Channel> data) {
        if (adapter != null) {
            adapter.setItems(data);
            showTitle();
        }
    }

    public void addItem(Channel data) {
        if (adapter != null) {
            adapter.add(data);
            showTitle();
        }
    }

    public void clearItems() {
        adapter.clear();
    }

    public boolean isEmpty() {
        return adapter.getItems().size() == 0;
    }
}
