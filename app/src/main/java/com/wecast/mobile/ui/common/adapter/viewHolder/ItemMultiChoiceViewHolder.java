package com.wecast.mobile.ui.common.adapter.viewHolder;

import com.wecast.core.data.db.entities.ShowType;
import com.wecast.mobile.databinding.CardCheckBoxBinding;
import com.wecast.mobile.ui.common.adapter.ItemMultiChoiceAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public class ItemMultiChoiceViewHolder extends RecyclerView.ViewHolder {

    private CardCheckBoxBinding binding;

    public ItemMultiChoiceViewHolder(@NonNull CardCheckBoxBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void onBind(ShowType item, ItemMultiChoiceAdapter.OnItemClickListener onItemClickListener) {
        binding.name.setText(item.getName());
        binding.checkBox.setChecked(item.isChecked());

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
