package com.wecast.mobile.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.annotation.Nullable;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import dagger.android.support.AndroidSupportInjection;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;

/**
 * Created by ageech@live.com
 */

public abstract class BaseFragment<T extends ViewDataBinding, V extends BaseViewModel> extends Fragment {

    private CompositeDisposable subscriptions = new CompositeDisposable();
    private BaseActivity activity;
    private View rootView;
    private T viewDataBinding;
    private V viewModel;

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract V getViewModel();

    @Nullable
    public BaseActivity getBaseActivity() {
        return activity;
    }

    public T getViewDataBinding() {
        return viewDataBinding;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = getViewModel();
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        viewDataBinding.setVariable(getBindingVariable(), viewModel);
        rootView = viewDataBinding.getRoot();
        return rootView;
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

    /**
     * COMMON METHODS
     */

    public void subscribe(Disposable disposable) {
        subscriptions.add(disposable);
    }

    public boolean isNetworkConnected() {
        return activity != null && activity.isNetworkConnected();
    }

    public void hideKeyboard() {
        if (activity != null) {
            activity.hideKeyboard();
        }
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
