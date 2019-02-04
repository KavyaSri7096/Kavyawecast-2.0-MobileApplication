package com.wecast.mobile.ui.screen.live.guide;

import android.content.Context;

import com.wecast.core.data.db.dao.ChannelDao;
import com.wecast.core.data.db.entities.TVGuide;
import com.wecast.mobile.databinding.CardTvGuideBinding;
import com.wecast.mobile.ui.base.BaseOnClickListener;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;

import javax.inject.Inject;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class TVGuideViewHolder extends BaseViewHolder<TVGuide> {

    private CardTvGuideBinding binding;
    private TVGuideViewModel viewModel;

    @Inject
    ChannelDao channelDao;

    public TVGuideViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (CardTvGuideBinding) binding;
    }

    @Override
    public void onBind(Context context, BaseOnClickListener onClickListener, TVGuide item) {
        attachOnClickListener((ListRowOnClickListener) onClickListener, item);

        viewModel = new TVGuideViewModel(item);
        binding.setViewModel(viewModel);

        // Immediate Binding
        // When a variable or observable changes, the binding will be scheduled to change before
        // the next frame. There are times, however, when binding must be executed immediately.
        // To force execution, use the executePendingBindings() method.
        binding.executePendingBindings();
    }

    private void attachOnClickListener(ListRowOnClickListener onClickListener, TVGuide item) {
        if (onClickListener != null) {
            itemView.setOnClickListener(view -> onClickListener.onClick(item, view));
        }
    }
}