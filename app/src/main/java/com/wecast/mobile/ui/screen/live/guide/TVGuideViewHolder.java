package com.wecast.mobile.ui.screen.live.guide;

import android.content.Context;

import com.wecast.core.data.db.entities.ShowType;
import com.wecast.core.data.db.entities.TVGuide;
import com.wecast.mobile.databinding.CardTvGuideBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.common.adapter.ItemMultiChoiceAdapter;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class TVGuideViewHolder extends BaseViewHolder<TVGuide> implements TVGuideViewModel.OnClickListener {

    private CardTvGuideBinding binding;
    private TVGuideViewModel viewModel;

    public TVGuideViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (CardTvGuideBinding) binding;
    }

    @Override
    public void onBind(TVGuide item) {
        viewModel = new TVGuideViewModel(item, this);
        binding.setViewModel(viewModel);

        // Immediate Binding
        // When a variable or observable changes, the binding will be scheduled to change before
        // the next frame. There are times, however, when binding must be executed immediately.
        // To force execution, use the executePendingBindings() method.
        binding.executePendingBindings();
    }

    @Override
    public void onItemClick(TVGuide item) {
        if (item != null) {
            Context context = binding.getRoot().getContext();
            ScreenRouter.openChannelDetails(context, item.getId());
        }
    }
}