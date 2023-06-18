package com.hjq.permissions;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.annotation.NonNull;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2023/03/12
 *    desc   : 通知栏权限兼容类
 */
final class NotificationPermissionCompat {

    private static final String OP_POST_NOTIFICATION_FIELD_NAME = "OP_POST_NOTIFICATION";
    private static final int OP_POST_NOTIFICATION_DEFAULT_VALUE = 11;

    static boolean isGrantedPermission(@NonNull Context context) {
        if (AndroidVersion.isAndroid7()) {
            return context.getSystemService(NotificationManager.class).areNotificationsEnabled();
        }

        if (AndroidVersion.isAndroid4_4()) {
            return PermissionUtils.checkOpNoThrow(context, OP_POST_NOTIFICATION_FIELD_NAME, OP_POST_NOTIFICATION_DEFAULT_VALUE);
        }
        return true;
    }

    static Intent getPermissionIntent(@NonNull Context context) {
        Intent intent = null;
        if (AndroidVersion.isAndroid8()) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            //intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
        } else if (AndroidVersion.isAndroid5()) {
            intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        }
        if (!PermissionUtils.areActivityIntent(context, intent)) {
            intent = PermissionIntentManager.getApplicationDetailsIntent(context);
        }
        return intent;
    }
}