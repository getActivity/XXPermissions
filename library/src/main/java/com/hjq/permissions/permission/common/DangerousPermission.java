package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionType;
import com.hjq.permissions.permission.base.BasePermission;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 危险权限的基类
 */
public abstract class DangerousPermission extends BasePermission {

    protected DangerousPermission() {
        super();
    }

    protected DangerousPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public PermissionType getType() {
        return PermissionType.DANGEROUS;
    }

    @Override
    public boolean isGranted(@NonNull Context context, boolean skipRequest) {
        if (isLowVersionRunning()) {
            return isGrantedByLowVersion(context, skipRequest);
        }
        return isGrantedByStandardVersion(context, skipRequest);
    }

    protected boolean isGrantedByStandardVersion(@NonNull Context context, boolean skipRequest) {
        return checkSelfPermission(context, getName());
    }

    protected boolean isGrantedByLowVersion(@NonNull Context context, boolean skipRequest) {
        return true;
    }

    @Override
    public boolean isDoNotAskAgain(@NonNull Activity activity) {
        if (isLowVersionRunning()) {
            return isDoNotAskAgainByLowVersion(activity);
        }
        return isDoNotAskAgainByStandardVersion(activity);
    }

    protected boolean isDoNotAskAgainByStandardVersion(@NonNull Activity activity) {
        return checkDoNotAskAgainPermission(activity, getName());
    }

    protected boolean isDoNotAskAgainByLowVersion(@NonNull Activity activity) {
        return false;
    }

    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        return getApplicationDetailsIntent(context);
    }

    /**
     * 判断某个危险权限是否授予了
     */
    public static boolean checkSelfPermission(@NonNull Context context, @NonNull String permission) {
        if (!AndroidVersionTools.isAndroid6()) {
            return true;
        }
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断是否应该向用户显示请求权限的理由
     */
    @SuppressWarnings({"JavaReflectionMemberAccess", "ConstantConditions", "BooleanMethodIsAlwaysInverted"})
    public static boolean shouldShowRequestPermissionRationale(@NonNull Activity activity, @NonNull String permission) {
        if (!AndroidVersionTools.isAndroid6()) {
            return false;
        }
        // 解决 Android 12 调用 shouldShowRequestPermissionRationale 出现内存泄漏的问题
        // Android 12L 和 Android 13 版本经过测试不会出现这个问题，证明 Google 在新版本上已经修复了这个问题
        // 但是对于 Android 12 仍是一个历史遗留问题，这是我们所有 Android App 开发者不得不面对的一个事情
        // issue 地址：https://github.com/getActivity/XXPermissions/issues/133
        if (AndroidVersionTools.getCurrentAndroidVersionCode() == AndroidVersionTools.ANDROID_12) {
            try {
                // 另外针对这个问题，我还给谷歌的 AndroidX 项目无偿提供了解决方案，目前 Merge Request 已被合入主分支
                // 我相信通过这一举措，将解决全球近 10 亿台 Android 12 设备出现的内存泄露问题
                // Pull Request 地址：https://github.com/androidx/androidx/pull/435
                PackageManager packageManager = activity.getApplication().getPackageManager();
                Method method = PackageManager.class.getMethod("shouldShowRequestPermissionRationale", String.class);
                return (boolean) method.invoke(packageManager, permission);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return activity.shouldShowRequestPermissionRationale(permission);
    }

    /**
     * 判断某个危险权限是否勾选了不再询问的选项
     */
    public static boolean checkDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        if (!AndroidVersionTools.isAndroid6()) {
            return false;
        }
        return !checkSelfPermission(activity, permission) &&
            !shouldShowRequestPermissionRationale(activity, permission);
    }
}