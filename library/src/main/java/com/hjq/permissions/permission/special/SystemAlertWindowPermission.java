package com.hjq.permissions.permission.special;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.AndroidVersion;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PhoneRomUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 悬浮窗权限类
 */
public final class SystemAlertWindowPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.SYSTEM_ALERT_WINDOW;

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
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        // 虽然悬浮窗权限是在 Android 6.0 新增的权限，但是有些国产的厂商在 Android 6.0 之前的版本就自己加了，并且框架已经有做兼容了
        // 所以为了兼容更低的 Android 版本，这里需要将悬浮窗权限出现的 Android 版本成 API 17（即框架要求 minSdkVersion 版本）
        return AndroidVersion.ANDROID_4_2;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (AndroidVersion.isAndroid6()) {
            return Settings.canDrawOverlays(context);
        }

        if (!AndroidVersion.isAndroid4_4()) {
            return true;
        }

        // 经过测试在 vivo x7 Plus（Android 5.1）和 OPPO A53 （Android 5.1 ColorOs 2.1）的机子上面判断不准确
        // 经过 debug 发现并不是 vivo 和 oppo 修改了 OP_SYSTEM_ALERT_WINDOW 的赋值导致的
        // 估计是 vivo 和 oppo 的机子修改了整个悬浮窗机制，这种就没有办法了
        return checkOpPermission(context, OP_SYSTEM_ALERT_WINDOW_FIELD_NAME, OP_SYSTEM_ALERT_WINDOW_DEFAULT_VALUE, true);
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context) {
        List<Intent> intentList = new ArrayList<>();
        Intent intent;

        if (AndroidVersion.isAndroid6()) {
            // 如果当前系统是 HyperOs，那么就不要跳转到 miui 权限设置页了，因为还要点一下《其他权限》入口才能找到悬浮窗权限设置选项
            // 这样的效果还不如直接跳转到所有应用的悬浮窗权限设置列表，然后再点进去来得更直观
            // 相关 Github issue 地址：https://github.com/getActivity/XXPermissions/issues/342
            if (AndroidVersion.isAndroid11() && !PhoneRomUtils.isHyperOs() &&
                        PhoneRomUtils.isMiui() && PhoneRomUtils.isXiaomiSystemOptimization()) {
                // 因为 Android 11 及后面的版本无法直接跳转到具体权限设置页面，只能跳转到悬浮窗权限应用列表，十分地麻烦的，这里做了一下简化
                // miui 做得比较人性化的，不会出现跳转不过去的问题，其他厂商就不一定了，就是不想让你跳转过去
                intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                intentList.add(intent);
            }

            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(PermissionUtils.getPackageNameUri(context));
            intentList.add(intent);

            // 在 Android 11 加包名跳转也是没有效果的，官方文档链接：
            // https://developer.android.google.cn/reference/android/provider/Settings#ACTION_MANAGE_OVERLAY_PERMISSION
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intentList.add(intent);

        } else {

            // 需要注意的是，这里不需要判断鸿蒙，因为鸿蒙 2.0 用代码判断是 API 等级是 29（Android 10）会直接走上面的逻辑，而不会走到下面来
            if (PhoneRomUtils.isEmui()) {
                // EMUI 发展史：http://www.360doc.com/content/19/1017/10/9113704_867381705.shtml
                // android 华为版本历史,一文看完华为EMUI发展史：https://blog.csdn.net/weixin_39959369/article/details/117351161

                Intent addViewMonitorActivityIntent = new Intent();
                // emui 3.1 的适配（华为荣耀 7 Android 5.0、华为揽阅 M2 青春版 Android 5.1、华为畅享 5S Android 5.1）
                addViewMonitorActivityIntent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");

                Intent notificationManagementActivityIntent = new Intent();
                // emui 3.0 的适配（华为麦芒 3S Android 4.4）
                notificationManagementActivityIntent.setClassName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity");

                // 获取厂商版本号
                String romVersionName = PhoneRomUtils.getRomVersionName();
                if (romVersionName == null) {
                    romVersionName = "";
                }

                if (romVersionName.startsWith("3.0")) {
                    // 3.0、3.0.1
                    intentList.add(notificationManagementActivityIntent);
                    intentList.add(addViewMonitorActivityIntent);
                } else {
                    // 3.1、其他的
                    intentList.add(addViewMonitorActivityIntent);
                    intentList.add(notificationManagementActivityIntent);
                }

                // 华为手机管家主页
                intent = PermissionSettingPage.getHuaWeiMobileManagerAppIntent(context);
                intentList.add(intent);

            } else if (PhoneRomUtils.isMiui()) {
                // 假设关闭了 miui 优化，就不走这里的逻辑
                // 小米手机也可以通过应用详情页开启悬浮窗权限（只不过会多一步操作）
                if (PhoneRomUtils.isXiaomiSystemOptimization()) {
                    intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                    intentList.add(intent);
                }
            } else if (PhoneRomUtils.isColorOs()) {
                // com.color.safecenter 是之前 oppo 安全中心的包名，而 com.oppo.safe 是 oppo 后面改的安全中心的包名
                // 经过测试发现是在 ColorOs 2.1 的时候改的，Android 4.4 还是 com.color.safecenter，到了 Android 5.0 变成了 com.oppo.safe

                // java.lang.SecurityException: Permission Denial: starting Intent
                // { cmp=com.oppo.safe/.permission.floatwindow.FloatWindowListActivity (has extras) } from
                // ProcessRecord{839a7c5 10595:com.hjq.permissions.demo/u0a3781} (pid=10595, uid=13781) not exported from uid 1000
                // intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity");

                // java.lang.SecurityException: Permission Denial: starting Intent
                // { cmp=com.color.safecenter/.permission.floatwindow.FloatWindowListActivity (has extras) } from
                // ProcessRecord{42b660b0 31279:com.hjq.permissions.demo/u0a204} (pid=31279, uid=10204) not exported from uid 1000
                // intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");

                // java.lang.SecurityException: Permission Denial: starting Intent
                // { cmp=com.color.safecenter/.permission.PermissionAppAllPermissionActivity (has extras) } from
                // ProcessRecord{42c49dd8 1791:com.hjq.permissions.demo/u0a204} (pid=1791, uid=10204) not exported from uid 1000
                // intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionAppAllPermissionActivity");

                // 虽然不能直接到达悬浮窗界面，但是到达它的上一级页面（权限隐私页面）还是可以的，所以做了简单的取舍
                // 测试机是 OPPO R7 Plus（Android 5.0，ColorOs 2.1）、OPPO R7s（Android 4.4，ColorOs 2.1）
                // com.oppo.safe.permission.PermissionTopActivity
                // com.oppo.safe..permission.PermissionAppListActivity
                // com.color.safecenter.permission.PermissionTopActivity

                intent = new Intent();
                intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionTopActivity");
                intentList.add(intent);

                intent = PermissionSettingPage.getOppoSafeCenterAppIntent(context);
                intentList.add(intent);

            } else if (PhoneRomUtils.isOriginOs()) {
                // java.lang.SecurityException: Permission Denial: starting Intent
                // { cmp=com.iqoo.secure/.ui.phoneoptimize.FloatWindowManager (has extras) } from
                // ProcessRecord{2c3023cf 21847:com.hjq.permissions.demo/u0a4633} (pid=21847, uid=14633) not exported from uid 10055
                // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager");

                // java.lang.SecurityException: Permission Denial: starting Intent
                // { cmp=com.iqoo.secure/.safeguard.PurviewTabActivity (has extras) } from
                // ProcessRecord{2c3023cf 21847:com.hjq.permissions.demo/u0a4633} (pid=21847, uid=14633) not exported from uid 10055
                // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.PurviewTabActivity");

                // 经过测试在 vivo x7 Plus（Android 5.1）上面能跳转过去，但是显示却是一个空白页面
                // intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");
                intent = PermissionSettingPage.getVivoMobileManagerAppIntent(context);
                intentList.add(intent);
            } else if (PhoneRomUtils.isOneUi()) {
                intent = PermissionSettingPage.getOneUiPermissionPageIntent(context);
                intentList.add(intent);
            } else if (PhoneRomUtils.isSmartisanOS() && !AndroidVersion.isAndroid5_1()) {
                // 经过测试，锤子手机 5.1 及以上的手机的可以直接通过直接跳转到应用详情开启悬浮窗权限，但是 4.4 以下的手机就不行，需要跳转到安全中心
                intent = PermissionSettingPage.getSmartisanPermissionPageIntent(context);
                intentList.add(intent);
            }

            // 360 第一部发布的手机是 360 N4，Android 版本是 6.0 了，所以根本不需要跳转到指定的页面开启悬浮窗权限
            // 经过测试，魅族手机 6.0 可以直接通过直接跳转到应用详情开启悬浮窗权限
        }

        intent = getApplicationDetailsIntent(context);
        intentList.add(intent);

        return intentList;
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 表示当前权限需要在 AndroidManifest.xml 文件中进行静态注册
        return true;
    }
}