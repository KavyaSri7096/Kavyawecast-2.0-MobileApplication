package com.wecast.mobile.ui.screen.live.channel;

import android.content.Context;

import com.wecast.core.data.db.entities.Channel;
import com.wecast.mobile.databinding.CardChannelBinding;
import com.wecast.mobile.ui.base.BaseOnClickListener;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class ChannelViewHolder extends BaseViewHolder<Channel> {

    private CardChannelBinding binding;
    private ChannelViewModel viewModel;

    public ChannelViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (CardChannelBinding) binding;
    }

    @Override
    public void onBind(Context context, BaseOnClickListener onClickListener, Channel item) {
        attachOnClickListener((ListRowOnClickListener) onClickListener, item);

        viewModel = new ChannelViewModel(item);
        binding.setViewModel(viewModel);

        // Immediate Binding
        // When a variable or observable changes, the binding will be scheduled to change before
        // the next frame. There are times, however, when binding must be executed immediately.
        // To force execution, use the executePendingBindings() method.
        binding.executePendingBindings();
    }

    private void attachOnClickListener(ListRowOnClickListener onClickListener, Channel item) {
        if (onClickListener != null) {
            itemView.setOnClickListener(view -> onClickListener.onClick(item, view));
        }
    }
}