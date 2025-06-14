package com.hjq.permissions.permission.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.AndroidManifestInfo;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionType;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 权限接口
 */
public interface IPermission extends Parcelable {

    /**
     * 获取权限类型
     */
    @NonNull
    PermissionType getType();

    /**
     * 获取权限名称
     */
    @NonNull
    String getName();

    /**
     * 获取权限出现的 Android 版本
     */
    int getFromAndroidVersion();

    /**
     * 获取使用这个权限最低要求的 targetSdk 版本
     */
    default int getMinTargetSdkVersion() {
        return getFromAndroidVersion();
    }

    /**
     * 判断当前权限是否授予
     */
    default boolean isGranted(@NonNull Context context) {
        return isGranted(context, true);
    }

    /**
     * 判断当前权限是否授予
     *
     * @param skipRequest       是否跳过申请直接判断的？
     */
    boolean isGranted(@NonNull Context context, boolean skipRequest);

    /**
     * 判断当前权限是否被用户勾选了《不再询问的选项》
     */
    boolean isDoNotAskAgain(@NonNull Activity activity);

    /**
     * 获取权限设置页的意图
     */
    @NonNull
    Intent getSettingIntent(@NonNull Context context);

    /**
     * 判断当前权限是否在低版本（不受支持的版本）上面运行
     */
    default boolean isLowVersionRunning() {
        return getFromAndroidVersion() > AndroidVersionTools.getCurrentAndroidVersionCode();
    }

    /**
     * 获取权限请求的间隔时间
     */
    default int getRequestIntervalTime() {
        return 0;
    }

    /**
     * 获取处理权限结果的等待时间
     */
    default int getResultWaitTime() {
        return 0;
    }

    /**
     * 权限自我检查
     */
    default void checkSelf(@NonNull Activity activity,
                            @NonNull List<IPermission> requestPermissions,
                            @Nullable AndroidManifestInfo androidManifestInfo) {}
}