package com.wecast.mobile.ui.screen.vod.genre;

import android.content.Context;

import com.wecast.core.data.db.entities.VodGenre;
import com.wecast.mobile.databinding.CardVodGenreBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseViewHolder;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class VodGenreViewHolder extends BaseViewHolder<VodGenre> implements VodGenreViewModel.OnClickListener {

    private CardVodGenreBinding binding;
    private VodGenreViewModel viewModel;

    public VodGenreViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (CardVodGenreBinding) binding;
    }

    @Override
    public void onBind(VodGenre item) {
        // Setup binding
        viewModel = new VodGenreViewModel(item, this);
        binding.setViewModel(viewModel);

        // Immediate Binding
        // When a variable or observable changes, the binding will be scheduled to change before
        // the next frame. There are times, however, when binding must be executed immediately.
        // To force execution, use the executePendingBindings() method.
        binding.executePendingBindings();
    }

    @Override
    public void onItemClick(VodGenre item) {
        if (item != null) {
            Context context = binding.getRoot().getContext();
            ScreenRouter.openVodByGenre(context, item);
        }
    }
}