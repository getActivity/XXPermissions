package com.hjq.permissions;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/07/18
 *    desc   : 动态申请的权限没有在清单文件中注册会抛出的异常
 */
final class ManifestRegisterException extends RuntimeException {

    ManifestRegisterException() {
        // 清单文件中没有注册任何权限
        super("No permissions are registered in the manifest file");
    }

    ManifestRegisterException(String permission) {
        // 申请的危险权限没有在清单文件中注册
        super(permission + ": Permissions are not registered in the manifest file");
    }
}