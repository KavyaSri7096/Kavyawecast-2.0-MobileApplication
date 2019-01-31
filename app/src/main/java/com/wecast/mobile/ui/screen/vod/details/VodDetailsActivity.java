package com.wecast.mobile.ui.screen.vod.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wecast.core.data.db.entities.ShowType;
import com.wecast.core.data.db.entities.Vod;
import com.wecast.core.data.db.entities.VodGenre;
import com.wecast.core.data.db.entities.VodImage;
import com.wecast.core.data.db.entities.VodSourceProfile;
import com.wecast.mobile.BR;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityVodDetailsBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.screen.gallery.GalleryActivity;
import com.wecast.mobile.ui.screen.gallery.GalleryAdapter;
import com.wecast.mobile.ui.screen.gallery.GalleryType;
import com.wecast.mobile.ui.screen.vod.player.VodPlayerActivity;
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

public class VodDetailsActivity extends BaseActivity<ActivityVodDetailsBinding, VodDetailsActivityViewModel> implements VodDetailsActivityNavigator {

    @Inject
    VodDetailsActivityViewModel viewModel;

    private ActivityVodDetailsBinding binding;
    private Vod vod;

    public static void open(Context context, Vod vod) {
        Intent intent = new Intent(context, VodDetailsActivity.class);
        intent.putExtra("ID", vod.getId());
        intent.putExtra("IS_EPISODE", vod.getMultiEventVodId() != null);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_vod_details;
    }

    @Override
    public VodDetailsActivityViewModel getViewModel() {
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
            boolean isEpisode = bundle.getBoolean("IS_EPISODE");
            getByID(id, isEpisode);
        }
    }

    private void setupListeners() {
        binding.gallery.back.setOnClickListener(v -> onBackPressed());
        binding.actions.rent.setOnClickListener(v -> {
            if (vod != null) {
                ScreenRouter.openVodRentDialog(VodDetailsActivity.this, vod);
            }
        });
        binding.actions.play.setOnClickListener(v -> {
            if (vod != null) {
                checkForSingleProfile();
            }
        });
        binding.actions.rate.setOnClickListener(v -> {
            if (vod != null) {
                ScreenRouter.openVodRateDialog(VodDetailsActivity.this, vod);
            }
        });
        binding.actions.trailer.setOnClickListener(v -> {
            if (vod != null) {
                ScreenRouter.openVodPlayer(VodDetailsActivity.this, vod, null, VodPlayerActivity.PLAY_TRAILER);
            }
        });
    }

    private void getByID(int id, boolean isEpisode) {
        Disposable disposable = viewModel.getByID(id, isEpisode)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        showData(response);
                    }
                }, this::toast);
        subscribe(disposable);
    }

    private void showData(Vod vod) {
        this.vod = vod;

        List<VodImage> images = VodDetailsUtils.getImages(vod);
        if (images.size() > 0) {
            GalleryAdapter adapter = new GalleryAdapter(this, GalleryType.CARD);
            adapter.setItems(images);
            adapter.setListener(position -> {
                GalleryActivity.open(getApplicationContext(), vod.getId(), 0, position);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
            binding.gallery.viewPager.setAdapter(adapter);
            binding.gallery.viewPagerIndicator.setViewPager(binding.gallery.viewPager);
        }

        if (CommonUtils.notNullOrEmpty(vod.getTitle())) {
            binding.details.title.setText(vod.getTitle());
            binding.actions.title.setText(vod.getTitle());
        } else {
            binding.details.title.setVisibility(View.GONE);
        }

        if (CommonUtils.notNullOrEmpty(vod.getYear())) {
            binding.details.info.setText(vod.getYear());
        }

        if (vod.getParentalRating() != null && CommonUtils.notNullOrEmpty(vod.getParentalRating().getCode())) {
            binding.details.info.setText(checkForDivider(vod.getParentalRating().getCode()));
        }

        if (CommonUtils.notNullOrEmpty(vod.getRuntime())) {
            binding.details.info.setText(checkForDivider(CommonUtils.getRuntime(vod.getRuntime())));
        }

        if (vod.getRate() != null && CommonUtils.notNullOrEmpty(vod.getRate().getNumberOfRates())) {
            binding.details.info.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_star_filled, 0);
            binding.details.info.setText(checkForDivider(vod.getRate().getNumberOfRates() + "/5"));
        }

        if (CommonUtils.notNullOrEmpty(vod.getRating())) {
            binding.details.imdb.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_imdb, 0);
            binding.details.imdb.setText(String.format(getString(R.string.imdb_rating), vod.getRating()));
        } else {
            binding.details.imdb.setVisibility(View.GONE);
        }

        if (vod.getGenres() != null && vod.getGenres().size() > 0) {
            for (VodGenre vodGenre : vod.getGenres()) {
                VodDetailsGenreView view = new VodDetailsGenreView(this, vodGenre);
                binding.details.genres.addView(view);
            }
        } else {
            binding.details.genres.setVisibility(View.GONE);
        }

        if (vod.getShowTypes() != null && vod.getShowTypes().size() > 0) {
            for (int i = 0; i < vod.getShowTypes().size(); i++) {
                ShowType showType = vod.getShowTypes().get(i);
                VodDetailsShowTypeView view = new VodDetailsShowTypeView(this, showType);
                view.hasDivider(i < vod.getShowTypes().size() - 1);
                binding.details.showTypes.addView(view);
            }
        } else {
            binding.details.showTypes.setVisibility(View.GONE);
        }

        if (CommonUtils.notNullOrEmpty(vod.getDescription())) {
            binding.details.description.setText(vod.getDescription());
        } else {
            binding.details.description.setVisibility(View.GONE);
        }

        if (vod.getActors() != null && vod.getActors().size() > 0) {
            binding.details.cast.setText(VodDetailsUtils.getMembers(this, vod.getActors(), R.string.cast));
        } else {
            binding.details.cast.setVisibility(View.GONE);
        }

        if (vod.getWriters() != null && vod.getWriters().size() > 0) {
            binding.details.director.setText(VodDetailsUtils.getMembers(this, vod.getWriters(), R.string.director));
        } else {
            binding.details.director.setVisibility(View.GONE);
        }

        boolean notPlayable = VodDetailsUtils.getSourceProfiles(vod, true).isEmpty();
        binding.actions.playRoot.setVisibility(notPlayable ? View.GONE : View.VISIBLE);

        boolean notRentable = VodDetailsUtils.getSourceProfiles(vod, false).isEmpty();
        binding.actions.rentRoot.setVisibility(notRentable ? View.GONE : View.VISIBLE);
    }

    private String checkForDivider(String string) {
        return binding.details.info.getText().length() > 0
                ? binding.details.info.getText() + " | " + string
                : string;
    }

    /**
     * If vod has only one profile for movie source
     * play that profile without opening play dialog
     */
    private void checkForSingleProfile() {
        List<VodSourceProfile> profiles = VodDetailsUtils.getSourceProfiles(vod, true);
        if (profiles != null) {
            if (profiles.size() == 1) {
                VodSourceProfile vodSourceProfile = profiles.get(0);
                if (vodSourceProfile.isSubscribed()) {
                    ScreenRouter.openVodPlayer(this, vod, vodSourceProfile, VodPlayerActivity.PLAY_MOVIE);
                }
            } else {
                ScreenRouter.openVodPlayDialog(this, vod);
            }
        }
    }
}
