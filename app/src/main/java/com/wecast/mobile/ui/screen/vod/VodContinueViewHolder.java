package com.wecast.mobile.ui.screen.vod;

import android.content.Context;
import android.view.View;

import com.wecast.core.data.db.entities.Vod;
import com.wecast.mobile.databinding.CardVodContinueWatchingBinding;
import com.wecast.mobile.ui.base.BaseOnClickListener;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class VodContinueViewHolder extends BaseViewHolder<Vod> {

    private CardVodContinueWatchingBinding binding;
    private VodViewModel viewModel;

    public VodContinueViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (CardVodContinueWatchingBinding) binding;
    }

    @Override
    public void onBind(Context context, BaseOnClickListener onClickListener, Vod item) {
        attachOnClickListener((ListRowOnClickListener) onClickListener, item);

        viewModel = new VodViewModel(item);
        binding.setViewModel(viewModel);

        if (item.getContinueWatching() != null) {
            binding.progress.setProgress((int) item.getContinueWatching().getStoppedTime());
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

    private void attachOnClickListener(ListRowOnClickListener onClickListener, Vod item) {
        if (onClickListener != null) {
            itemView.setOnClickListener(view -> onClickListener.onClick(item, view));
        }
    }
}