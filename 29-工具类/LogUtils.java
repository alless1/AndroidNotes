package com.gsl.speed.utils;

import android.util.Log;

/**
 * Created by chengjie on 2018/8/6
 * Description:
 */
public class LogUtils {
    private static boolean isLogOn = true;

    public static void d(Object object, String message) {
        if (isLogOn)
            Log.i(getTag(object), message);
    }

    public static void d(String Tag, String message) {
        if (isLogOn)
            Log.i(Tag, message);
    }

    public static void e(Object object, String message) {
        if (isLogOn)
            Log.e(getTag(object), message);
    }

    public static void e(String Tag, String message) {
        if (isLogOn)
            Log.e(Tag, message);
    }



    public static String getTag(Object object) {
        //return object.getClass().getName();
        return object.getClass().getSimpleName();
    }


}
