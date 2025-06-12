package com.hjq.permissions.permission.special;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.PhoneRomUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 请求忽略电池优化选项权限
 */
public final class RequestIgnoreBatteryOptimizationsPermission extends SpecialPermission {

    public static final Parcelable.Creator<RequestIgnoreBatteryOptimizationsPermission> CREATOR = new Parcelable.Creator<RequestIgnoreBatteryOptimizationsPermission>() {

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
    public String getName() {
        return PermissionConstants.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_6;
    }

    @Override
    public boolean isGranted(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersionTools.isAndroid6()) {
            return true;
        }
        return context.getSystemService(PowerManager.class).isIgnoringBatteryOptimizations(context.getPackageName());
    }

    @SuppressLint("BatteryLife")
    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid6()) {
            return getApplicationDetailsIntent(context);
        }
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(PermissionUtils.getPackageNameUri(context));

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        }

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    @Override
    public int getResultWaitTime() {
        if (PhoneRomUtils.isMiui() && AndroidVersionTools.isAndroid11() &&
            AndroidVersionTools.getCurrentAndroidVersionCode() >= getFromAndroidVersion()) {
            // 经过测试，发现小米 Android 11 及以上的版本，申请这个权限需要 1000 毫秒才能判断到（测试了 800 毫秒还不行）
            // 因为在 Android 10 的时候，这个特殊权限弹出的页面小米还是用谷歌原生的
            // 然而在 Android 11 之后的，这个权限页面被小米改成了自己定制化的页面
            // 测试了原生的模拟器和 vivo 云测并发现没有这个问题，所以断定这个 Bug 就是小米特有的
            return 1000;
        }
        return super.getResultWaitTime();
    }
}