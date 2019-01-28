package com.wecast.mobile.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ageech@live.com
 */

public final class PermissionUtils {

    private Activity activity;
    private PermissionListener permissionListener;
    private int requestCode;

    public void request(Activity activity, @NonNull String[] permissions, int requestCode, PermissionListener permissionListener) {
        this.activity = activity;
        this.requestCode = requestCode;
        this.permissionListener = permissionListener;

        if (!needRequestRuntimePermissions()) {
            permissionListener.onAllowed();
            return;
        }

        requestUnGrantedPermissions(permissions, requestCode);
    }

    private boolean needRequestRuntimePermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void requestUnGrantedPermissions(String[] permissions, int requestCode) {
        String[] unGrantedPermissions = findUnGrantedPermissions(permissions);
        if (unGrantedPermissions.length == 0) {
            permissionListener.onAllowed();
            return;
        }
        ActivityCompat.requestPermissions(activity, unGrantedPermissions, requestCode);
    }

    private boolean isPermissionGranted(String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private String[] findUnGrantedPermissions(String[] permissions) {
        List<String> unGrantedPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (!isPermissionGranted(permission)) {
                unGrantedPermissionList.add(permission);
            }
        }
        return unGrantedPermissionList.toArray(new String[0]);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == this.requestCode) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        permissionListener.onDeclined();
                        return;
                    }
                }
                permissionListener.onAllowed();
            } else {
                permissionListener.onDeclined();
            }
        }
    }

    public interface PermissionListener {

        void onAllowed();

        void onDeclined();
    }
}
