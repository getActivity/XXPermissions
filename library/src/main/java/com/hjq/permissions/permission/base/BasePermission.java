package com.hjq.permissions.permission.base;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hjq.permissions.AndroidVersionTools;
import com.hjq.permissions.PermissionIntentManager;
import com.hjq.permissions.PermissionUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/06/11
 *    desc   : 权限基类
 */
public abstract class BasePermission implements IPermission {

    protected BasePermission() {
        // default implementation ignored
    }

    protected BasePermission(Parcel in) {
        // default implementation ignored
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {}

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // 重写 equals 方法是为了 List 和 Map 集合有能力辨别不同的权限对象是不是来自同一个权限
        if (obj instanceof IPermission) {
            // 如果这两个权限的名称一样，那么就认为它们是同一个权限
            return PermissionUtils.equalsPermission(getName(), ((IPermission) obj).getName());
        }
        return false;
    }

    @NonNull
    public Intent getApplicationDetailsIntent(@NonNull Context context) {
        return PermissionIntentManager.getApplicationDetailsIntent(context, this);
    }

    /**
     * 通过 AppOpsManager 判断某个权限是否授予
     *
     * @param opName               需要传入 {@link AppOpsManager} 类中的以 OPSTR 开头的字段
     */
    public static boolean checkOpNoThrow(Context context, String opName) {
        if (!AndroidVersionTools.isAndroid4_4()) {
            return true;
        }
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode;
        if (AndroidVersionTools.isAndroid10()) {
            mode = appOps.unsafeCheckOpNoThrow(opName, context.getApplicationInfo().uid, context.getPackageName());
        } else {
            mode = appOps.checkOpNoThrow(opName, context.getApplicationInfo().uid, context.getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * 判断 AppOpsManager 某个权限是否授予
     *
     * @param opFieldName               要反射 {@link AppOpsManager} 类中的字段名称
     * @param opDefaultValue            当反射获取不到对应字段的值时，该值作为替补
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean checkOpNoThrow(Context context, String opFieldName, int opDefaultValue) {
        if (!AndroidVersionTools.isAndroid4_4()) {
            return true;
        }
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
            int opValue;
            try {
                Field opValueField = appOpsClass.getDeclaredField(opFieldName);
                opValue = (int) opValueField.get(Integer.class);
            } catch (NoSuchFieldException e) {
                opValue = opDefaultValue;
            }
            Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
            return ((int) checkOpNoThrowMethod.invoke(appOps, opValue, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (ClassNotFoundException | NoSuchMethodException |
                 InvocationTargetException | IllegalAccessException | RuntimeException e) {
            return true;
        }
    }
}