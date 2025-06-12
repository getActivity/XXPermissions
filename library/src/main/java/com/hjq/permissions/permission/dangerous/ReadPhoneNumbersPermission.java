package com.hjq.permissions.permission.dangerous;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.permission.PermissionConstants;
import com.hjq.permissions.permission.PermissionManifest;
import com.hjq.permissions.permission.common.DangerousPermission;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 读取手机号码权限类
 */
public final class ReadPhoneNumbersPermission extends DangerousPermission {

    public static final Parcelable.Creator<ReadPhoneNumbersPermission> CREATOR = new Parcelable.Creator<ReadPhoneNumbersPermission>() {

        @Override
        public ReadPhoneNumbersPermission createFromParcel(Parcel source) {
            return new ReadPhoneNumbersPermission(source);
        }

        @Override
        public ReadPhoneNumbersPermission[] newArray(int size) {
            return new ReadPhoneNumbersPermission[size];
        }
    };

    public ReadPhoneNumbersPermission() {
        // default implementation ignored
    }

    private ReadPhoneNumbersPermission(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String getName() {
        return PermissionConstants.READ_PHONE_NUMBERS;
    }

    @Override
    public int getFromAndroidVersion() {
        return AndroidVersionTools.ANDROID_8;
    }

    @Override
    protected boolean isGrantedByLowVersion(@NonNull Context context, boolean skipRequest) {
        return PermissionManifest.READ_PHONE_STATE.isGranted(context, skipRequest);
    }

    @Override
    protected boolean isDoNotAskAgainByLowVersion(@NonNull Activity activity) {
        return PermissionManifest.READ_PHONE_STATE.isDoNotAskAgain(activity);
    }
}