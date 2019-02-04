package com.wecast.mobile.ui.widget.listRow;

import android.view.View;

import com.wecast.mobile.ui.base.BaseOnClickListener;

/**
 * Created by ageech@live.com
 */

public interface ListRowOnClickListener<T> extends BaseOnClickListener {

    void onClick(T item, View view);
}
