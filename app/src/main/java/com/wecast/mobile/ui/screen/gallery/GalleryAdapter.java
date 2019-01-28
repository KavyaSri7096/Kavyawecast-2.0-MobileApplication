package com.wecast.mobile.ui.screen.gallery;

import android.content.Context;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wecast.core.data.db.entities.VodImage;
import com.wecast.mobile.databinding.CardGalleryBinding;
import com.wecast.mobile.databinding.CardGalleryPreviewBinding;
import com.wecast.mobile.utils.BindingUtils;

import java.util.List;

/**
 * Created by ageech@live.com
 */

public class GalleryAdapter extends PagerAdapter {

    private Context context;
    private GalleryType galleryType;
    private GalleryOnClickLiIstener clickListener;
    private List<VodImage> items;

    public GalleryAdapter(Context context, GalleryType cardType) {
        this.context = context;
        this.galleryType = cardType;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup viewGroup, int position) {
        // Get item
        VodImage item = items.get(position);
        // Inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewDataBinding binding;
        if (galleryType == GalleryType.CARD) {
            binding = CardGalleryBinding.inflate(inflater, viewGroup, false);
            BindingUtils.bindGallery(((CardGalleryBinding) binding).image, item.getPreviewAr());
        } else if (galleryType == GalleryType.PREVIEW) {
            binding = CardGalleryPreviewBinding.inflate(inflater, viewGroup, false);
            BindingUtils.bindGalleryPreview(((CardGalleryPreviewBinding) binding).image, item.getPreviewAr());
        } else {
            throw new NullPointerException("Please provide correct Gallery Type!");
        }
        // Add click listener
        binding.getRoot().setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(position);
            }
        });
        // Add view to pager
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

    public void setItems(List<VodImage> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setListener(GalleryOnClickLiIstener clickListener) {
        this.clickListener = clickListener;
    }
}


