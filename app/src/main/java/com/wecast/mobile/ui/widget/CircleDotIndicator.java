package com.wecast.mobile.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wecast.mobile.R;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ageech@live.com
 */

public class CircleDotIndicator extends FlowLayout {

    private static final int DEFAULT_SELECTED_POINT_COLOR = Color.WHITE;
    private static final int DEFAULT_UN_SELECTED_POINT_COLOR = Color.LTGRAY;
    private static final float DEFAULT_WIDTH_FACTOR = 1.0f;

    private List<ImageView> dots;
    private ViewPager viewPager;
    private float dotsSize;
    private float dotsCornerRadius;
    private float dotsSpacing;
    private int currentPage;
    private float dotsWidthFactor;
    private int selectedDotColor;
    private int unSelectedDotColor;

    private boolean dotsClickable;
    private ViewPager.OnPageChangeListener pageChangedListener;

    public CircleDotIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public CircleDotIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleDotIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        dots = new ArrayList<>();

        setOrientation(HORIZONTAL);

        dotsSize = dpToPx(8);
        dotsSpacing = dpToPx(4);
        dotsCornerRadius = dotsSize / 2;
        dotsWidthFactor = DEFAULT_WIDTH_FACTOR;
        selectedDotColor = DEFAULT_SELECTED_POINT_COLOR;
        unSelectedDotColor = DEFAULT_UN_SELECTED_POINT_COLOR;
        dotsClickable = true;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleDotIndicator);
            selectedDotColor = a.getColor(R.styleable.CircleDotIndicator_selectedDotColor, DEFAULT_SELECTED_POINT_COLOR);
            unSelectedDotColor = a.getColor(R.styleable.CircleDotIndicator_unSelectedDotColor, DEFAULT_UN_SELECTED_POINT_COLOR);
            setUpCircleColors(unSelectedDotColor);
            dotsWidthFactor = a.getFloat(R.styleable.CircleDotIndicator_dotsWidthFactor, DEFAULT_WIDTH_FACTOR);
            if (dotsWidthFactor < 1) {
                dotsWidthFactor = 1.5f;
            }
            dotsSize = a.getDimension(R.styleable.CircleDotIndicator_dotsSize, dotsSize);
            dotsCornerRadius = (int) a.getDimension(R.styleable.CircleDotIndicator_dotsCornerRadius, dotsSize / 2);
            dotsSpacing = a.getDimension(R.styleable.CircleDotIndicator_dotsSpacing, dotsSpacing);
            a.recycle();
        } else {
            setUpCircleColors(DEFAULT_UN_SELECTED_POINT_COLOR);
        }

        if (isInEditMode()) {
            addDots(5);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refreshDots();
    }

    private void refreshDots() {
        if (viewPager != null && viewPager.getAdapter() != null) {
            // Check if we need to refresh the dots count
            if (dots.size() < viewPager.getAdapter().getCount()) {
                addDots(viewPager.getAdapter().getCount() - dots.size());
            } else if (dots.size() > viewPager.getAdapter().getCount()) {
                removeDots(dots.size() - viewPager.getAdapter().getCount());
            }
            setUpDotsAnimators();
        }
    }

    private void addDots(int count) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < count; i++) {
            View dot = inflater.inflate(R.layout.component_dot, this, false);
            ImageView imageView = dot.findViewById(R.id.dot);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            params.width = params.height = (int) dotsSize;
            params.setMargins((int) dotsSpacing, 0, (int) dotsSpacing, (int) dotsSpacing);
            ((GradientDrawable) imageView.getBackground()).setCornerRadius(dotsCornerRadius);
            ((GradientDrawable) imageView.getBackground()).setColor(unSelectedDotColor);

            final int finalI = i;
            dot.setOnClickListener(v -> {
                if (dotsClickable && viewPager != null && viewPager.getAdapter() != null && finalI < viewPager.getAdapter().getCount()) {
                    viewPager.setCurrentItem(finalI, true);
                }
            });

            dots.add(imageView);
            addView(dot);
        }
    }

    private void removeDots(int count) {
        for (int i = 0; i < count; i++) {
            removeViewAt(getChildCount() - 1);
            dots.remove(dots.size() - 1);
        }
    }

    private void setUpDotsAnimators() {
        if (viewPager != null && viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 0) {
            if (currentPage < dots.size()) {
                View dot = dots.get(currentPage);
                if (dot != null) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dot.getLayoutParams();
                    params.width = (int) dotsSize;
                    dot.setLayoutParams(params);
                }
            }

            currentPage = viewPager.getCurrentItem();
            if (currentPage >= dots.size()) {
                currentPage = dots.size() - 1;
                viewPager.setCurrentItem(currentPage, false);
            }

            View dot = dots.get(currentPage);
            if (dot != null) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dot.getLayoutParams();
                params.width = (int) (dotsSize * dotsWidthFactor);
                params.height = (int) (dotsSize * dotsWidthFactor);
                dot.setLayoutParams(params);
            }

            if (pageChangedListener != null) {
                viewPager.removeOnPageChangeListener(pageChangedListener);
            }

            setUpOnPageChangedListener();
            viewPager.addOnPageChangeListener(pageChangedListener);
        }
    }

    private void setUpOnPageChangedListener() {
        pageChangedListener = new ViewPager.OnPageChangeListener() {
            private int lastPage;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position != currentPage && positionOffset == 0 || currentPage < position) {
                    setDotSize(dots.get(currentPage), (int) dotsSize);
                    setDotColor(dots.get(currentPage), unSelectedDotColor);
                    currentPage = position;
                }

                if (Math.abs(currentPage - position) > 1) {
                    setDotSize(dots.get(currentPage), (int) dotsSize);
                    setDotColor(dots.get(currentPage), unSelectedDotColor);
                    currentPage = lastPage;
                }

                ImageView dot = dots.get(currentPage);

                ImageView nextDot = null;
                if (currentPage == position && currentPage + 1 < dots.size()) {
                    nextDot = dots.get(currentPage + 1);
                } else if (currentPage > position) {
                    nextDot = dot;
                    dot = dots.get(currentPage - 1);
                }

                int dotSize = (int) (dotsSize + (dotsSize * (dotsWidthFactor - 1) * (1 - positionOffset)));
                setDotSize(dot, dotSize);
                setDotColor(dot, selectedDotColor);

                if (nextDot != null) {
                    int nextDotSize = (int) (dotsSize + (dotsSize * (dotsWidthFactor - 1) * (positionOffset)));
                    setDotSize(nextDot, nextDotSize);
                    setDotColor(nextDot, unSelectedDotColor);
                }

                lastPage = position;
            }

            private void setDotSize(ImageView dot, int dotSize) {
                ViewGroup.LayoutParams dotParams = dot.getLayoutParams();
                dotParams.width = dotSize;
                dotParams.height = dotSize;
                dot.setLayoutParams(dotParams);
            }

            private void setDotColor(ImageView dot, int color) {
                ((GradientDrawable) dot.getBackground()).setColor(color);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
    }

    private void setUpCircleColors(int color) {
        if (dots != null) {
            for (ImageView elevationItem : dots) {
                ((GradientDrawable) elevationItem.getBackground()).setColor(color);
            }
        }
    }

    private void setUpViewPager() {
        if (viewPager.getAdapter() != null) {
            viewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    refreshDots();
                }
            });
        }
    }

    private int dpToPx(int dp) {
        return (int) (getContext().getResources().getDisplayMetrics().density * dp);
    }

    public void setPointsColor(int color) {
        setUpCircleColors(color);
    }

    public void setDotsClickable(boolean dotsClickable) {
        this.dotsClickable = dotsClickable;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        setUpViewPager();
        refreshDots();
    }
}