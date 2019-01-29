package com.wecast.mobile.ui.common.adapter.viewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by ageech@live.com
 */

public abstract class SingleChoiceViewHolder<T> extends RecyclerView.ViewHolder {

    protected SingleChoiceViewHolder(View itemView) {
        super(itemView);
        itemView.setClickable(true);
    }

    public abstract void bind(T item);

    public abstract void onItemChecked();

    public abstract void onItemUnChecked();

    public abstract void onItemUnChecked(View view);
}