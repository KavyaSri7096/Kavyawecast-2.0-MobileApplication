package com.wecast.mobile.ui.screen.show.genre;

import android.content.Context;

import com.wecast.core.data.db.entities.ShowType;
import com.wecast.core.data.db.entities.TVShowGenre;
import com.wecast.mobile.databinding.CardTvShowGenreBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseViewHolder;
import com.wecast.mobile.ui.common.adapter.ItemMultiChoiceAdapter;

import androidx.databinding.ViewDataBinding;

/**
 * Created by ageech@live.com
 */

public class TVShowGenreViewHolder extends BaseViewHolder<TVShowGenre> implements TVShowGenreViewModel.OnClickListener {

    private CardTvShowGenreBinding binding;
    private TVShowGenreViewModel viewModel;

    public TVShowGenreViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = (CardTvShowGenreBinding) binding;
    }

    @Override
    public void onBind(TVShowGenre item) {
        // Setup binding
        viewModel = new TVShowGenreViewModel(item, this);
        binding.setViewModel(viewModel);

        // Immediate Binding
        // When a variable or observable changes, the binding will be scheduled to change before
        // the next frame. There are times, however, when binding must be executed immediately.
        // To force execution, use the executePendingBindings() method.
        binding.executePendingBindings();
    }

    @Override
    public void onItemClick(TVShowGenre item) {
        if (item != null) {
            Context context = binding.getRoot().getContext();
            ScreenRouter.openTVShowByGenre(context, item);
        }
    }
}
