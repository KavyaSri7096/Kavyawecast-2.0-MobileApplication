package com.wecast.mobile.ui.screen.show.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.wecast.core.data.db.entities.ShowType;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.CardTvShowShowTypeBinding;

/**
 * Created by ageech@live.com
 */

public class TVShowDetailsShowTypeView extends FrameLayout implements TVShowDetailsShowTypeViewModel.OnClickListener {

    private CardTvShowShowTypeBinding binding;
    private ShowType showType;

    public TVShowDetailsShowTypeView(Context context, ShowType showType) {
        super(context);
        this.showType = showType;
        initialize(context);
    }

    public TVShowDetailsShowTypeView(Context context) {
        super(context);
        initialize(context);
    }

    public TVShowDetailsShowTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public TVShowDetailsShowTypeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = CardTvShowShowTypeBinding.inflate(inflater, this, true);
        TVShowDetailsShowTypeViewModel viewModel = new TVShowDetailsShowTypeViewModel(showType, this);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();
    }

    public void hasDivider(boolean hasDivider) {
        if (hasDivider) {
            binding.name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_dot_active, 0);
        }
    }

    @Override
    public void onItemClick(ShowType item) {

    }
}
