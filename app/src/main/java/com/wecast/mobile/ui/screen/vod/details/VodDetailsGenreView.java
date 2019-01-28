package com.wecast.mobile.ui.screen.vod.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.wecast.core.data.db.entities.VodGenre;
import com.wecast.mobile.databinding.CardVodGenreDetailsBinding;

/**
 * Created by ageech@live.com
 */

public class VodDetailsGenreView extends FrameLayout implements VodDetailsGenreViewModel.OnClickListener {

    private VodGenre vodGenre;

    public VodDetailsGenreView(Context context, VodGenre vodGenre) {
        super(context);
        this.vodGenre = vodGenre;
        initialize(context);
    }

    public VodDetailsGenreView(Context context) {
        super(context);
        initialize(context);
    }

    public VodDetailsGenreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public VodDetailsGenreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        CardVodGenreDetailsBinding binding = CardVodGenreDetailsBinding.inflate(inflater, this, true);
        VodDetailsGenreViewModel viewModel = new VodDetailsGenreViewModel(vodGenre, this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    @Override
    public void onItemClick(VodGenre item) {

    }
}
