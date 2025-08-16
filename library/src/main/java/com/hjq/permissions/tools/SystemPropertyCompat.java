package com.hjq.permissions.tools;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2025/08/13
 *    desc   : 系统属性兼容类
 */
public final class SystemPropertyCompat {

    private SystemPropertyCompat() {
        // 私有化构造方法，禁止外部实例化
    }

    /**
     * 获取指定的系统属性值（结合了多种方式）
     */
    @NonNull
    public static String getSystemPropertyValue(@Nullable String propertyName) {
        if (propertyName == null || propertyName.isEmpty()) {
            return "";
        }

        String propertyValue = null;
        try {
            propertyValue = getSystemPropertyByReflect(propertyName);
        } catch (Exception ignored) {
            // default implementation ignored
        }

        if (propertyValue != null && !propertyValue.isEmpty()) {
            return propertyValue;
        }

        try {
            propertyValue = getSystemPropertyByShell(propertyName);
        } catch (IOException ignored) {
            // default implementation ignored
        }

        if (propertyValue != null && !propertyValue.isEmpty()) {
            return propertyValue;
        }

        try {
            propertyValue = getSystemPropertyByStream(propertyName);
        } catch (IOException ignored) {
            // default implementation ignored
        }

        if (propertyValue != null && !propertyValue.isEmpty()) {
            return propertyValue;
        }

        return "";
    }

    /**
     * 获取多个系统属性值
     */
    @NonNull
    public static String[] getSystemPropertyValue(@Nullable String[] propertyNames) {
        if (propertyNames == null) {
            return new String[0];
        }

        String[] propertyValues = new String[propertyNames.length];

        for (int i = 0; i < propertyNames.length; i++) {
            propertyValues[i] = getSystemPropertyValue(propertyNames[i]);
        }
        return propertyValues;
    }

    /**
     * 获取多个系统属性中的任一一个值
     */
    @NonNull
    public static String getSystemPropertyAnyOneValue(@Nullable String[] propertyNames) {
        if (propertyNames == null) {
            return "";
        }

        for (String propertyName : propertyNames) {
            String propertyValue = getSystemPropertyValue(propertyName);
            if (!propertyValue.isEmpty()) {
                return propertyValue;
            }
        }
        return "";
    }

    /**
     * 判断某个系统属性是否存在
     */
    public static boolean isSystemPropertyExist(@Nullable String propertyName) {
        return !TextUtils.isEmpty(getSystemPropertyValue(propertyName));
    }

    /**
     * 判断多个系统属性是否有任一一个存在
     */
    public static boolean isSystemPropertyAnyOneExist(@Nullable String[] propertyNames) {
        if (propertyNames == null) {
            return false;
        }
        for (String propertyName : propertyNames) {
            if (isSystemPropertyExist(propertyName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取系统属性值（通过反射系统类）
     */
    @SuppressLint("PrivateApi")
    private static String getSystemPropertyByReflect(@NonNull String propertyName) throws ClassNotFoundException, InvocationTargetException,
                                                                        NoSuchMethodException, IllegalAccessException  {
        Class<?> clz = Class.forName("android.os.SystemProperties");
        Method getMethod = clz.getMethod("get", String.class, String.class);
        return (String) getMethod.invoke(clz, propertyName, "");
    }

    /**
     * 获取系统属性值（通过 shell 命令）
     */
    private static String getSystemPropertyByShell(@NonNull String propertyName) throws IOException {
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propertyName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            String firstLine = input.readLine();
            if (firstLine != null) {
                return firstLine;
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                    // default implementation ignored
                }
            }
        }
        return null;
    }

    /**
     * 获取系统属性值（通过读取系统文件）
     */
    private static String getSystemPropertyByStream(@NonNull String key) throws IOException {
        FileInputStream inputStream = null;
        try {
            Properties prop = new Properties();
            File file = new File(Environment.getRootDirectory(), "build.prop");
            inputStream = new FileInputStream(file);
            prop.load(inputStream);
            return prop.getProperty(key, "");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                    // default implementation ignored
                }
            }
        }
    }
}