package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.hjq.permissions.AndroidManifestInfo;
import com.hjq.permissions.AndroidManifestInfo.PermissionInfo;
import com.hjq.permissions.AndroidManifestInfo.ServiceInfo;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 通知栏监听权限类
 */
public final class BindNotificationListenerServicePermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.BIND_NOTIFICATION_LISTENER_SERVICE;

    public static final Parcelable.Creator<BindNotificationListenerServicePermission> CREATOR = new Parcelable.Creator<BindNotificationListenerServicePermission>() {

        @Override
        public BindNotificationListenerServicePermission createFromParcel(Parcel source) {
            return new BindNotificationListenerServicePermission(source);
        }

        @Override
        public BindNotificationListenerServicePermission[] newArray(int size) {
            return new BindNotificationListenerServicePermission[size];
        }
    };

    /** Settings.Secure.ENABLED_NOTIFICATION_LISTENERS */
    private static final String SETTING_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";

    @Nullable
    private final String mServiceClassName;

    public BindNotificationListenerServicePermission() {
        this((String) null);
    }

    public BindNotificationListenerServicePermission(@Nullable Class<? extends Service> clazz) {
        this(clazz != null ? clazz.getName() : null);
    }

    public BindNotificationListenerServicePermission(@Nullable String serviceClassName) {
        mServiceClassName = serviceClassName;
    }

    private BindNotificationListenerServicePermission(Parcel in) {
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
        return AndroidVersionTools.ANDROID_4_3;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        // 经过实践得出，通知监听权限是在 Android 4.3 才出现的，所以前面的版本统一返回 true
        if (!AndroidVersionTools.isAndroid4_3()) {
            return true;
        }
        NotificationManager notificationManager;
        if (AndroidVersionTools.isAndroid6()) {
            notificationManager = context.getSystemService(NotificationManager.class);
        } else {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        String serviceClassName = PermissionUtils.isClassExist(mServiceClassName) ? mServiceClassName : null;
        // 虽然这个 SystemService 永远不为空，但是不怕一万，就怕万一，开展防御性编程
        if (AndroidVersionTools.isAndroid8_1() && notificationManager != null && serviceClassName != null) {
            return notificationManager.isNotificationListenerAccessGranted(new ComponentName(context, serviceClassName));
        }
        final String enabledNotificationListeners = Settings.Secure.getString(context.getContentResolver(), SETTING_ENABLED_NOTIFICATION_LISTENERS);
        if (TextUtils.isEmpty(enabledNotificationListeners)) {
            return false;
        }
        // com.hjq.permissions.demo/com.hjq.permissions.demo.NotificationMonitorService:com.huawei.health/com.huawei.bone.ui.setting.NotificationPushListener
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
        Intent intent = null;
        if (AndroidVersionTools.isAndroid11() && PermissionUtils.isClassExist(mServiceClassName)) {
            intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS);
            intent.putExtra(Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME, new ComponentName(context, mServiceClassName).flattenToString());
            if (!PermissionUtils.areActivityIntent(context, intent)) {
                intent = null;
            }
        }

        if (intent == null) {
            if (AndroidVersionTools.isAndroid5_1()) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                // android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
        }

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
        // 判断有没有 Service 类注册了 android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" 属性
        List<ServiceInfo> serviceInfoList = androidManifestInfo.serviceInfoList;
        for (int i = 0; i < serviceInfoList.size(); i++) {
            String permission = serviceInfoList.get(i).permission;
            if (permission == null) {
                continue;
            }
            if (PermissionUtils.equalsPermission(getPermissionName(), permission)) {
                // 发现有 Service 注册过，终止循环并返回，避免走到抛异常的情况
                return;
            }
        }

        /*
         没有找到有任何 Service 注册过 android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" 属性，
         请注册该属性给 NotificationListenerService 的子类到 AndroidManifest.xml 文件中，否则会导致无法申请该权限
         */
        throw new IllegalArgumentException("No Service was found to have registered the android:permission=\"" + getPermissionName() +
            "\" property, Please register this property to NotificationListenerService subclass by AndroidManifest.xml file, "
            + "otherwise it will lead to can't apply for the permission");
    }

    @Nullable
    public String getServiceClassName() {
        return mServiceClassName;
    }
}