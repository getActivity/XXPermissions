package com.hjq.permissions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 反射工具，提供支持缓存的简易反射功能。
 *
 * @author 焕晨HChen
 * @date 2024/09/08
 */
final class InvokeUtils {
    private static final HashMap<String, Method> methodCache = new HashMap<>();
    private static final HashMap<String, Field> fieldCache = new HashMap<>();

    private final static String TAG = "XXPermissions";

    // ----------------------------反射调用方法--------------------------------
    static <T> T callMethod(Object instance, String method, Class<?>[] param, Object... value) {
        return baseInvokeMethod(null, instance, method, param, value);
    }

    static <T> T callStaticMethod(Class<?> clz, String method, Class<?>[] param, Object... value) {
        return baseInvokeMethod(clz, null, method, param, value);
    }

    // ----------------------------设置字段--------------------------------
    static <T> T setField(Object instance, String field, Object value) {
        return baseInvokeField(null, instance, field, true, value);
    }

    static <T> T setStaticField(Class<?> clz, String field, Object value) {
        return baseInvokeField(clz, null, field, true, value);
    }

    static <T> T getField(Object instance, String field) {
        return baseInvokeField(null, instance, field, false, null);
    }

    static <T> T getStaticField(Class<?> clz, String field) {
        return baseInvokeField(clz, null, field, false, null);
    }

    private static <T> T baseInvokeMethod(Class<?> clz /* 类 */, Object instance /* 实例 */, String method /* 方法名 */,
                                          Class<?>[] param /* 方法参数 */, Object... value /* 值 */) {
        Method declaredMethod;
        if (clz == null && instance == null) {
            return null;
        } else if (clz == null) {
            clz = instance.getClass();
        }
        try {
            String methodTag = clz.getName() + "#" + method + "#" + Arrays.toString(param);
            declaredMethod = methodCache.get(methodTag);
            if (declaredMethod == null) {
                declaredMethod = clz.getDeclaredMethod(method, param);
                methodCache.put(methodTag, declaredMethod);
            }
            declaredMethod.setAccessible(true);
            return (T) declaredMethod.invoke(instance, value);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T baseInvokeField(Class<?> clz /* 类 */, Object instance /* 实例 */, String field /* 字段名 */,
                                         boolean set /* 是否为 set 模式 */, Object value /* 指定值 */) {
        Field declaredField = null;
        if (clz == null && instance == null) {
            return null;
        } else if (clz == null) {
            clz = instance.getClass();
        }
        try {
            String fieldTag = clz.getName() + "#" + field;
            declaredField = fieldCache.get(fieldTag);
            if (declaredField == null) {
                try {
                    declaredField = clz.getDeclaredField(field);
                } catch (NoSuchFieldException e) {
                    while (true) {
                        clz = clz.getSuperclass();
                        if (clz == null || clz.equals(Object.class))
                            break;

                        try {
                            declaredField = clz.getDeclaredField(field);
                            break;
                        } catch (NoSuchFieldException ignored) {
                        }
                    }
                    if (declaredField == null) throw e;
                }
                fieldCache.put(fieldTag, declaredField);
            }
            declaredField.setAccessible(true);
            if (set) {
                declaredField.set(instance, value);
                return null;
            } else
                return (T) declaredField.get(instance);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Class<?> findClass(String className) {
        return findClass(className, null);
    }

    static Class<?> findClass(String className, ClassLoader classLoader) {
        try {
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 简易的 prop 条目获取或修改类。
     *
     * @author 焕晨HChen
     * @date 2024/09/08
     */
    final static class PropUtils {
        private static final Class<?> clazz = findClass("android.os.SystemProperties");

        static String getProp(ClassLoader classLoader, String name) {
            return classLoaderMethod(classLoader, name);
        }

        static boolean getProp(String key, boolean def) {
            return Boolean.TRUE.equals(invokeMethod("getBoolean", new Class[]{String.class, boolean.class}, key, def));
        }

        static int getProp(String key, int def) {
            return (int) Optional.ofNullable(invokeMethod("getInt", new Class[]{String.class, int.class}, key, def))
                    .orElse(def);
        }

        static long getProp(String key, long def) {
            return (long) Optional.ofNullable(invokeMethod("getLong", new Class[]{String.class, long.class}, key, def))
                    .orElse(def);
        }

        static String getProp(String key, String def) {
            return (String) Optional.ofNullable(invokeMethod("get", new Class[]{String.class, String.class}, key, def))
                    .orElse(def);
        }

        static String getProp(String key) {
            return (String) Optional.ofNullable(invokeMethod("get", new Class[]{String.class}, key))
                    .orElse("");
        }

        private static String classLoaderMethod(ClassLoader classLoader, String name) {
            return (String) Optional.ofNullable(callStaticMethod(
                    findClass("android.os.SystemProperties", classLoader),
                    "get", new Class[]{String.class}, name)).orElse("");
        }

        private static <T> T invokeMethod(String str, Class<?>[] clsArr, Object... objArr) {
            return callStaticMethod(clazz, str, clsArr, objArr);
        }

        private static class Optional<T> {
            private final T value;

            private Optional(T value) {
                this.value = value;
            }

            static <T> Optional<T> ofNullable(T value) {
                return value == null ? new Optional<>(null) :
                        new Optional<>(value);
            }

            T orElse(T other) {
                return value != null ? value : other;
            }
        }
    }
}
