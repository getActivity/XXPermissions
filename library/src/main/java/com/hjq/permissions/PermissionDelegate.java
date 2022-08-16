package com.hjq.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XXPermissions
 *    time   : 2022/06/11
 *    desc   : 权限委托接口
 */
public interface PermissionDelegate {

   /**
    * 判断某个权限是否授予了
    */
   boolean isGrantedPermission(Context context, String permission);

   /**
    * 判断某个权限是否永久拒绝了
    */
   boolean isPermissionPermanentDenied(Activity activity, String permission);

   /**
    * 获取权限设置页的意图
    */
   Intent getPermissionIntent(Context context, String permission);
}