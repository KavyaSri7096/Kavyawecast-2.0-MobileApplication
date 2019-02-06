package com.wecast.mobile.ui.widget.listRow;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.stream.MalformedJsonException;
import com.wecast.core.analytics.SocketManager;
import com.wecast.core.data.api.manager.AccountManager;
import com.wecast.core.data.db.entities.composer.Modules;
import com.wecast.core.data.db.pref.PreferenceManager;
import com.wecast.core.data.repository.ComposerRepository;
import com.wecast.mobile.R;
import com.wecast.mobile.ui.ScreenRouter;
import com.wecast.mobile.ui.base.BaseActivity;

import java.io.IOException;
import java.net.SocketTimeoutException;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Created by ageech@live.com
 */

public abstract class BaseListRow extends FrameLayout {

    @Inject
    AccountManager accountManager;
    @Inject
    PreferenceManager preferenceManager;
    @Inject
    SocketManager socketManager;
    @Inject
    ComposerRepository composerRepository;

    private CompositeDisposable subscriptions = new CompositeDisposable();

    public BaseListRow(@NonNull Context context) {
        super(context);
    }

    public BaseListRow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseListRow(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Add all subscriptions to CompositeDisposable variable
     * so we can dispose them in onDetachedFromWindow()
     */
    protected void subscribe(Disposable disposable) {
        // Dispose all subscriptions
        subscriptions.add(disposable);
    }

    /**
     * If API returns 401 Unauthorized, login user with UID
     * and in doOnComplete repeat previous action
     */
    protected void refreshToken(Action action) {
        Disposable disposable = accountManager.loginWithUID()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(action)
                .subscribe(response -> {
                    if (response != null) {
                        String token = response.getData().getDevice().getToken();
                        preferenceManager.setAccessToken(token);
                        socketManager.reconnect();
                    } else {
                        openActivityOnTokenExpire();
                    }
                }, throwable -> openActivityOnTokenExpire());
        subscribe(disposable);
    }

    /**
     * If check token fails (or login with UID)
     * close current screen, and go to login screen
     */
    public void openActivityOnTokenExpire() {
        ScreenRouter.openLogin(getContext());
        ((AppCompatActivity) getContext()).finish();
    }

    /**
     * For each row before loading data check if
     * row is enabled in composer
     */
    public Modules getAppModules() {
        return composerRepository.getAppModules();
    }

    /**
     * API throwable message handler
     */
    public void toast(Throwable throwable) {
        throwable.printStackTrace();

        if (throwable instanceof SocketTimeoutException) {
            toast(R.string.error_timeout);
        } else if (throwable instanceof MalformedJsonException) {
            toast(R.string.error_malformed_json);
        } else if (throwable instanceof IOException) {
            toast(R.string.error_no_internet_connection);
        } else if (throwable instanceof HttpException) {
            toast(((HttpException) throwable).response().message());
        } else {
            toast(R.string.error_unknown);
        }
    }

    public void toast(int message) {
        if (message > -1) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public void toast(String message) {
        if (message != null && !message.isEmpty()) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * If there is no items,
     * remove current widget from container
     */
    public void removeView() {
        Handler handler = new Handler(Looper.myLooper());
        if (getParent() instanceof ListRowContainer) {
            ListRowContainer container = (ListRowContainer) getParent();
            handler.post(() -> container.removeView(BaseListRow.this));
        } else {
            ViewGroup container = (ViewGroup) getParent();
            handler.post(() -> container.removeView(BaseListRow.this));
        }
    }

    /**
     * Show snack bar message if user subscription is not valid
     */
    protected void snackBar(int message) {
        if (getContext() instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) getContext();
            activity.snackBar(message);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        subscriptions.dispose();
        super.onDetachedFromWindow();
    }
}
