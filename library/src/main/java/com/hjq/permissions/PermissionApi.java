package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/12/31
 *    desc   : 权限判断类
 */
final class PermissionApi {

    @NonNull
    private static final IPermissionDelegate DELEGATE = new PermissionDelegateImplV34();

    /**
     * 判断某个权限是否授予
     */
    static boolean isGrantedPermission(@NonNull Context context, @NonNull String permission) {
        return isGrantedPermission(context, permission, true);
    }

    /**
     * 判断某个权限是否授予
     *
     * @param skipRequest           是否跳过权限请求，直接判断权限状态
     */
    static boolean isGrantedPermission(@NonNull Context context, @NonNull String permission, boolean skipRequest) {
        return DELEGATE.isGrantedPermission(context, permission, skipRequest);
    }

    /**
     * 判断某个权限是否被永久拒绝
     */
    static boolean isDoNotAskAgainPermission(@NonNull Activity activity, @NonNull String permission) {
        return DELEGATE.isDoNotAskAgainPermission(activity, permission);
    }

    /**
     * 获取权限设置页的意图
     */
    static Intent getPermissionSettingIntent(@NonNull Context context, @NonNull String permission) {
        return DELEGATE.getPermissionSettingIntent(context, permission);
    }

    /**
     * 判断某个权限是否是特殊权限
     */
    static boolean isSpecialPermission(@NonNull String permission) {
        return PermissionHelper.isSpecialPermission(permission);
    }

    /**
     * 判断某个权限是否是后台权限
     */
    static boolean isBackgroundPermission(@NonNull String permission) {
        return PermissionHelper.isBackgroundPermission(permission);
    }

