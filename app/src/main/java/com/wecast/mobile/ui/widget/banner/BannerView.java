package com.wecast.mobile.ui.widget.banner;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.wecast.core.analytics.SocketManager;
import com.wecast.core.data.api.ApiStatus;
import com.wecast.core.data.db.entities.Banner;
import com.wecast.core.data.db.entities.BannerAdsFile;
import com.wecast.core.data.repository.BannerRepository;
import com.wecast.mobile.Constants;
import com.wecast.mobile.R;
import com.wecast.mobile.WeApp;
import com.wecast.mobile.databinding.WidgetBannerBinding;
import com.wecast.mobile.ui.widget.listRow.BaseListRow;
import com.wecast.mobile.utils.GlideApp;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ageech@live.com
 */

public class BannerView extends BaseListRow {

    @Inject
    BannerRepository bannerRepository;
    @Inject
    SocketManager socketManager;

    private Context context;
    private WidgetBannerBinding binding;
    private Banner banner;
    private String boxPosition;

    public BannerView(Context context) {
        super(context);
        initialize(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        this.context = context;

        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = WidgetBannerBinding.inflate(inflater, this, true);
        setVisibility(GONE);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
            boxPosition = a.getString(R.styleable.BannerView_box_position);
            a.recycle();
        }
    }

    private void fetchData() {
        Disposable disposable = bannerRepository.getBanner(boxPosition)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.status == ApiStatus.SUCCESS) {
                            setBanner(response.data);
                        } else if (response.status == ApiStatus.ERROR) {
                            removeView();
                        } else if (response.status == ApiStatus.TOKEN_EXPIRED) {
                            refreshToken(this::fetchData);
                        } else if (response.status == ApiStatus.SUBSCRIPTION_EXPIRED) {
                            setBanner(response.data);
                            //snackBar(R.string.error_subscription_expired);
                        }
                    }
                }, throwable -> {
                    toast(throwable);
                    removeView();
                });
        subscribe(disposable);
    }

    public void setBanner(Banner banner) {
        this.banner = banner;

        if (this.banner == null) {
            removeView();
            return;
        }

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        // IF there is target url allow user to click on banner
        String target = banner.getTargetUrl();
        setClickable(target != null && !target.isEmpty());
        setFocusable(target != null && !target.isEmpty());

        // Show banner info according to type
        BannerType bannerType = getBannerType();
        if (bannerType != null) {
            switch (bannerType) {
                case BANNER_TEXT:
                    setupText();
                    break;
                case BANNER_IMAGE:
                    setupImage();
                    break;
                case BANNER_HTML:
                    setupHTML();
                    break;
                case BANNER_GOOGLE_ADS:
                    setupGoogleAdvertisement();
                    break;
            }
        }
    }

    private void setupListeners() {
        setOnClickListener(view -> {
            socketManager.sendBannerData(banner, true, false);
            // Open target url in WebView
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(banner.getTargetUrl()));
            context.startActivity(i);
        });

        binding.close.setOnClickListener(v -> {
            socketManager.sendBannerData(banner, false, true);
            hide();
        });
    }

    private void setupImage() {
        BannerAdsFile file = banner.getAdsFile();

        if (file == null || file.getOriginal() == null) {
            removeView();
            return;
        }

        GlideApp.with(context)
                .load(file.getOriginal())
                .into(binding.image);

        binding.image.setVisibility(VISIBLE);
        // Add delay for 1 second
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::show, 1000);
    }

    private void setupText() {
        String text = banner.getStringValue();

        if (text == null || text.isEmpty()) {
            removeView();
            return;
        }

        binding.text.setText(banner.getStringValue());
        binding.text.setVisibility(VISIBLE);
        show();
    }

    private void setupHTML() {
        String html = banner.getStringValue();

        if (html == null || html.isEmpty()) {
            removeView();
            return;
        }

        binding.web.getSettings().setLoadWithOverviewMode(true);
        binding.web.getSettings().setUseWideViewPort(true);
        binding.web.setWebViewClient(new WebViewClient());
        binding.web.loadData(html, "text/html", "UTF-8");
        binding.web.setVisibility(VISIBLE);
        // Add delay for 1 second
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::show, 1000);
    }

    private void setupGoogleAdvertisement() {
        AdView banner = new AdView(context);
        banner.setAdSize(AdSize.BANNER);
        banner.setAdUnitId(Constants.ADMOB_APP_ID);
        AdRequest.Builder adRequest = new AdRequest.Builder();
        adRequest.addTestDevice("306BD0907F83DF2A140FF109EC784426");
        binding.adView.addView(banner);
        banner.loadAd(adRequest.build());
        binding.adView.setVisibility(VISIBLE);
        show();
    }

    private void show() {
        setVisibility(VISIBLE);
        /*boolean isRtl = getResources().getBoolean(R.bool.is_right_to_left);
        Animation animation = AnimationUtils.loadAnimation(context, isRtl
                ? R.anim.item_animation_from_right_rtl
                : R.anim.item_animation_from_right);
        animation.setAnimationListener(new BannerAnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //setVisibility(VISIBLE);
            }
        });
        requestLayout();
        startAnimation(animation);*/
    }

    private void hide() {
        boolean isRtl = getResources().getBoolean(R.bool.is_right_to_left);
        Animation animation = AnimationUtils.loadAnimation(context, isRtl
                ? R.anim.item_animation_to_right_rtl
                : R.anim.item_animation_to_right);
        animation.setAnimationListener(new BannerAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                removeView();
            }
        });
        requestLayout();
        startAnimation(animation);
    }

    private BannerType getBannerType() {
        switch (banner.getAdsType()) {
            case "image":
                return BannerType.BANNER_IMAGE;
            case "text":
                return BannerType.BANNER_TEXT;
            case "embed_html":
                return BannerType.BANNER_HTML;
            case "google_ads":
                return BannerType.BANNER_GOOGLE_ADS;
            default:
                return null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        WeApp.getInstance().getAppComponent().inject(this);

        if (getAppModules().hasAdvertisements()) {
            fetchData();
        } else {
            removeView();
        }
    }
}