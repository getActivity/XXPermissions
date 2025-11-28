package com.hjq.permissions;

import androidx.annotation.NonNull;
import com.hjq.permissions.permission.base.IPermission;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2018/06/15
 *    desc   : 权限请求结果回调接口
 */
public interface OnPermissionCallback {

    /**
     * 权限请求结果回调
     *
     * @param grantedList               授予权限列表
     * @param deniedList                拒绝权限列表
     */
    void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList);
}