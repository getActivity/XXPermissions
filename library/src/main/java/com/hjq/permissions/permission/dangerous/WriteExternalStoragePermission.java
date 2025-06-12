package com.hjq.permissions.permission.dangerous;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.common.DangerousPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 写入外部存储权限类
 */
public final class WriteExternalStoragePermission extends DangerousPermission {

    public static final Parcelable.Creator<WriteExternalStoragePermission> CREATOR = new Parcelable.Creator<WriteExternalStoragePermission>() {

        @Override
        public WriteExternalStoragePermission createFromParcel(Parcel source) {
            return new WriteExternalStoragePermission(source);
        }

        @Override
        public WriteExternalStoragePermission[] newArray(int size) {
            return new WriteExternalStoragePermission[size];
        }
    };

    public WriteExternalStoragePermission() {
        // default implementation ignored
    }

    private WriteExternalStoragePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.WRITE_EXTERNAL_STORAGE;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_6;
    }

    @SuppressLint("NewApi")
    @Override
    protected boolean isGrantedByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_11)) {
            // 这里补充一下这样写的具体原因：
            // 1. 当 targetSdk >= Android 11 并且在此版本及之上申请 WRITE_EXTERNAL_STORAGE，虽然可以弹出授权框，但是没有什么实际作用
            //    相关文档地址：https://developer.android.google.cn/reference/android/Manifest.permission#WRITE_EXTERNAL_STORAGE
            //                https://developer.android.google.cn/about/versions/11/privacy/storage?hl=zh-cn#permissions-target-11
            //    开发者可能会在清单文件注册 android:maxSdkVersion="29" 属性，这样会导致 WRITE_EXTERNAL_STORAGE 权限申请失败，这里需要返回 true 给外层
            // 2. 当 targetSdk >= Android 13 并且在此版本及之上申请 WRITE_EXTERNAL_STORAGE，会被系统直接拒绝
            //    不会弹出系统授权对话框，框架为了保证不同 Android 版本的回调结果一致性，这里需要返回 true 给到外层
            // 基于上面这两个原因，所以当项目 targetSdk >= Android 11 并且运行在 Android 11 及以上的设备上面时
            // 判断 WRITE_EXTERNAL_STORAGE 权限，结果无论是否授予，最终都会直接返回 true 给外层
            return true;
        }
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_10)) {
            // Environment.isExternalStorageLegacy API 解释：是否采用的是非分区存储的模式
            return Environment.isExternalStorageLegacy();
        }
        return super.isGrantedByStandardVersion(context, skipRequest);
    }

    @SuppressLint("NewApi")
    @Override
    protected boolean isDoNotAskAgainByStandardVersion(@NonNull Activity activity) {
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(activity, AndroidVersionTools.ANDROID_11)) {
            return false;
        }
        // Environment.isExternalStorageLegacy API 解释：是否采用的是非分区存储的模式
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(activity, AndroidVersionTools.ANDROID_10) && Environment.isExternalStorageLegacy()) {
            return false;
        }
        return super.isDoNotAskAgainByStandardVersion(activity);
    }
}