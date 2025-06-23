package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.manifest.node.ServiceManifestInfo;
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
 *    desc   : 无障碍服务权限
 */
public final class BindAccessibilityServicePermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.BIND_ACCESSIBILITY_SERVICE;

    public static final Creator<BindAccessibilityServicePermission> CREATOR = new Creator<BindAccessibilityServicePermission>() {

        @Override
        public BindAccessibilityServicePermission createFromParcel(Parcel source) {
            return new BindAccessibilityServicePermission(source);
        }

        @Override
        public BindAccessibilityServicePermission[] newArray(int size) {
            return new BindAccessibilityServicePermission[size];
        }
    };

    /** 设备管理器的 Service 类名 */
    @NonNull
    private final String mServiceClassName;

    public BindAccessibilityServicePermission(@NonNull Class<? extends Service> serviceClazz) {
        this(serviceClazz.getName());
    }

    public BindAccessibilityServicePermission(@NonNull String serviceClassName) {
        mServiceClassName = serviceClassName;
    }

    private BindAccessibilityServicePermission(Parcel in) {
        this(in.readString());
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mServiceClassName);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersion.ANDROID_4_1;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        final String enabledNotificationListeners = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (TextUtils.isEmpty(enabledNotificationListeners)) {
            return false;
        }

        String serviceClassName = PermissionUtils.isClassExist(mServiceClassName) ? mServiceClassName : null;
        // hello.litiaotiao.app/hello.litiaotiao.app.LttService:com.hjq.permissions.demo/com.hjq.permissions.demo.DemoAccessibilityService
        final String[] allComponentNameArray = enabledNotificationListeners.split(":");
        for (String component : allComponentNameArray) {
            ComponentName componentName = ComponentName.unflattenFromString(component);
            if (componentName == null) {
                continue;
            }
            if (serviceClassName != null) {
                // 精准匹配
                if (serviceClassName.equals(componentName.getClassName())) {
                    return true;
                }
            } else {
                // 模糊匹配
                if (context.getPackageName().equals(componentName.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @NonNull
    @Override
    public Intent getPermissionSettingIntent(@NonNull Context context) {
        // 这里解释一下为什么只能跳转到无障碍设置页？而不是当前应用的无障碍设置页？
        // 这是因为系统没有开放这个途径给应用层去实现，所以实现不了，你可能会说，这不是瞎扯？
        // 我明明看到 Settings 类中有一个意图叫 ACTION_ACCESSIBILITY_DETAILS_SETTINGS，怎么就实现不了？
        // 能看到不代表能用，OK？这个 Action 我已经帮大家试过了，普通应用没有办法跳转的，放弃吧
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getAndroidSettingAppIntent();
        }
        return intent;
    }

    @Override
    public void checkCompliance(@NonNull Activity activity, @NonNull List<IPermission> requestPermissions, @Nullable AndroidManifestInfo androidManifestInfo) {
        super.checkCompliance(activity, requestPermissions, androidManifestInfo);
        if (!PermissionUtils.isClassExist(mServiceClassName)) {
            throw new IllegalArgumentException("The passed-in ServiceClass is an invalid class");
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
        // 判断有没有 Service 类注册了 android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE" 属性
        List<ServiceManifestInfo> serviceManifestInfoList = androidManifestInfo.serviceManifestInfoList;
        for (int i = 0; i < serviceManifestInfoList.size(); i++) {
            String permission = serviceManifestInfoList.get(i).permission;
            if (permission == null) {
                continue;
            }
            if (PermissionUtils.equalsPermission(getPermissionName(), permission)) {
                // 发现有 Service 注册过，终止循环并返回，避免走到抛异常的情况
                return;
            }
        }

        /*
         没有找到有任何 Service 注册过 android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE" 属性，
         请注册该属性给 AccessibilityService 的子类到 AndroidManifest.xml 文件中，否则会导致无法申请该权限
         */
        throw new IllegalArgumentException("No Service was found to have registered the android:permission=\"" + getPermissionName() +
            "\" property, Please register this property to AccessibilityService subclass by AndroidManifest.xml file, "
            + "otherwise it will lead to can't apply for the permission");
    }

    @NonNull
    public String getServiceClassName() {
        return mServiceClassName;
    }
}