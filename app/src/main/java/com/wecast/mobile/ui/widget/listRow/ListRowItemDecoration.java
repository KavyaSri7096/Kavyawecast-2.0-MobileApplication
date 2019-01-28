package com.wecast.mobile.ui.widget.listRow;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import com.wecast.mobile.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import static androidx.recyclerview.widget.RecyclerView.State;

/**
 * Created by ageech@live.com
 */

public class ListRowItemDecoration extends ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
        super.getItemOffsets(outRect, view, parent, state);

        Context context = view.getContext();
        if (parent.getChildAdapterPosition(view) == 0) {
            if (context.getResources().getBoolean(R.bool.is_right_to_left)) {
                outRect.right = context.getResources().getDimensionPixelOffset(R.dimen.standard);
            } else {
                outRect.left = context.getResources().getDimensionPixelOffset(R.dimen.standard);
            }
        }
    }
}
