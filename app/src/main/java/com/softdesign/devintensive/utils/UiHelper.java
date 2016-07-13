package com.softdesign.devintensive.utils;

import android.content.Context;
import android.util.TypedValue;

public class UiHelper {
    private static Context mContext = DevIntensiveApplication.getContext();

    public static int getStatusBarHeight() {
        int result = 0;
        int resourseId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "andriod");
        if (resourseId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourseId);
        }
        return result;
    }

    public static int getActionBarHeight() {
        int result = 0;
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            result = TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().getDisplayMetrics());
        }
        return result;
    }

    public static int lepr(int start, int end, float friction) {
        return (int) (start + (end - start) * friction);
    }

    public static float currentFriction(int start, int end, int currentValue) {
        return (float) (currentValue - start) / (end - start);
    }
}
