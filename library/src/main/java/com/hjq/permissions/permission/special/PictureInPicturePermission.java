package com.hjq.permissions.permission.special;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.AndroidManifestInfo;
import com.hjq.permissions.AndroidManifestInfo.ActivityInfo;
import com.hjq.permissions.AndroidManifestInfo.PermissionInfo;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.permissions.permission.common.SpecialPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 画中画权限类
 */
public final class PictureInPicturePermission extends SpecialPermission {

    /** 当前权限名称，注意：该常量字段仅供框架内部使用，不提供给外部引用，如果需要获取权限名称的字符串，请直接通过 {@link PermissionConstants} 类获取 */
    public static final String PERMISSION_NAME = PermissionConstants.PICTURE_IN_PICTURE;

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
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_8;
    }

    @Override
    public boolean isGranted(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersionTools.isAndroid8()) {
            return true;
        }
        return checkOpNoThrow(context, AppOpsManager.OPSTR_PICTURE_IN_PICTURE);
    }

    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid8()) {
            return getApplicationDetailsIntent(context);
        }

        // android.provider.Settings.ACTION_PICTURE_IN_PICTURE_SETTINGS
        Intent intent = new Intent("android.settings.PICTURE_IN_PICTURE_SETTINGS");
        intent.setData(PermissionUtils.getPackageNameUri(context));

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
        List<ActivityInfo> activityInfoList = androidManifestInfo.activityInfoList;
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