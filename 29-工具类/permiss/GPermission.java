package com.example.remotedemo.utils.permiss;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Created by chengjie on 2019/6/13
 * Description:
 *         GPermission.with(this)
 *                 .permission(PERMISSIONS)
 *                 .callback(new PermissionActivity.PermissionCallback() {
 *                     @Override
 *                     public void onPermissionGranted() {
 *                         LogUtils.log("onPermissionGranted");
 *                     }
 *
 *                     @Override
 *                     public void shouldShowRational(String permission) {
 *                         LogUtils.log("shouldShowRational");
 *                     }
 *
 *                     @Override
 *                     public void onPermissionReject(String permission) {
 *                         LogUtils.log("onPermissionReject");
 *                     }
 *                 }).request();
 */
public class GPermission {
    // 权限申请回调
    private PermissionActivity.PermissionCallback callback;
    // 需要申请的权限
    private String[] permissions;
    private Context context;

    public GPermission(Context context) {
        this.context = context;
    }

    public static GPermission with(Context context) {
        GPermission permisson = new GPermission(context);
        return permisson;
    }

    public GPermission permission(String[] permissons) {
        this.permissions = permissons;
        return this;
    }

    public GPermission callback(PermissionActivity.PermissionCallback callback) {
        this.callback = callback;
        return this;
    }

    public void request() {
        if (permissions == null || permissions.length <= 0) {
            return;
        }

        // 当api大于23时，才进行权限申请
        if (Build.VERSION.SDK_INT < 23)
            return;

        boolean isGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
                break;
            }
        }
        if (isGranted) {
            callback.onPermissionGranted();
        } else {
            PermissionActivity.request(context, permissions, callback);
        }
    }
}