package com.wecast.mobile.ui.base;

import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableBoolean;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by ageech@live.com
 */

public abstract class BaseViewModel<Navigator> extends ViewModel {

    private final ObservableBoolean isLoading = new ObservableBoolean(true);
    private final ObservableBoolean isEmpty = new ObservableBoolean(false);

    private CompositeDisposable compositeDisposable;
    private WeakReference<Navigator> navigator;

    public BaseViewModel() {
        this.compositeDisposable = new CompositeDisposable();
    }

    /**
     * LOADING
     */

    public ObservableBoolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.set(loading);
    }

    /**
     * EMPTY VIEW
     */

    public ObservableBoolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty.set(empty);
    }

    /**
     * NAVIGATOR
     */

    public Navigator getNavigator() {
        return navigator.get();
    }

    public void setNavigator(Navigator navigator) {
        this.navigator = new WeakReference<>(navigator);
    }

    /**
     * SUBSCRIPTIONS
     */

    public void subscribe(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }
}
