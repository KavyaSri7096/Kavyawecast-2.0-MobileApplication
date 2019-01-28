package com.wecast.mobile.ui.screen.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.entities.VodImage;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityGalleryBinding;
import com.wecast.mobile.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;

/**
 * Created by ageech@live.com
 */

public class GalleryActivity extends BaseActivity<ActivityGalleryBinding, GalleryActivityViewModel> implements GalleryActivityNavigator {

    @Inject
    GalleryActivityViewModel viewModel;

    private ActivityGalleryBinding binding;
    private int id;
    private int type;
    private int position;

    public static void open(Context context, int id, int type, int position) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra("ID", id);
        intent.putExtra("TYPE", type);
        intent.putExtra("POSITION", position);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_gallery;
    }

    @Override
    public GalleryActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        // Set background color
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        // Set status bar color
        setStatusTransparent(this);
        setDarkMode(this);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getIntExtra("ID", 0);
            type = intent.getIntExtra("TYPE", 0);
            position = intent.getIntExtra("POSITION", 0);
        }

        List<VodImage> images;
        if (type == 0) {
            images = getImages(viewModel.getVodById(id));
        } else {
            images = getImages(viewModel.getTVShowById(id));
        }
        GalleryAdapter adapter = new GalleryAdapter(this, GalleryType.PREVIEW);
        adapter.setItems(images);
        binding.gallery.setAdapter(adapter);
        binding.galleryIndicator.setViewPager(binding.gallery);

        // Displaying selected image first
        binding.gallery.setCurrentItem(position);
    }

    private void setupListeners() {
        binding.back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, android.R.anim.fade_out);
        });
    }

    private List<VodImage> getImages(Vod vod) {
        List<VodImage> images = new ArrayList<>();
        if (vod.getBanners() != null && vod.getBanners().size() > 0) {
            images.addAll(vod.getBanners());
        }
        if (vod.getGallery() != null && vod.getGallery().size() > 0) {
            images.addAll(vod.getGallery());
        }
        return images;
    }

    private List<VodImage> getImages(TVShow tvShow) {
        List<VodImage> images = new ArrayList<>();
        if (tvShow.getBanners() != null && tvShow.getBanners().size() > 0) {
            images.addAll(tvShow.getBanners());
        }
        if (tvShow.getGallery() != null && tvShow.getGallery().size() > 0) {
            images.addAll(tvShow.getGallery());
        }
        return images;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, android.R.anim.fade_out);
    }
}
