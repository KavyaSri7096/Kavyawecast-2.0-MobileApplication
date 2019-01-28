package com.wecast.mobile.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by ageech@live.com
 */

public class LanguagePicker extends android.widget.NumberPicker {

    public LanguagePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    private void updateView(View view) {
        if (view instanceof EditText) {
            ((EditText) view).setTextSize(22);
            ((EditText) view).setTextColor(Color.WHITE);
            ((EditText) view).setCursorVisible(false);
            ((EditText) view).setEnabled(false);
            ((EditText) view).setFocusable(false);
            ((EditText) view).setFocusableInTouchMode(false);
        }
    }
}