    /**
     * 判断某个权限集合是否包含特殊权限
     */
    static boolean containsSpecialPermission(@Nullable List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        for (String permission : permissions) {
            if (isSpecialPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个权限集合是否包含后台权限
     */
    static boolean containsBackgroundPermission(@Nullable List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        for (String permission : permissions) {
            if (isBackgroundPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某些权限是否全部被授予
     */
    static boolean isGrantedPermissions(@NonNull Context context, @NonNull List<String> permissions) {
        if (permissions.isEmpty()) {
            return false;
        }

        for (String permission : permissions) {
            if (!isGrantedPermission(context, permission)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取已经授予的权限
     */
    static List<String> getGrantedPermissions(@NonNull Context context, @NonNull List<String> permissions) {
        List<String> grantedPermissions = new ArrayList<>(permissions.size());
        for (String permission : permissions) {
            if (isGrantedPermission(context, permission)) {
                grantedPermissions.add(permission);
            }
        }
        return grantedPermissions;
    }

    /**
     * 获取已经拒绝的权限
     */
    static List<String> getDeniedPermissions(@NonNull Context context, @NonNull List<String> permissions) {
        List<String> deniedPermissions = new ArrayList<>(permissions.size());
        for (String permission : permissions) {
            if (!isGrantedPermission(context, permission)) {
                deniedPermissions.add(permission);
            }
        }
        return deniedPermissions;
    }

    /**
     * 在权限组中检查是否有某个权限是否被永久拒绝
     *
     * @param activity              Activity对象
     * @param permissions            请求的权限
     */
    static boolean isDoNotAskAgainPermissions(@NonNull Activity activity, @NonNull List<String> permissions) {
        for (String permission : permissions) {
            if (isDoNotAskAgainPermission(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据传入的权限自动选择最合适的权限设置页的意图
     */
    static Intent getBestPermissionSettingIntent(@NonNull Context context, @Nullable List<String> permissions) {
        // 如果失败的权限里面不包含特殊权限
        if (permissions == null || permissions.isEmpty()) {
            return PermissionIntentManager.getApplicationDetailsIntent(context);
        }

        // 创建一个新的集合对象，避免复用对象可能引发外层的冲突
        List<String> realPermissions = new ArrayList<>(permissions);
        for (String permission : permissions) {
            if (PermissionHelper.findAndroidVersionByPermission(permission) >
                            AndroidVersionTools.getCurrentAndroidVersionCode()) {
                // 如果当前权限是高版本才出现的权限，则进行剔除
                realPermissions.remove(permission);
                continue;
            }

            List<String> oldPermissions = PermissionHelper.queryOldPermissionByNewPermission(permission);
            // 1. 如果旧版本列表不为空，并且当前权限是特殊权限，就剔除它对应的旧版本权限
            // 例如：MANAGE_EXTERNAL_STORAGE -> READ_EXTERNAL_STORAGE、WRITE_EXTERNAL_STORAGE
            // 2. 如果旧版本列表不为空，并且当前权限对应的旧版本权限包含了特殊权限，就剔除它对应的旧版本权限
            // 例如：POST_NOTIFICATIONS -> NOTIFICATION_SERVICE
            if (oldPermissions != null && !oldPermissions.isEmpty() &&
                (isSpecialPermission(permission) || containsSpecialPermission(oldPermissions))) {
                realPermissions.removeAll(oldPermissions);
            }
        }

        if (realPermissions.isEmpty()) {
            return PermissionIntentManager.getApplicationDetailsIntent(context);
        }

        if (realPermissions.size() == 1) {
            return PermissionApi.getPermissionSettingIntent(context, realPermissions.get(0));
        }

        return PermissionIntentManager.getApplicationDetailsIntent(context, realPermissions);
    }

    /**
     * 根据新权限添加旧权限
     */
    static void addOldPermissionsByNewPermissions(@NonNull List<String> requestPermissions) {
        // 需要补充的权限列表
        List<String> needSupplementPermissions =  null;
        for (String permission : requestPermissions) {
            // 如果当前运行的 Android 版本大于权限出现的 Android 版本，则证明这个权限在当前设备上不用添加旧权限
            if (AndroidVersionTools.getCurrentAndroidVersionCode() >= PermissionHelper.findAndroidVersionByPermission(permission)) {
                continue;
            }
            // 通过新权限查询到对应的旧权限
            List<String> oldPermissions = PermissionHelper.queryOldPermissionByNewPermission(permission);
            if (oldPermissions == null || oldPermissions.isEmpty()) {
                continue;
            }
            for (String oldPermission : oldPermissions) {
                // 如果请求列表已经包含此权限，就不重复添加，直接跳过
                if (PermissionUtils.containsPermission(requestPermissions, oldPermission)) {
                    continue;
                }
                if (needSupplementPermissions == null) {
                    needSupplementPermissions = new ArrayList<>();
                }
                // 先检查一下有没有添加过，避免重复添加
                if (PermissionUtils.containsPermission(needSupplementPermissions, oldPermission)) {
                    continue;
                }
                // 添加旧版的权限到需要补充的权限列表中
                // 这里解释一下为什么直接添加到 requestPermissions 对象？而是重新弄一个新的集合来存放
                // 这是当前 for 循环正在遍历 requestPermissions 对象，如果在此时添加新的元素，会导致异常
                needSupplementPermissions.add(oldPermission);
            }
        }

        if (needSupplementPermissions == null || needSupplementPermissions.isEmpty()) {
            return;
        }
        requestPermissions.addAll(needSupplementPermissions);
    }

    /**
     * 调整权限的请求顺序
     */
    static void adjustPermissionsSort(@NonNull List<String> requestPermissions) {
        // 获取低等级权限列表
        List<String> lowLevelPermissions = PermissionHelper.getLowLevelPermissions();
        for (String lowLevelPermission : lowLevelPermissions) {
            if (!PermissionUtils.containsPermission(requestPermissions, lowLevelPermission)) {
                continue;
            }
            // 如果请求的权限中包含这个低等级权限，则先删除再添加，这个权限就会排到最后面了
            // 这样做的好处在于，可以避免出现的一种情况，当前这个权限严重依赖其他权限
            // 例如：ACCESS_MEDIA_LOCATION 权限需要已授予存储相关权限的情况下才可以申请成功
            requestPermissions.remove(lowLevelPermission);
            requestPermissions.add(lowLevelPermission);
        }
    }

    /**
     * 判断传入的权限组是不是都是危险权限
     */
    static boolean areAllDangerousPermission(@NonNull List<String> permissions) {
        for (String permission : permissions) {
            if (isSpecialPermission(permission)) {
                return false;
            }
        }
        return true;
    }
}