package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.support.annotation.NonNull;
import com.hjq.permissions.tools.AndroidVersionTools;
import com.hjq.permissions.permission.PermissionType;
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
    public PermissionType getPermissionType() {
        return PermissionType.DANGEROUS;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        // 判断权限是不是在旧系统上面运行（权限出现的版本 > 当前系统的版本）
        if (isLowVersionRunning()) {
            return isGrantedPermissionByLowVersion(context, skipRequest);
        }
        return isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    /**
     * 在标准版本的系统上面判断权限是否授予
     */
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        return checkSelfPermission(context, getPermissionName());
    }

    /**
     * 在低版本的系统上面判断权限是否授予
     */
    protected boolean isGrantedPermissionByLowVersion(@NonNull Context context, boolean skipRequest) {
        return true;
    }

    @Override
    public boolean isDoNotAskAgainPermission(@NonNull Activity activity) {
        // 判断权限是不是在旧系统上面运行（权限出现的版本 > 当前系统的版本）
        if (isLowVersionRunning()) {
            return isDoNotAskAgainPermissionByLowVersion(activity);
        }
        return isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    /**
     * 在标准版本的系统上面判断权限是否被用户勾选了《不再询问的选项》
     */
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        return checkDoNotAskAgainPermission(activity, getPermissionName());
    }

    /**
     * 在低版本的系统上面判断权限是否被用户勾选了《不再询问的选项》
     */
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return false;
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        return getApplicationDetailsIntent(context);
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 危险权限默认需要在清单文件中注册，这样定义是为了避免外层在自定义特殊权限的时候，还要去重写此方法
        return true;
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