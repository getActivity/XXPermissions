package com.hjq.permissions.permission.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.annotation.NonNull;
import com.hjq.permissions.permission.PermissionType;
import com.hjq.permissions.permission.base.BasePermission;
import com.hjq.permissions.tools.PermissionSettingPage;
import com.hjq.permissions.tools.PermissionVersion;
import com.hjq.permissions.tools.PhoneRomUtils;
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
    public PermissionType getPermissionType() {
        return PermissionType.DANGEROUS;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        // 判断权限是不是在旧系统上面运行（权限出现的版本 > 当前系统的版本）
        if (getFromAndroidVersion() > PermissionVersion.getCurrentVersion()) {
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
        if (getFromAndroidVersion() > PermissionVersion.getCurrentVersion()) {
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
        return !checkSelfPermission(activity, getPermissionName()) &&
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
    public List<Intent> getPermissionSettingIntents(@NonNull Context context) {
        List<Intent> intentList = new ArrayList<>(5);
        Intent intent;

        // 如果当前厂商系统是澎湃或者 miui 的话，并且已经开启小米系统优化的前提下
        // 优先跳转到小米特有的应用权限设置页，这样做可以优化用户授权的体验
        if (PhoneRomUtils.isMiui() && PhoneRomUtils.isXiaomiSystemOptimization()) {
            intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
            intentList.add(intent);
        } else if (PhoneRomUtils.isHyperOs() && PhoneRomUtils.isXiaomiSystemOptimization()) {
            String romVersionName = PhoneRomUtils.getRomVersionName();
            // 这里需要过滤 2.0.0.0 ~ 2.0.5.0 范围的版本，因为我在小米云测上面测试了，这个范围的版本直接跳转到小米特有的应用权限设置页有问题
            // 实测在 2.0.6.0 这个问题才被解决，但是澎湃 1.0 无论是什么版本都没有这个问题，所以基本锁定这个问题是在 2.0.0.0 ~ 2.0.5.0 的版本
            // 这是因为小米在刚开始做澎湃 2.0 的时候，小米特有的权限设置页还是一个半成品，跳转后里面没有危险权限的选项，只有一个《其他权限》的选项
            // 并且其他权限的选项点进去后还只有可伶的几个权限：桌面快捷方式、通知类短信、锁屏显示、后台弹出界面、显示悬浮窗
            if (romVersionName != null && !romVersionName.matches("^2\\.0\\.[012345]\\.\\d+$")) {
                intent = PermissionSettingPage.getXiaoMiApplicationPermissionPageIntent(context);
                intentList.add(intent);
            }
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