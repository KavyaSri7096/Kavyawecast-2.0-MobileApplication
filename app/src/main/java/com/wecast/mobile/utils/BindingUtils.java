package com.wecast.mobile.utils;

import androidx.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.wecast.mobile.Constants;
import com.wecast.mobile.R;

/**
 * Created by ageech@live.com
 */

public final class BindingUtils {

    public static void bindAppLogo(ImageView view, String logoUrl) {
        GlideApp.with(view.getContext())
                .load(Constants.BASE_API_URL + "/" + logoUrl)
                //.load(R.drawable.app_logo)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(view);
    }

    @BindingAdapter({"highlightedUrl"})
    public static void bindHighlighted(ImageView view, String imageUrl) {
        GlideApp.with(view.getContext())
                .load(imageUrl)
                .error(R.drawable.placeholder_banner)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
    }

    @BindingAdapter({"coverUrl"})
    public static void bindCover(ImageView view, String imageUrl) {
        GlideApp.with(view.getContext())
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
    }

    @BindingAdapter({"bannerUrl"})
    public static void bindBanner(ImageView view, String imageUrl) {
        GlideApp.with(view.getContext())
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
    }

    @BindingAdapter({"logoUrl"})
    public static void bindLogo(ImageView view, String imageUrl) {
        GlideApp.with(view.getContext())
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
    }

    @BindingAdapter({"screenshotUrl"})
    public static void bindScreenshot(ImageView view, String imageUrl) {
        GlideApp.with(view.getContext())
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
    }

    @BindingAdapter({"galleryUrl"})
    public static void bindGallery(ImageView view, String imageUrl) {
        GlideApp.with(view.getContext())
                .load(imageUrl)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
    }

    @BindingAdapter({"galleryPreviewUrl"})
    public static void bindGalleryPreview(ImageView view, String imageUrl) {
        GlideApp.with(view.getContext())
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
    }
}
