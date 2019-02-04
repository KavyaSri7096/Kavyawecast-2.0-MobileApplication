package com.wecast.mobile.ui.screen.vod.genre;

import android.content.Context;

import com.wecast.core.data.db.entities.VodGenre;
import com.wecast.mobile.databinding.CardVodGenreBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseOnClickListener;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class VodGenreViewHolder extends BaseViewHolder<VodGenre> {

    private CardVodGenreBinding binding;
    private VodGenreViewModel viewModel;

    public VodGenreViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (CardVodGenreBinding) binding;
    }

    @Override
    public void onBind(Context context, BaseOnClickListener onClickListener, VodGenre item) {
        attachOnClickListener((ListRowOnClickListener) onClickListener, item);

        viewModel = new VodGenreViewModel(item);
        binding.setViewModel(viewModel);

        // Immediate Binding
        // When a variable or observable changes, the binding will be scheduled to change before
        // the next frame. There are times, however, when binding must be executed immediately.
        // To force execution, use the executePendingBindings() method.
        binding.executePendingBindings();
    }

    private void attachOnClickListener(ListRowOnClickListener onClickListener, VodGenre item) {
        if (onClickListener != null) {
            itemView.setOnClickListener(view -> onClickListener.onClick(item, view));
        }
    }
}