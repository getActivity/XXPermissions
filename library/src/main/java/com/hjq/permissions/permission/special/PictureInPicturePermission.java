package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.manifest.node.ActivityManifestInfo;
import com.hjq.permissions.manifest.node.PermissionManifestInfo;
import com.hjq.permissions.permission.PermissionNames;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import com.hjq.permissions.tools.PermissionVersion;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 画中画权限类
 */
public final class PictureInPicturePermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionNames} 类获取 */
    public static final String PERMISSION_NAME = PermissionNames.PICTURE_IN_PICTURE;

    public static final Parcelable.Creator<PictureInPicturePermission> CREATOR = new Parcelable.Creator<PictureInPicturePermission>() {

        @Override
        public PictureInPicturePermission createFromParcel(Parcel source) {
            return new PictureInPicturePermission(source);
        }

        @Override
        public PictureInPicturePermission[] newArray(int size) {
            return new PictureInPicturePermission[size];
        }
    };

    public PictureInPicturePermission() {
        // default implementation ignored
    }

    private PictureInPicturePermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getPermissionName() {
        return PERMISSION_NAME;
    }

    @Override
    public int getFromAndroidVersion(@NonNull Context context) {
        return PermissionVersion.ANDROID_8;
    }

    @Override
    public boolean isGrantedPermission(@NonNull Context context, boolean skipRequest) {
        if (!PermissionVersion.isAndroid8()) {
            return true;
        }
        return checkOpPermission(context, AppOpsManager.OPSTR_PICTURE_IN_PICTURE, true);
    }

    @NonNull
    @Override
    public List<Intent> getPermissionSettingIntents(@NonNull Context context, boolean skipRequest) {
        List<Intent> intentList = new ArrayList<>(6);
        Intent intent;

        if (PermissionVersion.isAndroid8()) {
            // android.provider.Settings.ACTION_PICTURE_IN_PICTURE_SETTINGS
            String action = "android.settings.PICTURE_IN_PICTURE_SETTINGS";

            intent = new Intent(action);
            intent.setData(getPackageNameUri(context));
            intentList.add(intent);

            // 如果是因为加包名的数据后导致不能跳转，就把包名的数据移除掉
            intent = new Intent(action);
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
    protected void checkSelfByManifestFile(@NonNull Activity activity,
                                           @NonNull List<IPermission> requestList,
                                           @NonNull AndroidManifestInfo manifestInfo,
                                           @NonNull List<PermissionManifestInfo> permissionInfoList,
                                           @Nullable PermissionManifestInfo currentPermissionInfo) {
        super.checkSelfByManifestFile(activity, requestList, manifestInfo, permissionInfoList, currentPermissionInfo);
        List<ActivityManifestInfo> activityInfoList = manifestInfo.activityInfoList;
        for (int i = 0; i < activityInfoList.size(); i++) {
            boolean supportsPictureInPicture = activityInfoList.get(i).supportsPictureInPicture;
            if (supportsPictureInPicture) {
                // 终止循环并返回
                return;
            }
        }

         /*
         没有找到有任何 Service 注册过 android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" 属性，
         请注册该属性给 NotificationListenerService 的子类到 AndroidManifest.xml 文件中，否则会导致无法申请该权限
         */
        throw new IllegalArgumentException("No Activity was found to have registered the android:supportsPictureInPicture=\"true\" property, " +
            "Please register this property to " + activity.getClass().getName() + " class by AndroidManifest.xml file, "
            + "otherwise it will lead to can't apply for the permission");
    }
}