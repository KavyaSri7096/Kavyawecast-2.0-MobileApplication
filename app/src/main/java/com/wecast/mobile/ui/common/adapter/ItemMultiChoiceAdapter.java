package com.wecast.mobile.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wecast.core.data.db.entities.ShowType;
import com.wecast.mobile.databinding.CardCheckBoxBinding;
import com.wecast.mobile.ui.common.adapter.viewHolder.ItemMultiChoiceViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public class ItemMultiChoiceAdapter extends RecyclerView.Adapter<ItemMultiChoiceViewHolder> {

    private List<ShowType> items;
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ItemMultiChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new ItemMultiChoiceViewHolder(CardCheckBoxBinding.inflate(inflater, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemMultiChoiceViewHolder holder, int position) {
        ShowType item = items.get(position);
        holder.onBind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<ShowType> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onClick(ShowType showType, boolean isChecked);
    }
}
