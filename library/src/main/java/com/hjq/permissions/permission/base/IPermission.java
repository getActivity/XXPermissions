package com.hjq.permissions.permission.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.PermissionType;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 权限接口
 */
public interface IPermission extends Parcelable {

    /**
     * 获取权限的名称
     */
    @NonNull
    String getPermissionName();

    /**
     * 获取请求时的权限名称（默认为权限的名称）
     */
    default String getRequestPermissionName(Context context) {
        return getPermissionName();
    }

    /**
     * 获取权限的类型
     */
    @NonNull
    PermissionType getPermissionType();

    /**
     * 获取权限页面的类型
     */
    @NonNull
    PermissionPageType getPermissionPageType(@NonNull Context context);

    /**
     * 获取权限的组别
     */
    @Nullable
    default String getPermissionGroup() {
        // 返回空表示没有组别
        return null;
    }

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
     * 获取当前权限对应的旧权限
     */
    @Nullable
    default List<IPermission> getOldPermissions(Context context) {
        // 表示没有旧权限
        return null;
    }

    /**
     * 获取当前权限对应的前台权限
     */
    @Nullable
    default List<IPermission> getForegroundPermissions(@NonNull Context context) {
        // 表示没有前台权限
        return null;
    }

    /**
     * 当前权限是否为后台权限
     */
    default boolean isBackgroundPermission(@NonNull Context context) {
        List<IPermission> foregroundPermission = getForegroundPermissions(context);
        if (foregroundPermission == null) {
            return false;
        }
        return !foregroundPermission.isEmpty();
    }

    /**
     * 当前是否支持请求该权限
     */
    default boolean isSupportRequestPermission(@NonNull Context context) {
        // 如果当前权限是否在低版本（不受支持的版本）上面运行，则证明不支持请求该权限
        // 例如 MANAGE_EXTERNAL_STORAGE 权限是 Android 11 才出现的权限，在 Android 10 上面肯定是不支持申请
        return getFromAndroidVersion() <= PermissionVersion.getCurrentVersion();
    }

    /**
     * 判断当前权限是否授予
     */
    default boolean isGrantedPermission(@NonNull Context context) {
        return isGrantedPermission(context, true);
    }

    /**
     * 判断当前权限是否授予
     *
     * @param skipRequest       是否跳过申请直接判断的权限状态
     */
    boolean isGrantedPermission(@NonNull Context context, boolean skipRequest);

    /**
     * 判断当前权限是否被用户勾选了《不再询问的选项》
     */
    boolean isDoNotAskAgainPermission(@NonNull Activity activity);

    /**
     * 获取当前权限所有可用的设置页意图
     */
    @NonNull
    default List<Intent> getPermissionSettingIntents(@NonNull Context context) {
        return getPermissionSettingIntents(context, true);
    }

    /**
     * 获取当前权限所有可用的设置页意图
     *
     * 需要注意的是：无需在此方法中判断设置页的意图是否存在再添加，
     *             因为框架在跳转的时候框架会先过滤一遍不存在的意图，
     *             另外通过代码事先判断出来存在的意图也有可能会跳转失败，
     *             如果出现跳转失败会自动使用下一个意图进行跳转，
     *             总结：不存在的意图铁定会跳转失败，存在的意图不一定 100% 会跳转成功。
     *
     * @param skipRequest       是否跳过申请直接获取的 Intent
     */
    @NonNull
    List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest);

    /**
     * 获取权限请求的间隔时间
     */
    default int getRequestIntervalTime(@NonNull Context context) {
        return 0;
    }

    /**
     * 获取处理权限结果的等待时间
     */
    default int getResultWaitTime(@NonNull Context context) {
        return 0;
    }

    /**
     * 检查权限请求是否合规
     */
    default void checkCompliance(@NonNull Activity activity,
                                 @NonNull List<IPermission> requestList,
                                 @Nullable AndroidManifestInfo manifestInfo) {}
}