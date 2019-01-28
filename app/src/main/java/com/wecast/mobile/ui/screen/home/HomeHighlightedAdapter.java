package com.wecast.mobile.ui.screen.home;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wecast.core.data.db.entities.Highlighted;
import com.wecast.mobile.databinding.CardHighlightedBinding;
import com.wecast.mobile.ui.ScreenRouter;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public class HomeHighlightedAdapter extends PagerAdapter implements HomeHighlightedViewModel.OnClickListener {

    private Context context;
    private List<Highlighted> items;

    HomeHighlightedAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup viewGroup, int position) {
        // Inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        CardHighlightedBinding binding = CardHighlightedBinding.inflate(inflater, viewGroup, false);
        // Create view model
        HomeHighlightedViewModel viewModel = new HomeHighlightedViewModel(items.get(position), this);
        binding.setViewModel(viewModel);
        // Add item to view pager
        ViewPager viewPager = (ViewPager) viewGroup;
        viewPager.addView(binding.getRoot(), 0);
        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<Highlighted> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Highlighted item) {
        ScreenRouter.openHighlighted(context, item);
    }
}
