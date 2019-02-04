package com.wecast.mobile.ui.base;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected BaseViewHolder(View view) {
        super(view);
    }

    public abstract void onBind(Context context, BaseOnClickListener onClickListener, T item);
}
