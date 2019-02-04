package com.wecast.mobile.ui.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public abstract class BaseAdapter<T, VH extends BaseViewHolder<T>> extends RecyclerView.Adapter<VH> {

    private final LayoutInflater layoutInflater;
    private final List<T> items;
    private BaseOnClickListener onClickListener;

    public BaseAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        items = new ArrayList<>();
    }

    /**
     * To be implemented in as specific adapter
     */
    @NonNull
    @Override
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the itemView to reflect the item at the given
     * position.
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T item = items.get(position);
        holder.onBind(layoutInflater.getContext(), onClickListener, item);
    }

    public void setItems(List<T> items) {
        if (items == null) {
            throw new IllegalArgumentException("Cannot set 'null' item to the Recycler adapter");
        }
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return items;
    }

    public T getItem(int position) {
        return items.get(position);
    }

    public int getCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * Adds item to the end of the data set.
     * Notifies that item has been inserted.
     */
    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add 'null' item to the Recycler adapter");
        }
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    /**
     * Adds items of items to the end of the adapter's data set.
     * Notifies that item has been inserted.
     */
    public void addAll(List<T> items) {
        if (items == null) {
            throw new IllegalArgumentException("Cannot add 'null' items to the Recycler adapter");
        }
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void addAll(List<T> items, boolean hasFooter) {
        if (items == null) {
            throw new IllegalArgumentException("Cannot add 'null' items to the Recycler adapter");
        }
        this.items.addAll(items);
        notifyItemRangeInserted(this.items.size() - items.size() + 1, items.size());
    }

    /**
     * Clears all the items in the adapter.
     */
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    /**
     * Removes an item from the adapter.
     * Notifies that item has been removed.
     *
     * @param item to be removed
     */
    public void remove(T item) {
        int position = items.indexOf(item);
        if (position > -1) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Returns whether adapter is empty or not.
     *
     * @return `true` if adapter is empty or `false` otherwise
     */
    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    /**
     * Set click onClickListener, which must extend {@link BaseOnClickListener}
     *
     * @param onClickListener click onClickListener
     */
    public void setOnClickListener(BaseOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Inflates a view.
     *
     * @param layout layout to me inflater
     * @param parent container where to inflate
     * @return inflated View
     */
    @NonNull
    private View inflate(@LayoutRes final int layout, @Nullable final ViewGroup parent) {
        return layoutInflater.inflate(layout, parent, false);
    }
}
