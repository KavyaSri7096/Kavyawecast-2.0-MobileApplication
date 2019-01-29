package com.wecast.mobile.ui.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected BaseViewHolder(View view) {
        super(view);
    }

    /**
     * Bind data to the item and set listener if needed.
     * @param item
     */
    public abstract void onBind(T item);
}
