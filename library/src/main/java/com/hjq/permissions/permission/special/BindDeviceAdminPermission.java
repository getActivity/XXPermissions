package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.AndroidManifestInfo;
import com.hjq.permissions.AndroidManifestInfo.BroadcastReceiverInfo;
import com.hjq.permissions.AndroidManifestInfo.PermissionInfo;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/15
 *    desc   : 设备管理器权限
 */
public final class BindDeviceAdminPermission extends SpecialPermission {

    public static final Creator<BindDeviceAdminPermission> CREATOR = new Creator<BindDeviceAdminPermission>() {

        @Override
        public BindDeviceAdminPermission createFromParcel(Parcel source) {
            return new BindDeviceAdminPermission(source);
        }

        @Override
        public BindDeviceAdminPermission[] newArray(int size) {
            return new BindDeviceAdminPermission[size];
        }
    };

    /** 设备管理器的 BroadcastReceiver 类名 */
    @NonNull
    private final String mClazzName;

    /** 申请设备管理器权限的附加说明 */
    @Nullable
    private final String mExtraAddExplanation;

    public BindDeviceAdminPermission(@NonNull Class<? extends BroadcastReceiver> clazz, @Nullable String extraAddExplanation) {
        this(clazz.getName(), extraAddExplanation);
    }

    public BindDeviceAdminPermission(@NonNull String clazzName, @Nullable String extraAddExplanation) {
        mClazzName = clazzName;
        mExtraAddExplanation = extraAddExplanation;
    }

    private BindDeviceAdminPermission(Parcel in) {
        this(in.readString(), in.readString());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mClazzName);
        dest.writeString(mExtraAddExplanation);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PermissionNames.BIND_DEVICE_ADMIN;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_2_2;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return policyManager.isAdminActive(new ComponentName(context, mClazzName));
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(context, mClazzName));
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, mExtraAddExplanation);

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }
        return intent;
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionInfo> permissionInfoList,
                                            @Nullable PermissionInfo currentPermissionInfo) {
        // 该权限不需要在清单文件中静态注册，所以注释掉父类的调用
        // super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionInfoList, currentPermissionInfo);
        // 判断有没有 BroadcastReceiver 类注册了 android:permission="android.permission.BIND_DEVICE_ADMIN" 属性
        List<BroadcastReceiverInfo> broadcastReceiverInfoList = androidManifestInfo.broadcastReceiverInfoList;
        for (int i = 0; i < broadcastReceiverInfoList.size(); i++) {
            String permission = broadcastReceiverInfoList.get(i).permission;
            if (permission == null) {
                continue;
            }
            if (PermissionUtils.equalsPermission(getPermissionName(), permission)) {
                // 发现有 BroadcastReceiver 注册过，终止循环并返回，避免走到抛异常的情况
                return;
            }
        }

        /*
         没有找到有任何 BroadcastReceiver 注册过 android:permission="android.permission.BIND_DEVICE_ADMIN" 属性，
         请注册该属性给 DeviceAdminReceiver 的子类到 AndroidManifest.xml 文件中，否则会导致无法申请该权限
         */
        throw new IllegalArgumentException("No BroadcastReceiver was found to have registered the android:permission=\"" + getPermissionName() +
            "\" property, Please register this property to DeviceAdminReceiver subclass by AndroidManifest.xml file, "
            + "otherwise it will lead to can't apply for the permission");
    }

    @NonNull
    public String getClazzName() {
        return mClazzName;
    }

    @Nullable
    public String getExtraAddExplanation() {
        return mExtraAddExplanation;
    }
}