package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.annotation.NonNull;
import com.hjq.device.compat.DeviceOs;
import com.hjq.permissions.manager.AlreadyRequestPermissionsManager;
import com.hjq.permissions.permission.PermissionChannel;
import com.hjq.permissions.permission.PermissionPageType;
import com.hjq.permissions.permission.base.BasePermission;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

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
    public PermissionChannel getPermissionChannel(@NonNull Context context) {
        return PermissionChannel.REQUEST_PERMISSIONS;
    }

    @NonNull
    @Override
    public PermissionPageType getPermissionPageType(@NonNull Context context) {
        return PermissionPageType.TRANSPARENT_ACTIVITY;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        // 判断权限是不是在旧系统上面运行（权限出现的版本 > 当前系统的版本）
        if (getFromAndroidVersion(context) > PermissionVersion.getCurrentVersion()) {
            return isGrantedPermissionByLowVersion(context, skipRequest);
        }
        return isGrantedPermissionByStandardVersion(context, skipRequest);
    }

    /**
     * 在标准版本的系统上面判断权限是否授予
     */
    protected boolean isGrantedPermissionByStandardVersion(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid6()) {
            return true;
        }
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
        if (getFromAndroidVersion(activity) > PermissionVersion.getCurrentVersion()) {
            return isDoNotAskAgainPermissionByLowVersion(activity);
        }
        return isDoNotAskAgainPermissionByStandardVersion(activity);
    }

    /**
     * 在标准版本的系统上面判断权限是否被用户勾选了《不再询问的选项》
     */
    protected boolean isDoNotAskAgainPermissionByStandardVersion(@NonNull Activity activity) {
        if (!PermissionVersion.isAndroid6()) {
            return false;
        }
        // 判断用户是否勾选了不再询问选项的前提条件
        // 1. 必须是本次运行状态已经申请过的权限
        // 2. 必须是未授权的权限
        // 通过以上两个条件就可以判断用户在拒绝的时候是否勾选了《不再询问》的选项，你可能会说为什么要写得那么麻烦？
        // 这是因为 Google 把 shouldShowRequestPermissionRationale 设计得很坑，用户在没有勾选《不再询问》的选项情况下，
        // shouldShowRequestPermissionRationale 也可能返回 false，这种情况就是你本次运行状态下没有申请过这个权限就调用它，
        // 这是 Google 压根不想让你知道用户是不是勾选了《不再询问》的选项，你只能在本次运行状态申请过这个权限才能知道，否则没有其他方法。
        // 目前框架针对这个问题进行了一些优化，主要针对在同时申请了前台权限和后台权限的场景，用户在明确拒绝了前台权限的条件下，
        // 与之对应的后台权限框架并没有继续去申请（因为申请了必然失败），就会导致 shouldShowRequestPermissionRationale 判断出现不准的问题。
        // 但是这样做仍然是有瑕疵的，就是应用本次运行状态如果没有申请过这个权限，直接用 shouldShowRequestPermissionRationale 判断是有问题的，
        // 只有在应用本次运行状态申请过一次这个权限才能用 shouldShowRequestPermissionRationale 准确判断用户是否勾选了《不再询问》的选项。
        // 你可能会说：为什么不永久存储 shouldShowRequestPermissionRationale 状态到磁盘上面？这样不是比你这种做法更加 very good？
        // 这个问题其实别人已经提过了，这里就不再重复解答了，传送地址：https://github.com/getActivity/XXPermissions/issues/154，
        // 目前这套处理方案是目前能想到的最佳解决方案了，如果你还有更好的做法，欢迎通过 issue 告诉我，我会持续跟进并优化这个问题。
        return AlreadyRequestPermissionsManager.isAlreadyRequestPermissions(this) &&
            !checkSelfPermission(activity, getPermissionName()) &&
            !shouldShowRequestPermissionRationale(activity, getPermissionName());
    }

    /**
     * 在低版本的系统上面判断权限是否被用户勾选了《不再询问的选项》
     */
    protected boolean isDoNotAskAgainPermissionByLowVersion(@NonNull Activity activity) {
        return false;
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(5);
        Intent intent;

        // 如果当前厂商系统是 HyperOS 或者 MIUI 的话，并且已经开启小米系统优化的前提下
        // 优先跳转到小米特有的应用权限设置页，这样做可以优化用户授权的体验
        // 需要注意的是，有人反馈 MIUI 国际版不能跳转到小米特有的权限设置页来设置危险权限
        // Github 地址：https://github.com/getActivity/XXPermissions/issues/398
        if (DeviceOs.isMiuiByChina() && DeviceOs.isMiuiOptimization()) {
            intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
            intentList.add(intent);
        } else if (DeviceOs.isHyperOsByChina() && DeviceOs.isHyperOsOptimization()) {
            String osVersionName = DeviceOs.getOsVersionName();
            // 这里需要过滤 2.0.0.0 ~ 2.0.5.0 范围的版本，因为我在小米云测上面测试了，这个范围的版本直接跳转到小米特有的应用权限设置页有问题
            // 实测在 2.0.6.0 这个问题才被解决，但是 HyperOS 1.0 无论是什么版本都没有这个问题，所以基本锁定这个问题是在 2.0.0.0 ~ 2.0.5.0 的版本
            // 这是因为小米在刚开始做 HyperOS 2.0 的时候，小米特有的权限设置页还是一个半成品，跳转后里面没有危险权限的选项，只有一个《其他权限》的选项
            // 并且其他权限的选项点进去后还只有可伶的几个权限：桌面快捷方式、通知类短信、锁屏显示、后台弹出界面、显示悬浮窗
            if (!osVersionName.matches("^2\\.0\\.[0-5]\\.\\d+$")) {
                intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                intentList.add(intent);
            }
        } else if (DeviceOs.isFlyme()) {
            intent = PermissionSettingPage.getMeiZuApplicationPermissionPageIntent(context);
            intentList.add(intent);
        }

        intent = getApplicationDetailsSettingIntent(context);
        intentList.add(intent);

        intent = getManageApplicationSettingIntent();
        intentList.add(intent);

        intent = getApplicationSettingIntent();
        intentList.add(intent);

        intent = getAndroidSettingIntent();
        intentList.add(intent);

        return intentList;
    }

    @Override
    protected boolean isRegisterPermissionByManifestFile() {
        // 危险权限默认需要在清单文件中注册，这样定义是为了避免外层在自定义特殊权限的时候，还要去重写此方法
        return true;
    }
}