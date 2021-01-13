package com.hjq.permissions;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求结果回调接口
 */
public interface OnPermissionCallback {

    /**
     * 有权限被同意授予时回调
     *
     * @param permissions           请求成功的权限组
     * @param all                   是否全部授予了
     */
    void onGranted(List<String> permissions, boolean all);

    /**
     * 有权限被拒绝授予时回调
     *
     * @param permissions         请求失败的权限组
     * @param permanentDeniedList
     * @param allNever            所有权限被永久拒绝了
     */
    void onDenied(List<String> permissions, List<String> permanentDeniedList, boolean allNever);
}