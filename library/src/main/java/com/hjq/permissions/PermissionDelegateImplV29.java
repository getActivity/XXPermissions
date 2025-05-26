package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : Android 10 权限委托实现
 */
class PermissionDelegateImplV29 extends PermissionDelegateImplV28 {

    @Override
    public boolean isGrantedPermission(@NonNull Context context, @NonNull String permission, boolean requestFlag) {
        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_MEDIA_LOCATION)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return true;
            }
            if (!AndroidVersionTools.isAndroid10()) {
                return PermissionUtils.isGrantedPermission(context, Permission.READ_EXTERNAL_STORAGE);
            }
            return isGrantedReadMediaPermission(context) &&
                PermissionUtils.isGrantedPermission(context, Permission.ACCESS_MEDIA_LOCATION);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return true;
            }
            if (!AndroidVersionTools.isAndroid10()) {
                return PermissionUtils.isGrantedPermission(context, Permission.ACCESS_FINE_LOCATION);
            }
            return PermissionUtils.isGrantedPermission(context, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACTIVITY_RECOGNITION)) {
            if (!AndroidVersionTools.isAndroid10()) {
                return true;
            }
            return PermissionUtils.isGrantedPermission(context, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.WRITE_EXTERNAL_STORAGE)) {
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
                return isUseDeprecationExternalStorage();
            }
            return PermissionUtils.isGrantedPermission(context, permission);
        }

        return super.isGrantedPermission(context, permission, requestFlag);
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_BACKGROUND_LOCATION)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return false;
            }
            if (!AndroidVersionTools.isAndroid10()) {
                return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.ACCESS_FINE_LOCATION);
            }
            // 先检查前台的定位权限是否拒绝了
            if (!PermissionUtils.isGrantedPermission(activity, Permission.ACCESS_FINE_LOCATION)) {
                // 如果是的话就判断前台的定位权限是否被永久拒绝了
                return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.ACCESS_FINE_LOCATION);
            }
            // 如果不是的话再去判断后台的定位权限是否被拒永久拒绝了
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACCESS_MEDIA_LOCATION)) {
            if (!AndroidVersionTools.isAndroid6()) {
                return false;
            }
            if (!AndroidVersionTools.isAndroid10()) {
                return PermissionUtils.isDoNotAskAgainPermission(activity, Permission.READ_EXTERNAL_STORAGE);
            }
            return isGrantedReadMediaPermission(activity) && PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.ACTIVITY_RECOGNITION)) {
            if (!AndroidVersionTools.isAndroid10()) {
                return false;
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        if (PermissionUtils.equalsPermission(permission, Permission.WRITE_EXTERNAL_STORAGE)) {
            if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(activity, AndroidVersionTools.ANDROID_11)) {
                return false;
            }
            if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(activity, AndroidVersionTools.ANDROID_10) &&
                    isUseDeprecationExternalStorage()) {
                return false;
            }
            return PermissionUtils.isDoNotAskAgainPermission(activity, permission);
        }

        return super.isDoNotAskAgainPermission(activity, permission);
    }

    /**
     * 判断是否授予了读取媒体的权限
     */
    @RequiresApi(AndroidVersionTools.ANDROID_6)
    private boolean isGrantedReadMediaPermission(@NonNull Context context) {
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_13)) {
            // 这里为什么加上 Android 14 和 READ_MEDIA_VISUAL_USER_SELECTED 权限判断？这是因为如果获取部分照片和视频
            // 然后申请 Permission.ACCESS_MEDIA_LOCATION 系统会返回失败，必须要选择获取全部照片和视频才可以申请该权限
            return PermissionUtils.isGrantedPermission(context, Permission.READ_MEDIA_IMAGES) ||
                    PermissionUtils.isGrantedPermission(context, Permission.READ_MEDIA_VIDEO) ||
                isGrantedPermission(context, Permission.MANAGE_EXTERNAL_STORAGE, false);
        }
        if (AndroidVersionTools.isAdaptationAndroidVersionNewFeatures(context, AndroidVersionTools.ANDROID_11)) {
            return PermissionUtils.isGrantedPermission(context, Permission.READ_EXTERNAL_STORAGE) ||
                isGrantedPermission(context, Permission.MANAGE_EXTERNAL_STORAGE, false);
        }
        return PermissionUtils.isGrantedPermission(context, Permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * 是否采用的是非分区存储的模式
     */
    @RequiresApi(AndroidVersionTools.ANDROID_10)
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean isUseDeprecationExternalStorage() {
        return Environment.isExternalStorageLegacy();
    }
}