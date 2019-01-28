package com.wecast.mobile.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.wecast.mobile.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ageech@live.com
 */

public final class CommonUtils {

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static String getDeviceType(Context context) {
        return context.getResources().getBoolean(R.bool.isPhone) ? "android-mobile" : "android-tablet";
    }

    public static boolean notNullOrEmpty(Object object) {
        return object != null && !TextUtils.isEmpty(String.valueOf(object));
    }

    public static String firstCapital(String str) {
        if (str != null && str.length() > 0) {
            return Character.toUpperCase(str.charAt(0)) + str.substring(1) + " ";
        } else {
            return str;
        }
    }

    public static String getRuntime(String min) {
        if (min == null || min.equals("")) {
            return "";
        }

        String removeChars = min.replaceAll("[^0-9]", "");
        if (Integer.parseInt(removeChars) > 59) {
            //Remove unexpected characters from string
            int totalMinutes = Integer.parseInt(removeChars);
            //Convert minutes to 00h 00min
            String minutes = Integer.toString(totalMinutes % 60);
            minutes = minutes.length() == 1 ? "0" + minutes : minutes;
            return (totalMinutes / 60) + "h " + minutes + "min";
        } else {
            return min + "min";
        }
    }

    public static int calculateColumns(Context context, int dimen) {
        int card_width = (int) context.getResources().getDimension(dimen);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) ((metrics.widthPixels / metrics.density) / (card_width / metrics.density));
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }
}
