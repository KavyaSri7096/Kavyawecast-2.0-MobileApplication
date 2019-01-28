package com.wecast.mobile.ui.screen.show.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.wecast.core.data.db.entities.TVShowGenre;
import com.wecast.mobile.databinding.CardTvShowGenreDetailsBinding;

/**
 * Created by ageech@live.com
 */

public class TVShowDetailsGenreView extends FrameLayout implements TVShowDetailsGenreViewModel.OnClickListener {

    private TVShowGenre tvShowGenre;

    public TVShowDetailsGenreView(Context context, TVShowGenre tvShowGenre) {
        super(context);
        this.tvShowGenre = tvShowGenre;
        initialize(context);
    }

    public TVShowDetailsGenreView(Context context) {
        super(context);
        initialize(context);
    }

    public TVShowDetailsGenreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public TVShowDetailsGenreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        CardTvShowGenreDetailsBinding binding = CardTvShowGenreDetailsBinding.inflate(inflater, this, true);
        TVShowDetailsGenreViewModel viewModel = new TVShowDetailsGenreViewModel(tvShowGenre, this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public void onItemClick(TVShowGenre item) {

    }
}
