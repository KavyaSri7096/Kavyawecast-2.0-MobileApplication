package com.wecast.mobile.ui.common.adapter;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.mobile.ui.common.adapter.viewHolder.SingleChoiceViewHolder;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public abstract class SingleChoiceAdapter<T, V extends SingleChoiceViewHolder> extends RecyclerView.Adapter {

    private final PreferenceManager preferenceManager;
    private final List<T> items;
    private final OnCheckListener<T> onCheckListener;
    private RecyclerView.LayoutManager layoutManager;
    private int lastCheckedPosition;

    protected SingleChoiceAdapter(PreferenceManager preferenceManager, List<T> arrayList, OnCheckListener<T> onCheckListener) {
        this.preferenceManager = preferenceManager;
        this.items = arrayList;
        this.onCheckListener = onCheckListener;
        this.lastCheckedPosition = findLastCheckedPosition();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ViewDataBinding dataBinding = DataBindingUtil.inflate(inflater, getLayoutResId(), viewGroup, false);
        return getViewHolder(dataBinding);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        layoutManager = recyclerView.getLayoutManager();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((V) holder).bind(items.get(position));

        if (lastCheckedPosition == position) {
            ((V) holder).onItemChecked();
        } else {
            ((V) holder).onItemUnChecked();
        }

        ((V) holder).itemView.setOnClickListener(view -> {
            if (position != lastCheckedPosition) {
                ((V) holder).onItemChecked();
                if (lastCheckedPosition != -1) {
                    ((V) holder).onItemUnChecked(getLastCheckedView());
                }
                onClick(position);
            }
        });
    }

    private void onClick(int position) {
        onCheckListener.onItemChecked(items.get(position));
        lastCheckedPosition = position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private View getLastCheckedView() {
        return layoutManager.findViewByPosition(lastCheckedPosition);
    }


    private int findLastCheckedPosition() {
        for (int i = 0; i < items.size(); i++) {
            if (findLastCheckedPosition(preferenceManager, items.get(i)))
                return i;
        }
        return 0;
    }

    @LayoutRes
    protected abstract int getLayoutResId();

    protected abstract V getViewHolder(ViewDataBinding dataBinding);

    protected abstract boolean findLastCheckedPosition(PreferenceManager preferenceManager, T item);

    public interface OnCheckListener<T> {
        void onItemChecked(T item);
    }
}

