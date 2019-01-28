package com.wecast.mobile.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import androidx.appcompat.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.wecast.mobile.R;
import com.wecast.core.utils.ScreenUtils;
import com.wecast.core.utils.ViewUtils;

/**
 * Created by ageech@live.com
 */

public class FilterSpinner extends androidx.appcompat.widget.AppCompatSpinner {

    private AlertDialog dialog;

    public FilterSpinner(Context context) {
        super(context);
    }

    public FilterSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public boolean performClick() {
        Context context = getContext();

        // Create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        CharSequence prompt = getPrompt();
        if (prompt != null) {
            builder.setTitle(prompt);
        }

        // Show dialog
        dialog = builder
                .setSingleChoiceItems(new DropDownAdapter(getAdapter()), getSelectedItemPosition(), this)
                .show();

        // Set dialog layout params
        if (dialog.getWindow() != null) {
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            // Width and height must be set to anything other than WRAP_CONTENT!
            //layoutParams.x = 200;
            //layoutParams.y = -270;
            layoutParams.x = calculateX();
            layoutParams.y = -calculateY();
            layoutParams.height = LayoutParams.WRAP_CONTENT;
            layoutParams.width = ViewUtils.dpToPx(200);
            layoutParams.horizontalMargin = 0;
            layoutParams.verticalMargin = 0;
            dialog.getWindow().setAttributes(layoutParams);
        }

        //ListView.getDefaultSize(size, measureSpec)
        ListView listView = dialog.getListView();

        // Remove divider between rows
        listView.setDivider(null);

        // Set custom background
        listView.setBackgroundResource(R.drawable.bg_spinner);

        // Remove background from all (grand)parent's
        ViewParent parent = listView.getParent();
        while (parent instanceof View) {
            ((View) parent).setBackgroundResource(R.color.transparent);
            parent = parent.getParent();
        }

        return true;
    }

    private int calculateX() {
        return ScreenUtils.getScreenWidth(getContext());
    }

    private int calculateY() {
        int statusBarHeight = ScreenUtils.getStatusBarHeight(getContext());
        int actionBarHeight = ScreenUtils.getActionBarHeight(getContext());
        return (statusBarHeight * 2 + actionBarHeight) + 50;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        setSelection(which);
        dialog.dismiss();
        this.dialog = null;
    }

    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {

        private SpinnerAdapter adapter;

        /**
         * <p>Creates a new List Adapter wrapper for the specified adapter.</p>
         *
         * @param adapter the Adapter to transform into a ListAdapter
         */
        public DropDownAdapter(SpinnerAdapter adapter) {
            this.adapter = adapter;
        }

        public int getCount() {
            return adapter == null ? 0 : adapter.getCount();
        }

        public Object getItem(int position) {
            return adapter == null ? null : adapter.getItem(position);
        }

        public long getItemId(int position) {
            return adapter == null ? -1 : adapter.getItemId(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return getDropDownView(position, convertView, parent);
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return adapter == null ? null : adapter.getDropDownView(position, convertView, parent);
        }

        public boolean hasStableIds() {
            return adapter != null && adapter.hasStableIds();
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            if (adapter != null) {
                adapter.registerDataSetObserver(observer);
            }
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (adapter != null) {
                adapter.unregisterDataSetObserver(observer);
            }
        }

        /**
         * <p>Always returns false.</p>
         *
         * @return false
         */
        public boolean areAllItemsEnabled() {
            return true;
        }

        /**
         * <p>Always returns false.</p>
         *
         * @return false
         */
        public boolean isEnabled(int position) {
            return true;
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }

    }
}