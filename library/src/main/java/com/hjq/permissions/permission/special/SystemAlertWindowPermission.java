package com.hjq.permissions.permission.special;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionActivityIntentHandler;
import com.hjq.permissions.PermissionIntentManager;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.PhoneRomUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 悬浮窗权限类
 */
public final class SystemAlertWindowPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.SYSTEM_ALERT_WINDOW;

    public static final Parcelable.Creator<SystemAlertWindowPermission> CREATOR = new Parcelable.Creator<SystemAlertWindowPermission>() {

        @Override
        public SystemAlertWindowPermission createFromParcel(Parcel source) {
            return new SystemAlertWindowPermission(source);
        }

        @Override
        public SystemAlertWindowPermission[] newArray(int size) {
            return new SystemAlertWindowPermission[size];
        }
    };

    private static final String OP_SYSTEM_ALERT_WINDOW_FIELD_NAME = "OP_SYSTEM_ALERT_WINDOW";
    private static final int OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE = 24;

    public SystemAlertWindowPermission() {
        // default implementation ignored
    }

    private SystemAlertWindowPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        // 虽然悬浮窗权限是在 Android 6.0 新增的权限，但是有些国产的厂商在 Android 6.0 之前的版本就自己加了，并且框架已经有做兼容了
        // 所以为了兼容更低的 Android 版本，这里需要将悬浮窗权限出现的 Android 版本成 API 17（即框架要求 minSdkVersion 版本）
        return AndroidVersionTools.ANDROID_4_2;
    }

    @Override
    public boolean isGranted(@NonNull Context context, boolean skipRequest) {
        if (AndroidVersionTools.isAndroid6()) {
            return Settings.canDrawOverlays(context);
        }

        // 经过测试在 vivo x7 Plus（Android 5.1）和 OPPO A53 （Android 5.1 ColorOs 2.1）的机子上面判断不准确
        // 经过 debug 发现并不是 vivo 和 oppo 修改了 OP_SYSTEM_ALERT_WINDOW 的赋值导致的
        // 估计是 vivo 和 oppo 的机子修改了整个悬浮窗机制，这种就没有办法了
        return checkOpNoThrow(context, OP_SYSTEM_ALERT_WINDOW_FIELD_NAME, OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE);
    }

    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        if (AndroidVersionTools.isAndroid6()) {
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

            if (AndroidVersionTools.isAndroid11() && PhoneRomUtils.isMiui() && PhoneRomUtils.isMiuiOptimization()) {
                // 因为 Android 11 及后面的版本无法直接跳转到具体权限设置页面，只能跳转到悬浮窗权限应用列表，十分地麻烦的，这里做了一下简化
                // miui 做得比较人性化的，不会出现跳转不过去的问题，其他厂商就不一定了，就是不想让你跳转过去
                Intent intent = PermissionIntentManager.getMiuiPermissionPageIntent(context);
                // 另外跳转到应用详情页也可以开启悬浮窗权限
                intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, getApplicationDetailsIntent(context));
                return intent;
            }

            Intent intent = getManageOverlayPermissionIntent(context);
            if (PermissionUtils.areActivityIntent(context, intent)) {
                return intent;
            }

            intent = getApplicationDetailsIntent(context);
            return intent;
        }

        // 需要注意的是，这里不需要判断鸿蒙，因为鸿蒙 2.0 用代码判断是 API 等级是 29（Android 10）会直接走上面的逻辑，而不会走到下面来
        if (PhoneRomUtils.isEmui()) {
            Intent intent = PermissionIntentManager.getEmuiWindowPermissionPageIntent(context);
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, getApplicationDetailsIntent(context));
            return intent;
        }

        if (PhoneRomUtils.isMiui()) {

            Intent intent = null;
            if (PhoneRomUtils.isMiuiOptimization()) {
                // 假设关闭了 miui 优化，就不走这里的逻辑
                intent = PermissionIntentManager.getMiuiWindowPermissionPageIntent(context);
            }

            // 小米手机也可以通过应用详情页开启悬浮窗权限（只不过会多一步操作）
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, getApplicationDetailsIntent(context));
            return intent;
        }

        if (PhoneRomUtils.isColorOs()) {
            Intent intent = PermissionIntentManager.getColorOsWindowPermissionPageIntent(context);
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, getApplicationDetailsIntent(context));
            return intent;
        }

        if (PhoneRomUtils.isOriginOs()) {
            Intent intent = PermissionIntentManager.getOriginOsWindowPermissionPageIntent(context);
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, getApplicationDetailsIntent(context));
            return intent;
        }

        if (PhoneRomUtils.isOneUi()) {
            Intent intent = PermissionIntentManager.getOneUiWindowPermissionPageIntent(context);
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, getApplicationDetailsIntent(context));
            return intent;
        }

        // 经过测试，锤子手机 5.1 及以上的手机的可以直接通过直接跳转到应用详情开启悬浮窗权限，但是 4.4 以下的手机就不行，需要跳转到安全中心
        if (PhoneRomUtils.isSmartisanOS() && !AndroidVersionTools.isAndroid5_1()) {
            Intent intent = PermissionIntentManager.getSmartisanWindowPermissionPageIntent(context);
            intent = PermissionActivityIntentHandler.addSubIntentForMainIntent(intent, getApplicationDetailsIntent(context));
            return intent;
        }

        // 360 第一部发布的手机是 360 N4，Android 版本是 6.0 了，所以根本不需要跳转到指定的页面开启悬浮窗权限
        // 经过测试，魅族手机 6.0 可以直接通过直接跳转到应用详情开启悬浮窗权限

        return getApplicationDetailsIntent(context);
    }

    @RequiresApi(AndroidVersionTools.ANDROID_6)
    private static Intent getManageOverlayPermissionIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        // 在 Android 11 加包名跳转也是没有效果的，官方文档链接：
        // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
        if (!AndroidVersionTools.isAndroid11()) {
            intent.setData(PermissionUtils.getPackageNameUri(context));
        }
        return intent;
    }
}