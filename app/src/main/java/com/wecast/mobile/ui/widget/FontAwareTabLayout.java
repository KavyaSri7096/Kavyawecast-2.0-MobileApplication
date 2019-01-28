package com.wecast.mobile.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by ageech@live.com
 */

public class FontAwareTabLayout extends TabLayout {

    public FontAwareTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        super.addTab(tab, position, setSelected);

        ViewGroup mainView = (ViewGroup) getChildAt(0);
        ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());
        int tabChildCount = tabView.getChildCount();
        for (int i = 0; i < tabChildCount; i++) {
            View tabViewChild = tabView.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                CalligraphyUtils.applyFontToTextView(getContext(), (TextView) tabViewChild, "fonts/helvetica_regular.ttf");
            }
        }
    }
}