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
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.BroadcastReceiverManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.AndroidVersion;
import com.hjq.permissions.tools.PermissionUtils;
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
    private final String mBroadcastReceiverClassName;

    /** 申请设备管理器权限的附加说明 */
    @Nullable
    private final String mExtraAddExplanation;

    public BindDeviceAdminPermission(@NonNull Class<? extends BroadcastReceiver> broadcastReceiverClass, @Nullable String extraAddExplanation) {
        this(broadcastReceiverClass.getName(), extraAddExplanation);
    }

    public BindDeviceAdminPermission(@NonNull String broadcastReceiverClassName, @Nullable String extraAddExplanation) {
        mBroadcastReceiverClassName = broadcastReceiverClassName;
        mExtraAddExplanation = extraAddExplanation;
    }

    private BindDeviceAdminPermission(Parcel in) {
        this(in.readString(), in.readString());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mBroadcastReceiverClassName);
        dest.writeString(mExtraAddExplanation);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PermissionNames.BIND_DEVICE_ADMIN;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersion.ANDROID_2_2;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        DevicePolicyManager devicePolicyManager;
        if (AndroidVersion.isAndroid6()) {
            devicePolicyManager = context.getSystemService(DevicePolicyManager.class);
        } else {
            devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        }
        // 虽然这个 SystemService 永远不为空，但是不怕一万，就怕万一，开展防御性编程
        if (devicePolicyManager == null) {
            return false;
        }
        return devicePolicyManager.isAdminActive(new ComponentName(context, mBroadcastReceiverClassName));
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(context, mBroadcastReceiverClassName));
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, mExtraAddExplanation);

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getAndroidSettingAppIntent();
        }
        return intent;
    }

    @Override
    public void checkCompliance(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions, @Nullable AndroidManifestInfo androidManifestInfo) {
        super.checkCompliance(activity, requestPermissions, androidManifestInfo);
        if (!PermissionUtils.isClassExist(mBroadcastReceiverClassName)) {
            throw new IllegalArgumentException("The passed-in BroadcastReceiverClass is an invalid class");
        }
    }

    @Override
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                            @NonNull List<IPermission> requestPermissions,
                                            @NonNull AndroidManifestInfo androidManifestInfo,
                                            @NonNull List<PermissionManifestInfo> permissionManifestInfoList,
                                            @Nullable PermissionManifestInfo currentPermissionManifestInfo) {
        super.checkSelfByManifestFile(activity, requestPermissions, androidManifestInfo, permissionManifestInfoList,
            currentPermissionManifestInfo);
        // 判断有没有 BroadcastReceiver 类注册了 android:permission="android.permission.BIND_DEVICE_ADMIN" 属性
        List<BroadcastReceiverManifestInfo> broadcastReceiverManifestInfoList = androidManifestInfo.broadcastReceiverManifestInfoList;
        for (int i = 0; i < broadcastReceiverManifestInfoList.size(); i++) {
            String permission = broadcastReceiverManifestInfoList.get(i).permission;
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
    public String getBroadcastReceiverClassName() {
        return mBroadcastReceiverClassName;
    }

    @Nullable
    public String getExtraAddExplanation() {
        return mExtraAddExplanation;
    }
}