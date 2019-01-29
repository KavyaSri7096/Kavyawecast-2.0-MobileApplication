package com.wecast.mobile.ui.common.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wecast.core.data.db.entities.ShowType;
import com.wecast.mobile.databinding.CardCheckBoxBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public class ShowTypeFilterAdapter extends RecyclerView.Adapter<ShowTypeFilterAdapter.ShowTypeFilterViewHolder> {

    private List<ShowType> items;
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ShowTypeFilterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new ShowTypeFilterViewHolder(CardCheckBoxBinding.inflate(inflater, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShowTypeFilterViewHolder holder, int position) {
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

    /**
     * VIEW HOLDER
     */

    public class ShowTypeFilterViewHolder extends RecyclerView.ViewHolder {

        private CardCheckBoxBinding binding;

        ShowTypeFilterViewHolder(@NonNull CardCheckBoxBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(ShowType item, ShowTypeFilterAdapter.OnItemClickListener onItemClickListener) {
            // Set name
            binding.name.setText(item.getName());

            // Set check box status
            binding.checkBox.setChecked(item.isChecked());

            // Set click listener
            itemView.setOnClickListener(v -> {
                boolean isChecked = !item.isChecked();
                item.setChecked(isChecked);
                binding.checkBox.setChecked(isChecked);
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(item, isChecked);
                }
            });
        }
    }
}
