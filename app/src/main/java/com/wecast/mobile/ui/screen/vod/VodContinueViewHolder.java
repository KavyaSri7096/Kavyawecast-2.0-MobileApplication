package com.wecast.mobile.ui.screen.vod;

import android.content.Context;
import android.view.View;

import com.wecast.core.data.db.entities.ShowType;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.mobile.databinding.CardVodContinueWatchingBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.common.adapter.ItemMultiChoiceAdapter;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class VodContinueViewHolder extends BaseViewHolder<Vod> implements VodViewModel.OnClickListener {

    private CardVodContinueWatchingBinding binding;
    private VodViewModel viewModel;

    public VodContinueViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (CardVodContinueWatchingBinding) binding;
    }

    @Override
    public void onBind(Vod item) {
        viewModel = new VodViewModel(item, this);
        binding.setViewModel(viewModel);

        if (item.getContinueWatching() != null) {
            binding.progress.setProgress(item.getContinueWatching().getStoppedTime());
            binding.progress.setMax((int) item.getContinueWatching().getDuration());
        } else {
            binding.progress.setVisibility(View.GONE);
        }

        // Immediate Binding
        // When a variable or observable changes, the binding will be scheduled to change before
        // the next frame. There are times, however, when binding must be executed immediately.
        // To force execution, use the executePendingBindings() method.
        binding.executePendingBindings();
    }

    @Override
    public void onItemClick(Vod item) {
        if (item != null) {
            Context context = binding.getRoot().getContext();
            ScreenRouter.continuePlaying(context, item);
        }
    }
}