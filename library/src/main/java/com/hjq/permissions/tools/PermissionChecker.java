package com.hjq.permissions.tools;

import android.app.Activity;
import android.app.Fragment;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hjq.permissions.manifest.AndroidManifestInfo;
import com.hjq.permissions.permission.base.IPermission;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2021/02/09
 *    desc   : 权限错误检测类
 */
public final class PermissionChecker {

    /**
     * 检查 {@link android.app.Activity} 对象的状态是否正常
     */
    public static void checkActivityStatus(@Nullable Activity activity) {
        // 检查当前 Activity 状态是否是正常的，如果不是则不请求权限
        if (activity == null) {
            // Context 的实例必须是 Activity 对象
            throw new IllegalArgumentException("The instance of the context must be an activity object");
        }

        if (activity.isFinishing()) {
            // 这个 Activity 对象当前不能是关闭状态，这种情况常出现在执行异步请求后申请权限
            // 请自行在外层判断 Activity 状态是否正常之后再进入权限申请
            throw new IllegalStateException("The activity has been finishing, " +
                "please manually determine the status of the activity");
        }

        if (activity.isDestroyed()) {
            // 这个 Activity 对象当前不能是销毁状态，这种情况常出现在执行异步请求后申请权限
            // 请自行在外层判断 Activity 状态是否正常之后再进入权限申请
            throw new IllegalStateException("The activity has been destroyed, " +
                "please manually determine the status of the activity");
        }
    }

    /**
     * 检查 {@link androidx.fragment.app.Fragment} 对象的状态是否正常
     */
    public static void checkAndroidXFragmentStatus(@NonNull androidx.fragment.app.Fragment xFragment) {
        if (!xFragment.isAdded()) {
            // 这个 Fragment 没有添加绑定
            throw new IllegalStateException("This androidX fragment has no binding added, " +
                "please manually determine the status of the androidX fragment");
        }

        if (xFragment.isRemoving()) {
            // 这个 Fragment 已经被移除
            throw new IllegalStateException("This androidX fragment has been removed, " +
                "please manually determine the status of the androidX fragment");
        }
    }

    /**
     * 检查 {@link android.app.Fragment} 对象的状态是否正常
     */
    @SuppressWarnings("deprecation")
    public static void checkAndroidFragmentStatus(@NonNull Fragment fragment) {
        if (!fragment.isAdded()) {
            // 这个 Fragment 没有添加绑定
            throw new IllegalStateException("This android fragment has no binding added, " +
                "please manually determine the status of the android fragment");
        }

        if (fragment.isRemoving()) {
            // 这个 Fragment 已经被移除
            throw new IllegalStateException("This android fragment has been removed, " +
                "please manually determine the status of the android fragment");
        }
    }

    /**
     * 检查传入的权限是否符合要求
     */
    public static void checkPermissionList(@NonNull Activity activity, @Nullable List<IPermission> requestList, @Nullable AndroidManifestInfo manifestInfo) {
        if (requestList == null || requestList.isEmpty()) {
            // 不传任何权限，就想动态申请权限？
            throw new IllegalArgumentException("The requested permission cannot be empty");
        }

        for (IPermission permission : requestList) {
            // 检查权限序列化实现是否有问题
            checkPermissionParcelable(permission);
            // 让权限自己检查一下自己
            permission.checkCompliance(activity, requestList, manifestInfo);
        }
    }

    /**
     * 检查权限序列化实现是否没问题
     */
    public static void checkPermissionParcelable(@NonNull IPermission permission) {
        Class<? extends IPermission> clazz = permission.getClass();
        String className = clazz.getName();

        // 获取 CREATOR 字段
        Field creatorField = null;
        try {
            creatorField = permission.getClass().getDeclaredField("CREATOR");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (creatorField == null) {
            // 这个权限类没有定义 CREATOR 字段
            throw new IllegalArgumentException("This permission class does not define the CREATOR field");
        }

        // 获取 CREATOR 对象
        Object creatorObject;
        try {
            // 静态字段使用 null 作为实例
            creatorObject = creatorField.get(null);
        } catch (Exception e) {
            // 访问权限类中的 CREATOR 字段异常，请用 public static final 来修饰 CREATOR 字段
            throw new IllegalArgumentException("The CREATOR field in the " + className +
                " has an access exception. Please modify CREATOR field with \"public static final\"");
        }

        if (!(creatorObject instanceof Parcelable.Creator)) {
            // 这个权限类中的 CREATOR 字段不是 android.os.Parcelable.Creator 类型
            throw new IllegalArgumentException("The CREATOR field in this " + className +
                " is not of type " + Parcelable.Creator.class.getName());
        }

        // 获取字段的泛型类型
        Type genericType = creatorField.getGenericType();

        // 检查是否为参数化类型
        if (!(genericType instanceof ParameterizedType)) {
            // 这个权限类中的 CREATOR 字段定义的泛型为空
            throw new IllegalArgumentException("The generic type defined for the CREATOR field in this " + className + " is empty");
        }

        // 获取泛型参数
        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();

        // 检查是否只有一个泛型参数
        if (typeArguments.length != 1) {
            // 这个权限类中的 CREATOR 字段定义的泛型数量只能有一个
            throw new IllegalArgumentException("The number of generics defined in the CREATOR field of this " + className + " can only be one");
        }

        // 获取泛型参数类型
        Type typeArgument = typeArguments[0];

        // 检查泛型参数是否为当前类
        if (!(typeArgument instanceof Class && clazz.isAssignableFrom((Class<?>) typeArgument))) {
            // 这个权限类中的 CREATOR 字段定义的泛型类型错误
            throw new IllegalArgumentException("The generic type defined in the CREATOR field of this " + className + " is incorrect");
        }

        // 直接调用 newArray 方法创建数组
        Parcelable.Creator<?> parcelableCreator = (Parcelable.Creator<?>) creatorObject;
        Object[] array = parcelableCreator.newArray(0);
        if (array == null) {
            // 这个权限类中的 CREATOR 字段的 newArray 方法返回了空，此方法返回不能为空
            throw new IllegalArgumentException("The newArray method of the CREATOR field in this " + className +
                " returns an empty value. This method cannot return an empty value");
        }
    }
}