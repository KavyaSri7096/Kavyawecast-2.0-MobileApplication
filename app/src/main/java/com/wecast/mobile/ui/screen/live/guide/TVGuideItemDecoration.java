package com.wecast.mobile.ui.screen.live.guide;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import com.wecast.core.utils.ScreenUtils;
import com.wecast.core.utils.ViewUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by ageech@live.com
 */

public class TVGuideItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        Context context = view.getContext();
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = ScreenUtils.getActionBarHeight(context) + ViewUtils.dpToPx(8);
        }
    }
}
