package com.hjq.permissions.permission.special;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionUtils;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PhoneRomUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 请求忽略电池优化选项权限类
 */
public final class RequestIgnoreBatteryOptimizationsPermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

    public static final Creator<RequestIgnoreBatteryOptimizationsPermission> CREATOR = new Creator<RequestIgnoreBatteryOptimizationsPermission>() {

        @Override
        public RequestIgnoreBatteryOptimizationsPermission createFromParcel(Parcel source) {
            return new RequestIgnoreBatteryOptimizationsPermission(source);
        }

        @Override
        public RequestIgnoreBatteryOptimizationsPermission[] newArray(int size) {
            return new RequestIgnoreBatteryOptimizationsPermission[size];
        }
    };

    public RequestIgnoreBatteryOptimizationsPermission() {
        // default implementation ignored
    }

    private RequestIgnoreBatteryOptimizationsPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @SuppressLint("BatteryLife")
    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        // 因为在 Android 10 的时候，这个特殊权限弹出的页面小米还是用谷歌原生的
        // 然而在 Android 11 之后的，这个权限页面被小米改成了自己定制化的页面
        if (PermissionVersion.isAndroid11() && (PhoneRomUtils.isHyperOs() || PhoneRomUtils.isMiui())) {
            return PermissionPageType.OPAQUE_ACTIVITY;
        }
        // 请求忽略电池优化选项权限在 Android 15 及以上版本的 OPPO 系统上面是一个不透明的 Activity 页面
        if (PhoneRomUtils.isColorOs() && PermissionVersion.isAndroid15()) {
            return PermissionPageType.OPAQUE_ACTIVITY;
        }
        if (PermissionVersion.isAndroid6() && !isGrantedPermission(context)) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(getPackageNameUri(context));
            if (PermissionUtils.areActivityIntent(context, intent)) {
                return PermissionPageType.TRANSPARENT_ACTIVITY;
            }
        }
        return PermissionPageType.OPAQUE_ACTIVITY;
    }

    @Override
    public int getFromAndroidVersion() {
        return PermissionVersion.ANDROID_6;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid6()) {
            return true;
        }
        PowerManager powerManager = context.getSystemService(PowerManager.class);
        // 虽然这个 SystemService 永远不为空，但是不怕一万，就怕万一，开展防御性编程
        if (powerManager == null) {
            return false;
        }
        return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    @SuppressLint("BatteryLife")
    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(7);

        Intent requestIgnoreBatteryOptimizationsIntent = null;
        if (PermissionVersion.isAndroid6()) {
            requestIgnoreBatteryOptimizationsIntent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            requestIgnoreBatteryOptimizationsIntent.setData(getPackageNameUri(context));
            // 经过测试，如果是已经授权的情况下，是不能再跳转到这个 Intent 的，否则就会导致存在这个 Intent，也可以跳转过去，
            // 但是这个权限设置页就会立马 finish，就会导致代码实际跳转了但是用户没有感觉到有跳转权限设置页的问题
            // 经过测试，发现有澎湃就算授权了也可以跳转过去，但 miui 就不行，Android 原生也不行，所以这里要排除一下澎湃
            if (isGrantedPermission(context, skipRequest) && !PhoneRomUtils.isHyperOs()) {
                requestIgnoreBatteryOptimizationsIntent = null;
            }
        }

        Intent advancedPowerUsageDetailIntent = null;
        if (PermissionVersion.isAndroid12()) {
            // 应用的电池使用情况详情页：Settings.ACTION_VIEW_ADVANCED_POWER_USAGE_DETAIL
            // 虽然 ACTION_VIEW_ADVANCED_POWER_USAGE_DETAIL 是 Android 10 的源码才出现的
            // 但是经过测试，在 Android 10 上面是无法跳转的，只有到了 Android 12 才能跳转
            advancedPowerUsageDetailIntent = new Intent("android.settings.VIEW_ADVANCED_POWER_USAGE_DETAIL");
            advancedPowerUsageDetailIntent.setData(getPackageNameUri(context));
        }

        Intent ignoreBatteryOptimizationSettingsIntent = null;
        if (PermissionVersion.isAndroid6()) {
            ignoreBatteryOptimizationSettingsIntent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        }

        // 因为在 Android 10 的时候，这个特殊权限弹出的页面小米还是用谷歌原生的
        // 然而在 Android 11 之后的，这个权限页面被小米改成了自己定制化的页面
        if (skipRequest && !(PermissionVersion.isAndroid11() && (PhoneRomUtils.isHyperOs() || PhoneRomUtils.isMiui()))) {
            if (advancedPowerUsageDetailIntent != null) {
                intentList.add(advancedPowerUsageDetailIntent);
            }
            if (ignoreBatteryOptimizationSettingsIntent != null) {
                intentList.add(ignoreBatteryOptimizationSettingsIntent);
            }
            if (requestIgnoreBatteryOptimizationsIntent != null) {
                intentList.add(requestIgnoreBatteryOptimizationsIntent);
            }
        } else {
            if (requestIgnoreBatteryOptimizationsIntent != null) {
                intentList.add(requestIgnoreBatteryOptimizationsIntent);
            }
            if (advancedPowerUsageDetailIntent != null) {
                intentList.add(advancedPowerUsageDetailIntent);
            }
            if (ignoreBatteryOptimizationSettingsIntent != null) {
                intentList.add(ignoreBatteryOptimizationSettingsIntent);
            }
        }

        Intent intent;
        // 经过测试，得出结论，miui 和澎湃支持在应用详情页设置该权限：
        // 1. miui 应用详情页 -> 省电策略
        // 2. Hyper 应用详情页 -> 电量消耗
        if (PhoneRomUtils.isMiui() || PhoneRomUtils.isHyperOs()) {
            intent = getApplicationDetailsSettingIntent(context);
            intentList.add(intent);

            intent = getManageApplicationSettingIntent();
            intentList.add(intent);

            intent = getApplicationSettingIntent();
            intentList.add(intent);
        }

        intent = getAndroidSettingIntent();
        intentList.add(intent);

        return intentList;
    }

    @Override
    public int getResultWaitTime(@NonNull Context context) {
        if (!isSupportRequestPermission(context)) {
            return 0;
        }

        // 小米手机默认等待时长
        final int xiaomiPhoneDefaultWaitTime = 1000;
        if (PhoneRomUtils.isHyperOs()) {
            // 1. 澎湃 Os 2.0.112.0，Android 15，小米 14，200 毫秒没有问题
            // 2. 澎湃 Os 2.0.8.0，Android 15，小米 12S Pro，200 毫秒没有问题
            // 3. 澎湃 Os 2.0.5.0，Android 15，红米 K60，200 毫秒没有问题
            // 4. 澎湃 Os 2.0.1.0，Android 15，红米 14R，200 毫秒没有问题
            // 5. 澎湃 Os 2.0.4.0，Android 14，小米平板 5，200 毫秒没有问题
            // 6. 澎湃 Os 2.0.1.0，Android 14，小米 12 Pro 天玑版，200 毫秒没有问题
            // 7. 澎湃 Os 1.0.7.0，Android 14，红米 Note 14，需要 1000 毫秒
            // 大致结论：澎湃 2.0 及以上的系统没有问题，澎湃 2.0 的 Android 版本有 Android 15 和 Android 14 的，
            //         Android 14 的澎湃只有 1.0 的，没有找到 Android 14 澎湃 2.0 的版本，
            //         所以这个问题应该是在澎湃 2.0 上面修复了，大概率澎湃 2.0  UI 大改版改动到了（看到 UI 有明显变化）
            //         结果测试人员发现了，开发人员不得不修，否则会影响自己的绩效，至此这个历史遗留 Bug 终于被发现并修复
            //         Android 15 的澎湃 2.0 版本 200 毫秒没有问题，但是 Android 14 的版本澎湃 1.0 还有有问题
            if (PermissionVersion.isAndroid15()) {
                return super.getResultWaitTime(context);
            }

            if (PermissionVersion.isAndroid14()) {
                int romBigVersionCode = PhoneRomUtils.getRomBigVersionCode();
                // 如果获取不到的大版本号又或者获取到的大版本号小于 2，就返回小米机型默认的等待时间
                if (romBigVersionCode < 2) {
                    return xiaomiPhoneDefaultWaitTime;
                }
                return super.getResultWaitTime(context);
            }

            return xiaomiPhoneDefaultWaitTime;
        }

        if (PhoneRomUtils.isMiui() && PermissionVersion.isAndroid11()) {
            // 经过测试，发现小米 Android 11 及以上的版本，申请这个权限需要 1000 毫秒才能判断到（测试了 800 毫秒还不行）
            // 因为在 Android 10 的时候，这个特殊权限弹出的页面小米还是用谷歌原生的
            // 然而在 Android 11 之后的，这个权限页面被小米改成了自己定制化的页面
            // 测试了原生的模拟器和 vivo 云测并发现没有这个问题，所以断定这个 Bug 就是小米特有的
            return xiaomiPhoneDefaultWaitTime;
        }

        return super.getResultWaitTime(context);
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 表示当前权限需要在 AndroidManifest.xml 文件中进行静态注册
        return true;
    }
}