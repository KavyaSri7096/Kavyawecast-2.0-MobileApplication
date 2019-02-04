package com.wecast.mobile.ui.screen.show;

import android.content.Context;

import com.wecast.core.data.db.entities.TVShow;
import com.wecast.mobile.databinding.CardTvShowBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseOnClickListener;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.widget.listRow.ListRowOnClickListener;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class TVShowViewHolder extends BaseViewHolder<TVShow> {

    private CardTvShowBinding binding;
    private TVShowViewModel viewModel;

    public TVShowViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (CardTvShowBinding) binding;
    }

    @Override
    public void onBind(Context context, BaseOnClickListener onClickListener, TVShow item) {
        attachOnClickListener((ListRowOnClickListener) onClickListener, item);

        viewModel = new TVShowViewModel(item);
        binding.setViewModel(viewModel);

        // Immediate Binding
        // When a variable or observable changes, the binding will be scheduled to change before
        // the next frame. There are times, however, when binding must be executed immediately.
        // To force execution, use the executePendingBindings() method.
        binding.executePendingBindings();
    }

    private void attachOnClickListener(ListRowOnClickListener onClickListener, TVShow item) {
        if (onClickListener != null) {
            itemView.setOnClickListener(view -> onClickListener.onClick(item, view));
        }
    }
}