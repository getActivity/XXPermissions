# 权限请求框架

![](XXPermissions.gif)

>[点击此处下载Demo](https://raw.githubusercontent.com/getActivity/XXPermissions/master/XXPermissions.apk)，[博客地址](https://www.jianshu.com/p/c69ff8a445ed)

#### 集成步骤

    dependencies {
        implementation 'com.hjq:xxpermissions:3.0'
    }

#### 一句代码搞定权限请求，从未如此简单

    XXPermissions.with(this)
            //.constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
            //.permission(Permission.REQUEST_INSTALL_PACKAGES, Permission.SYSTEM_ALERT_WINDOW) //支持请求安装权限和悬浮窗权限
            .permission(Permission.Group.STORAGE) //支持多个权限组进行请求，不指定则默以清单文件中的危险权限进行请求
            .request(new OnPermission() {

                @Override
                public void hasPermission(List<String> granted, boolean isAll) {
                    
                }

                @Override
                public void noPermission(List<String> denied, boolean quick) {
                    
                }
            });

#### 是否有这个权限

    if (XXPermissions.isHasPermission(this, Permission.Group.STORAGE)) {
		
    }

#### 跳转到设置页面

    XXPermissions.gotoPermissionSettings(this);

#### 框架亮点

* 简洁易用，采用链式调用的方式，使用只需一句代码

* 不指定权限则自动获取清单文件上的危险权限进行申请

* 如果动态申请的权限没有在清单文件中注册会抛出异常

* 支持大部分国产手机直接跳转到具体的权限设置页面

* 可设置被拒绝后继续申请，直到用户授权或者永久拒绝

* 支持请求6.0以上的悬浮窗权限以及8.0以上的安装权限

* 本框架不依赖AppCompatSupport库，兼容Eclipse和Studio

#### 混淆规则

    -dontwarn com.hjq.permissions.**

## License

```text
Copyright 2018 Huang Jinqun

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
