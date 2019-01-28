package com.wecast.mobile.ui.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.widget.FrameLayout;

import com.wecast.mobile.R;
import com.wecast.mobile.ui.screen.home.HomeFragment;
import com.wecast.mobile.ui.screen.live.LiveTVFragment;
import com.wecast.mobile.ui.screen.live.channel.ChannelFragment;
import com.wecast.mobile.ui.screen.live.guide.TVGuideFragment;
import com.wecast.mobile.ui.screen.show.TVShowFragment;
import com.wecast.mobile.ui.screen.trending.TrendingFragment;
import com.wecast.mobile.ui.screen.vod.VodFragment;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by ageech@live.com
 */

public class FragmentStateManager {

    private FragmentManager fragmentManager;
    private FrameLayout container;
    private HashMap<Integer, Fragment> fragmentHashMap;
    private Fragment currentFragment;

    public FragmentStateManager(FragmentManager fragmentManager, FrameLayout container) {
        this.fragmentManager = fragmentManager;
        this.container = container;
        this.fragmentHashMap = new LinkedHashMap<>();
    }

    public void goTo(int titleRes) {
        Fragment fragment = fragmentHashMap.get(titleRes);
        if (fragment == null) {
            fragment = getFragmentFromTitle(titleRes);
            fragmentHashMap.put(titleRes, fragment);
            addFragment(fragment);
        } else {
            showFragment(fragment);
        }
        currentFragment = fragment;
    }

    private void addFragment(Fragment fragment) {
        if (currentFragment == null) {
            fragmentManager.beginTransaction()
                    .add(container.getId(), fragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .hide(currentFragment)
                    .add(container.getId(), fragment)
                    .commit();
        }
    }

    private void showFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .hide(currentFragment)
                .show(fragment)
                .commit();
    }

    private Fragment getFragmentFromTitle(int titleRes) {
        switch (titleRes) {
            case R.string.home:
                return HomeFragment.newInstance();
            case R.string.trending:
                return TrendingFragment.newInstance();
            case R.string.live_tv:
                return LiveTVFragment.newInstance();
            case R.string.watch_live:
                return ChannelFragment.newInstance();
            case R.string.tv_guide:
                return TVGuideFragment.newInstance();
            case R.string.movies:
                return VodFragment.newInstance();
            case R.string.tv_shows:
                return TVShowFragment.newInstance();
        }
        return null;
    }
}
