package com.hjq.permissions;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/03/11
 *    desc   : 悬浮窗权限兼容类
 */
final class WindowPermissionCompat {

    private static final String OP_SYSTEM_ALERT_WINDOW_FIELD_NAME = "OP_SYSTEM_ALERT_WINDOW";
    private static final int OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE = 24;

    static boolean isGrantedPermission(@NonNull Context context) {
        if (AndroidVersion.isAndroid6()) {
            return Settings.canDrawOverlays(context);
        }

        if (AndroidVersion.isAndroid4_4()) {
            // 经过测试在 vivo x7 Plus（Android 5.1）和 OPPO A53 （Android 5.1 ColorOs 2.1）的机子上面判断不准确
            // 经过 debug 发现并不是 vivo 和 oppo 修改了 OP_SYSTEM_ALERT_WINDOW 的赋值导致的
            // 估计是 vivo 和 oppo 的机子修改了整个悬浮窗机制，这种就没有办法了
            return PermissionUtils.checkOpNoThrow(context, OP_SYSTEM_ALERT_WINDOW_FIELD_NAME, OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE);
        }

        return true;
    }

    static Intent getPermissionIntent(@NonNull Context context) {
        if (AndroidVersion.isAndroid6()) {
            // 如果当前系统是 HyperOs，那么就不要跳转到 miui 权限设置页了，因为还要点一下《其他权限》入口才能找到悬浮窗权限设置选项
            // 这样的效果还不如直接跳转到所有应用的悬浮窗权限设置列表，然后再点进去来得更直观
            // 需要注意的是：该逻辑需要在判断 miui 系统之前判断，因为在 HyperOs 系统上面判断当前系统是否为 miui 系统也会返回 true
            // 这是因为 HyperOs 系统本身就是从 miui 系统演变而来，有这个问题也很正常，主要是厂商为了系统兼容性而保留的
            // 相关 Github issue 地址：https://github.com/getActivity/XXPermissions/issues/342
            if (PhoneRomUtils.isHyperOs()) {
                Intent intent = getManageOverlayPermissionIntent(context);
                if (PermissionUtils.areActivityIntent(context, intent)) {
                    return intent;
                }
            }

            if (AndroidVersion.isAndroid11() && PhoneRomUtils.isMiui() && PhoneRomUtils.isMiuiOptimization()) {
                // 因为 Android 11 及后面的版本无法直接跳转到具体权限设置页面，只能跳转到悬浮窗权限应用列表，十分地麻烦的，这里做了一下简化
                // miui 做得比较人性化的，不会出现跳转不过去的问题，其他厂商就不一定了，就是不想让你跳转过去
                Intent intent = PermissionIntentManager.getMiuiPermissionPageIntent(context);
                // 另外跳转到应用详情页也可以开启悬浮窗权限
                intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, PermissionIntentManager.getApplicationDetailsIntent(context));
                return intent;
            }

            Intent intent = getManageOverlayPermissionIntent(context);
            if (PermissionUtils.areActivityIntent(context, intent)) {
                return intent;
            }

            intent = PermissionIntentManager.getApplicationDetailsIntent(context);
            return intent;
        }

        // 需要注意的是，这里不需要判断鸿蒙，因为鸿蒙 2.0 用代码判断是 API 等级是 29（Android 10）会直接走上面的逻辑，而不会走到下面来
        if (PhoneRomUtils.isEmui()) {
            Intent intent = PermissionIntentManager.getEmuiWindowPermissionPageIntent(context);
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, PermissionIntentManager.getApplicationDetailsIntent(context));
            return intent;
        }

        if (PhoneRomUtils.isMiui()) {

            Intent intent = null;
            if (PhoneRomUtils.isMiuiOptimization()) {
                // 假设关闭了 miui 优化，就不走这里的逻辑
                intent = PermissionIntentManager.getMiuiWindowPermissionPageIntent(context);
            }

            // 小米手机也可以通过应用详情页开启悬浮窗权限（只不过会多一步操作）
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, PermissionIntentManager.getApplicationDetailsIntent(context));
            return intent;
        }

        if (PhoneRomUtils.isColorOs()) {
            Intent intent = PermissionIntentManager.getColorOsWindowPermissionPageIntent(context);
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, PermissionIntentManager.getApplicationDetailsIntent(context));
            return intent;
        }

        if (PhoneRomUtils.isOriginOs()) {
            Intent intent = PermissionIntentManager.getOriginOsWindowPermissionPageIntent(context);
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, PermissionIntentManager.getApplicationDetailsIntent(context));
            return intent;
        }

        if (PhoneRomUtils.isOneUi()) {
            Intent intent = PermissionIntentManager.getOneUiWindowPermissionPageIntent(context);
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, PermissionIntentManager.getApplicationDetailsIntent(context));
            return intent;
        }

        // 360 第一部发布的手机是 360 N4，Android 版本是 6.0 了，所以根本不需要跳转到指定的页面开启悬浮窗权限
        // 经过测试，锤子手机 6.0 以下手机的可以直接通过直接跳转到应用详情开启悬浮窗权限
        // 经过测试，魅族手机 6.0 可以直接通过直接跳转到应用详情开启悬浮窗权限

        return PermissionIntentManager.getApplicationDetailsIntent(context);
    }

    @RequiresApi(AndroidVersion.ANDROID_6)
    private static Intent getManageOverlayPermissionIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        // 在 Android 11 加包名跳转也是没有效果的，官方文档链接：
        // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
        if (!AndroidVersion.isAndroid11()) {
            intent.setData(PermissionUtils.getPackageNameUri(context));
        }
        return intent;
    }
}