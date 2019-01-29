package com.wecast.mobile.ui.screen.live.channel.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.wecast.core.data.db.entities.ChannelGenre;
import com.wecast.mobile.databinding.CardCheckBoxBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public class ChannelSearchFilterAdapter extends RecyclerView.Adapter<ChannelSearchFilterAdapter.ChannelSearchViewHolder> {

    private List<ChannelGenre> items;
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ChannelSearchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new ChannelSearchViewHolder(CardCheckBoxBinding.inflate(inflater, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelSearchViewHolder holder, int position) {
        ChannelGenre item = items.get(position);
        holder.onBind(item);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<ChannelGenre> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onClick(ChannelGenre channelGenre, boolean isChecked);
    }

    /**
     * VIEW HOLDER
     */

    public class ChannelSearchViewHolder extends RecyclerView.ViewHolder {

        private CardCheckBoxBinding binding;

        ChannelSearchViewHolder(@NonNull CardCheckBoxBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void onBind(ChannelGenre item) {
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
