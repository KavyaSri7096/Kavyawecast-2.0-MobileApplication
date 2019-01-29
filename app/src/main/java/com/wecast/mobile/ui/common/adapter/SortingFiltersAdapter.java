package com.wecast.mobile.ui.common.adapter;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wecast.mobile.R;

/**
 * Created by ageech@live.com
 */

public class SortingFiltersAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] items;

    public SortingFiltersAdapter(@NonNull Context context, String[] items) {
        super(context, R.layout.component_drop_down, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.component_filter, parent, false);
        }
        TextView textView = view.findViewById(R.id.name);
        textView.setText(items[position]);
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView) view;
        if (position == 0) {
            Resources resources = context.getResources();
            textView.setTextColor(resources.getColor(R.color.gray));
        }
        return view;
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(R.layout.component_drop_down);
    }
}
