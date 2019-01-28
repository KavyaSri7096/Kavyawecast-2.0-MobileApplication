package com.wecast.mobile.ui.screen.show.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wecast.core.data.db.entities.ShowType;
import com.wecast.core.data.db.entities.TVShow;
import com.wecast.core.data.db.entities.TVShowGenre;
import com.wecast.core.data.db.entities.TVShowSeason;
import com.wecast.core.data.db.entities.VodImage;
import com.wecast.core.utils.ViewUtils;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityTvShowDetailsBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.screen.gallery.GalleryActivity;
import com.wecast.mobile.ui.screen.gallery.GalleryAdapter;
import com.wecast.mobile.ui.screen.gallery.GalleryType;
import com.wecast.mobile.ui.screen.show.TVShowNavigator;
import com.wecast.mobile.utils.CommonUtils;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class TVShowDetailsActivity extends BaseActivity<ActivityTvShowDetailsBinding, TVShowDetailsActivityViewModel> implements TVShowNavigator {

    @Inject
    TVShowDetailsActivityViewModel viewModel;

    private ActivityTvShowDetailsBinding binding;
    private TVShow tvShow;

    public static void open(Context context, TVShow tvShow) {
        Intent intent = new Intent(context, TVShowDetailsActivity.class);
        intent.putExtra("ID", tvShow.getId());
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tv_show_details;
    }

    @Override
    public TVShowDetailsActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        setStatusTransparent(this);
        setDarkMode(this);
        binding = getViewDataBinding();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int id = bundle.getInt("ID");
            getByID(id);
        }
    }

    private void setupListeners() {
        binding.gallery.back.setOnClickListener(v -> onBackPressed());
        binding.gallery.trailer.setOnClickListener(v -> ScreenRouter.openTVShowPlayer(this, tvShow));
    }

    private void getByID(int id) {
        Disposable disposable = viewModel.getByID(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        showData(response);
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void showData(TVShow tvShow) {
        this.tvShow = tvShow;

        List<VodImage> images = TVShowDetailsUtils.getImages(tvShow);
        if (images.size() > 0) {
            GalleryAdapter adapter = new GalleryAdapter(this, GalleryType.CARD);
            adapter.setItems(images);
            adapter.setListener(position -> {
                GalleryActivity.open(getApplicationContext(), tvShow.getId(), 1, position);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
            binding.gallery.viewPager.setAdapter(adapter);
            binding.gallery.viewPagerIndicator.setViewPager(binding.gallery.viewPager);
        }

        if (CommonUtils.notNullOrEmpty(tvShow.getTitle())) {
            binding.details.title.setText(tvShow.getTitle());
        } else {
            binding.details.title.setVisibility(View.GONE);
        }

        if (CommonUtils.notNullOrEmpty(tvShow.getYear())) {
            binding.details.info.setText(tvShow.getYear());
        }

        if (tvShow.getParentalRating() != null && CommonUtils.notNullOrEmpty(tvShow.getParentalRating().getCode())) {
            binding.details.info.setText(checkForDivider(tvShow.getParentalRating().getCode()));
        }

        if (CommonUtils.notNullOrEmpty(tvShow.getSeasonCount())) {
            binding.details.info.setText(checkForDivider(String.format(getString(R.string.seasons_count), tvShow.getSeasonCount())));
        }

        if (tvShow.getRate() != null && CommonUtils.notNullOrEmpty(tvShow.getRate().getNumberOfRates())) {
            binding.details.info.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_star_filled, 0);
            binding.details.info.setText(checkForDivider(tvShow.getRate().getNumberOfRates() + "/5"));
        }

        if (CommonUtils.notNullOrEmpty(tvShow.getRating())) {
            binding.details.imdb.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_imdb, 0);
            binding.details.imdb.setText(String.format(getString(R.string.imdb_rating), tvShow.getRating()));
        } else {
            binding.details.imdb.setVisibility(View.GONE);
        }

        if (tvShow.getGenres() != null && tvShow.getGenres().size() > 0) {
            for (TVShowGenre tvShowGenre : tvShow.getGenres()) {
                TVShowDetailsGenreView view = new TVShowDetailsGenreView(this, tvShowGenre);
                binding.details.genres.addView(view);
            }
        } else {
            binding.details.genres.setVisibility(View.GONE);
        }

        if (tvShow.getShowTypes() != null && tvShow.getShowTypes().size() > 0) {
            for (int i = 0; i < tvShow.getShowTypes().size(); i++) {
                ShowType showType = tvShow.getShowTypes().get(i);
                TVShowDetailsShowTypeView view = new TVShowDetailsShowTypeView(this, showType);
                view.hasDivider(i < tvShow.getShowTypes().size() - 1);
                binding.details.showTypes.addView(view);
            }
        } else {
            binding.details.showTypes.setVisibility(View.GONE);
        }

        if (CommonUtils.notNullOrEmpty(tvShow.getDescription())) {
            binding.details.description.setText(tvShow.getDescription());
        } else {
            binding.details.description.setVisibility(View.GONE);
        }

        if (tvShow.getActors() != null && tvShow.getActors().size() > 0) {
            binding.details.cast.setText(TVShowDetailsUtils.getMembers(this, tvShow.getActors(), R.string.cast));
        } else {
            binding.details.cast.setVisibility(View.GONE);
        }

        if (tvShow.getWriters() != null && tvShow.getWriters().size() > 0) {
            binding.details.director.setText(TVShowDetailsUtils.getMembers(this, tvShow.getWriters(), R.string.director));
        } else {
            binding.details.director.setVisibility(View.GONE);
        }

        if (tvShow.getSeasons() != null && tvShow.getSeasons().size() > 0) {
            for (TVShowSeason tvShowSeason : tvShow.getSeasons()) {
                TVShowEpisodeListRow view = new TVShowEpisodeListRow(this, tvShow.getId(), tvShowSeason);
                view.setPadding(0, 0, 0, ViewUtils.dpToPx(16));
                binding.seasons.addView(view);
            }
        }
    }

    private String checkForDivider(String string) {
        return binding.details.info.getText().length() > 0
                ? binding.details.info.getText() + " | " + string
                : string;
    }
}
