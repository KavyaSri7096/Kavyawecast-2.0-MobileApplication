package com.wecast.mobile.ui.widget.listRow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.wecast.mobile.WeApp;
import com.wecast.mobile.databinding.WidgetListRowPagerBinding;
import com.wecast.mobile.di.component.AppComponent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by ageech@live.com
 */

public abstract class ListRowPager extends BaseListRow {

    private WidgetListRowPagerBinding binding;

    public ListRowPager(@NonNull Context context) {
        super(context);
        initialize(context, null);
    }

    public ListRowPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public ListRowPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = WidgetListRowPagerBinding.inflate(inflater, this, true);
        setVisibility(GONE);

        // Set row title
        String title = title();
        if (title != null) {
            binding.title.setText(title);
        }

        // Set title start margin
        if (titleStartMargin() > 0) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.title.getLayoutParams();
            params.setMarginStart(titleStartMargin());
        }

        // Set content start margin
        if (dataStartMargin() > 0) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.data.getLayoutParams();
            params.setMarginStart(dataStartMargin());
        }

        binding.data.setNestedScrollingEnabled(false);
        binding.data.setClipToPadding(false);
        binding.data.setPageMargin(0);

        // Set page transformer
        ViewPager.PageTransformer pageTransformer = pageTransformer();
        if (pageTransformer != null) {
            binding.data.setPageTransformer(true, pageTransformer);
        }

        // Set offscreen limit
        if (offScreenPageLimit() != -1) {
            binding.data.setOffscreenPageLimit(offScreenPageLimit());
        }

        // Set adapter
        PagerAdapter adapter = adapter();
        if (adapter != null) {
            binding.data.setAdapter(adapter);
        }
    }

    protected abstract String title();

    protected abstract int titleStartMargin();

    protected abstract int dataStartMargin();

    protected abstract int offScreenPageLimit();

    protected abstract ViewPager.PageTransformer pageTransformer();

    protected abstract PagerAdapter adapter();

    protected abstract void inject(AppComponent appComponent);

    /**
     * And items to adapter, and then show title
     * (Data and title will be visible at the same time)
     */
    public void showTitle() {
        binding.title.setVisibility(VISIBLE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        inject(WeApp.getInstance().getAppComponent());
    }
}
