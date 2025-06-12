package com.hjq.permissions.permission.special;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionUtils;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.common.SpecialPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 全屏通知权限类
 */
public final class UseFullScreenIntentPermission extends SpecialPermission {

    public static final Parcelable.Creator<UseFullScreenIntentPermission> CREATOR = new Parcelable.Creator<UseFullScreenIntentPermission>() {

        @Override
        public UseFullScreenIntentPermission createFromParcel(Parcel source) {
            return new UseFullScreenIntentPermission(source);
        }

        @Override
        public UseFullScreenIntentPermission[] newArray(int size) {
            return new UseFullScreenIntentPermission[size];
        }
    };

    public UseFullScreenIntentPermission() {
        // default implementation ignored
    }

    private UseFullScreenIntentPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.USE_FULL_SCREEN_INTENT;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_14;
    }

    @Override
    public boolean isGranted(@NonNull Context context, boolean skipRequest) {
        if (!AndroidVersionTools.isAndroid14()) {
            return true;
        }
        return context.getSystemService(NotificationManager.class).canUseFullScreenIntent();
    }

    @NonNull
    @Override
    public Intent getSettingIntent(@NonNull Context context) {
        if (!AndroidVersionTools.isAndroid14()) {
            return getApplicationDetailsIntent(context);
        }

        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT);
        intent.setData(PermissionUtils.getPackageNameUri(context));

        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = getApplicationDetailsIntent(context);
        }

        return intent;
    }
}