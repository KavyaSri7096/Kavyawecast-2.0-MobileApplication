package com.wecast.mobile.ui.widget.listRow;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.wecast.mobile.R;

import androidx.annotation.Nullable;

/**
 * Created by ageech@live.com
 */

public class ListRowContainer extends LinearLayout {

    private int emptyLayoutResourceId;

    public ListRowContainer(Context context) {
        super(context);
        initialize(context, null);
    }

    public ListRowContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public ListRowContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ListRowContainer);
            emptyLayoutResourceId = typedArray.getResourceId(R.styleable.ListRowContainer_emptyLayout, -1);
            typedArray.recycle();
        }
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (getChildCount() > 0) {
            return;
        }

        // Inflate empty layout
        if (emptyLayoutResourceId != -1) {
            setGravity(Gravity.CENTER);
            View view = View.inflate(getContext(), emptyLayoutResourceId, null);
            addView(view);
        }
    }
}
