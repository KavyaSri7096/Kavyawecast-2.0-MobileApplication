package com.wecast.mobile.ui.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.ViewGroup;

import com.wecast.mobile.R;

import javax.annotation.Nullable;

import dagger.android.support.AndroidSupportInjection;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

/**
 * Created by ageech@live.com
 */

public abstract class BaseDialog extends DialogFragment {

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private BaseActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BaseDialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
        if (context instanceof BaseActivity) {
            activity = (BaseActivity) context;
        }
    }

    @Override
    public void onDetach() {
        activity = null;
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        subscriptions.dispose();
        super.onDestroy();
    }

    public void show(FragmentManager fragmentManager, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment prevFragment = fragmentManager.findFragmentByTag(tag);
        if (prevFragment != null) {
            transaction.remove(prevFragment);
        }
        transaction.addToBackStack(null);
        show(transaction, tag);
    }

    public void show(FragmentManager fragmentManager) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment prevFragment = fragmentManager.findFragmentByTag(getTag());
        if (prevFragment != null) {
            transaction.remove(prevFragment);
        }
        transaction.addToBackStack(null);
        show(transaction, getTag());
    }

    /**
     * COMMON METHODS
     */

    public BaseActivity getBaseActivity() {
        return activity;
    }

    public void subscribe(Disposable disposable) {
        subscriptions.add(disposable);
    }

    public void hideKeyboard() {
        if (activity != null) {
            activity.hideKeyboard();
        }
    }

    public void hideLoading() {
        if (activity != null) {
            activity.hideLoading();
        }
    }

    public boolean isNetworkConnected() {
        return activity != null && activity.isNetworkConnected();
    }

    public void refreshToken(Action action) {
        if (activity != null) {
            activity.refreshToken(action);
        }
    }

    public void openActivityOnTokenExpire() {
        if (activity != null) {
            activity.openActivityOnTokenExpire();
        }
    }

    public void showLoading() {
        if (activity != null) {
            activity.showLoading();
        }
    }

    public void toast(String message) {
        if (activity != null) {
            activity.toast(message);
        }
    }

    public void toast(int message) {
        if (activity != null) {
            activity.toast(message);
        }
    }

    public void toast(Throwable throwable) {
        if (activity != null) {
            activity.toast(throwable);
        }
    }

    public void snackBar(String message) {
        if (activity != null) {
            activity.snackBar(message);
        }
    }

    public void snackBar(int message) {
        if (activity != null) {
            activity.snackBar(message);
        }
    }
}
