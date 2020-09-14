package com.wecast.mobile.ui.screen.navigation;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.wecast.core.data.repository.ComposerRepository;
import com.wecast.core.service.SyncRemindersService;
import com.wecast.mobile.BR;
import com.wecast.mobile.Constants;
import com.wecast.mobile.R;
import com.wecast.mobile.databinding.ActivityNavigationBinding;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;
import com.wecast.mobile.ui.utils.FragmentStateManager;
import com.wecast.mobile.utils.BindingUtils;
import com.wecast.mobile.utils.PermissionUtils;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

/**
 * Created by ageech@live.com
 */

public class NavigationActivity extends BaseActivity<ActivityNavigationBinding, NavigationActivityViewModel> implements HasSupportFragmentInjector, NavigationActivityNavigator {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject
    NavigationActivityViewModel viewModel;
    @Inject
    ComposerRepository composerRepository;

    private ActivityNavigationBinding binding;
    private FragmentStateManager fragmentStateManager;

    public static void open(Context context) {
        Intent intent = new Intent(context, NavigationActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_navigation;
    }

    @Override
    public NavigationActivityViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public boolean shouldSetTheme() {
        return true;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUI();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void checkPermissions(){
        // Check for READ/WRITE calendar permission
        boolean isReadGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
        boolean isWriteGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
        if (isReadGranted && isWriteGranted) {
            startRemindersSyncService();
        }
    }



    private void setupUI() {
        setStatusTransparent(this);
        setThemeMode(this);
        binding = getViewDataBinding();
        viewModel.setNavigator(this);

        // Setup custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        // Set app logo
        BindingUtils.bindAppLogo(binding.toolbar.logo, composerRepository.getAppLogo());

        // Remove ripple effect from tab
        binding.tabLayout.setTabRippleColor(null);

        // Setup tab layout according app composer modules
        if (composerRepository.getAppModules().hasHome()) {
            binding.tabLayout.addTab(buildTab(R.string.home));
        }
        if (composerRepository.getAppModules().hasTrending()) {
            binding.tabLayout.addTab(buildTab(R.string.trending));
        }
        if (composerRepository.getAppModules().hasChannels()) {
            binding.tabLayout.addTab(buildTab(R.string.live_tv));
        }
        if (composerRepository.getAppModules().hasVod()) {
            binding.tabLayout.addTab(buildTab(R.string.movies));
            binding.tabLayout.addTab(buildTab(R.string.tv_shows));
        }

        // Setup fragment state manager
        fragmentStateManager = new FragmentStateManager(getSupportFragmentManager(), binding.container);
        TabLayout.Tab tab = binding.tabLayout.getTabAt(0);
        if (tab != null) {
            String title = (String) tab.getText();
            if (title != null) {
                selectTab(title, 0);
            }
        }
    }


    private TabLayout.Tab buildTab(int title) {
        String tabTitle = getString(title);
        TabLayout.Tab tab = binding.tabLayout.newTab();
        tab.setText(tabTitle);
        return tab;
    }

    private void setupListeners() {
        binding.tabLayout.addOnTabSelectedListener(new NavigationSelectListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String title = (String) tab.getText();
                if (title != null) {
                    selectTab(title, tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabTypeface(false, tab.getPosition());
            }
        });
    }

    private void selectTab(String title, int position) {
        if (title.equals(getString(R.string.home))) {
            fragmentStateManager.goTo(R.string.home);
        } else if (title.equals(getString(R.string.trending))) {
            fragmentStateManager.goTo(R.string.trending);
        } else if (title.equals(getString(R.string.live_tv))) {
            fragmentStateManager.goTo(R.string.live_tv);
        } else if (title.equals(getString(R.string.movies))) {
            fragmentStateManager.goTo(R.string.movies);
        } else if (title.equals(getString(R.string.tv_shows))) {
            fragmentStateManager.goTo(R.string.tv_shows);
        }
        updateTabTypeface(true, position);
    }

    private void updateTabTypeface(boolean select, int position) {
        LinearLayout tabLayout = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(position);
        TextView tabTextView = (TextView) tabLayout.getChildAt(1);
        Typeface bold = TypefaceUtils.load(getAssets(), "fonts/helvetica_bold.ttf");
        Typeface regular = TypefaceUtils.load(getAssets(), "fonts/helvetica_regular.ttf");
        tabTextView.setTypeface(select ? bold : regular);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings:
                ScreenRouter.openSettings(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startRemindersSyncService() {
        JobInfo.Builder jobBuilder = new JobInfo.Builder(1, new ComponentName(this, SyncRemindersService.class));
        jobBuilder.setMinimumLatency(Constants.REMINDERS_SYNC_TIMEOUT);
        jobBuilder.setOverrideDeadline((long) (Constants.REMINDERS_SYNC_TIMEOUT * 1.05));
        jobBuilder.setRequiresDeviceIdle(false);
        jobBuilder.build();

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (scheduler != null) {
            scheduler.schedule(jobBuilder.build());
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.tabLayout.getSelectedTabPosition() == 0) {
            ScreenRouter.openExit(this);
        } else {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(0);
            if (tab != null) {
                tab.select();
            } else {
                super.onBackPressed();
            }
        }
    }
}
