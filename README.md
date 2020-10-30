# 权限请求框架

> 码云地址：[Gitee](https://gitee.com/getActivity/XXPermissions)

> [点击此处下载Demo](XXPermissions.apk)，[博文地址：一句代码搞定权限请求，从未如此简单](https://www.jianshu.com/p/c69ff8a445ed)

> 另外想对 Android 6.0 权限需要深入了解的，可以看这篇文章[Android 6.0 运行权限解析](https://www.jianshu.com/p/6a4dff744031)

![](picture/1.jpg) ![](picture/2.jpg) ![](picture/3.jpg)
![](picture/4.jpg) ![](picture/5.jpg) ![](picture/6.jpg)

#### 集成步骤

```groovy
dependencies {
    // 权限请求框架：https://github.com/getActivity/XXPermissions
    implementation 'com.hjq:xxpermissions:9.0'
}
```

#### 一句代码搞定权限请求，从未如此简单

```java
XXPermissions.with(this)
        // 申请安装包权限
        //.permission(Permission.REQUEST_INSTALL_PACKAGES)
        // 申请悬浮窗权限
        //.permission(Permission.SYSTEM_ALERT_WINDOW)
        // 申请通知栏权限
        //.permission(Permission.NOTIFICATION_SERVICE)
        // 申请系统设置权限
        //.permission(Permission.WRITE_SETTINGS)
        // 申请单个权限
        .permission(Permission.RECORD_AUDIO)
        // 申请多个权限
        .permission(Permission.Group.CALENDAR)
        .request(new OnPermission() {

            @Override
            public void hasPermission(List<String> granted, boolean all) {
                if (all) {
                    toast("获取录音和日历权限成功");
                } else {
                    toast("获取部分权限成功，但部分权限未正常授予");
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean never) {
                if (never) {
                    toast("被永久拒绝授权，请手动授予录音和日历权限");
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(MainActivity.this, denied);
                } else {
                    toast("获取录音和日历权限失败");
                }
            }
        });
```
#### 从系统权限设置页返回判断

```java
public class XxxActivity extends AppCompatActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == XXPermissions.REQUEST_CODE) {
            if (XXPermissions.hasPermission(this, Permission.RECORD_AUDIO) &&
                    XXPermissions.hasPermission(this, Permission.Group.CALENDAR)) {
                toast("用户已经在权限设置页授予了录音和日历权限");
            }
        }
    }
}
```

#### 关于权限监听回调参数说明

* 我们都知道，如果用户全部授予只会调用 hasPermission 方法，如果用户全部拒绝只会调用 noPermission 方法。

* 但是还有一种情况，如果在请求多组权限的情况下，这些权限不是被全部授予或者全部拒绝了，而是部分授权部分拒绝这种情况，框架会如何处理回调呢？

* 框架会先调用 noPermission 方法，再调用 hasPermission 方法。其中我们可以通过 hasPermission 方法中的 all 参数来判断权限是否全部授予了。

* 如果想知道回调中的某个权限是否被授权或者拒绝，可以调用 List 类中的 contains(Permission.XXX) 方法来判断这个集合中是否包含了这个权限。

#### 框架亮点

* 第一款适配 Android 11 的权限请求框架，适配过程几乎零成本

* 简洁易用，采用链式调用的方式，使用只需一句代码

* 支持单个权限、多个权限、单个权限组、多个权限组请求

* 支持大部分国产手机直接跳转到具体的权限设置页面

* 支持申请安装包、悬浮窗、通知栏、系统设置权限

* 支持所有危险权限的申请，包含 6.0 之后出现的新权限

* 向下兼容属性，新权限在旧系统可以正常申请，无需调用者适配

* 本框架不依赖任何第三方库，整个框架大小不到 20 kb（在同类框架中排名第一）

* 如果申请的权限没有在清单文件中注册会抛出异常（仅在 Debug 模式下判断）

* 如果申请的权限和项目 targetSdkVersion 不符合要求会抛出异常（仅在 Debug 模式下判断）

#### Android 11 定位适配

* 在 Android 10 上面，定位被划分为前台权限（精确和模糊）和后台权限，而到了 Android 11 上面，需要分别申请这两种权限，如果同时申请这两种权限会惨遭系统无情拒绝，也就是连权限申请对话框都不会弹的那种拒绝。

* 如果你使用的是 XXPermissions 最新版本，那么恭喜你，直接将前台和后台传给框架即可，框架已经自动帮你把这两种权限分开申请了，整个适配过程零成本。

#### Android 11 存储适配

* 如果你的项目需要适配 Android 11 存储权限，那么需要先将 targetSdkVersion 进行升级

```groovy
android 
    defaultConfig {
        targetSdkVersion 30
    }
}
```

* 再添加 Android 11 存储权限注册到清单文件中

```xml
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
```

* 需要注意的是，旧版的存储权限也需要在清单文件中注册，因为在低于 Android 11 的环境下申请存储权限，框架会自动切换到旧版的申请方式

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

* 还需要在清单文件中加上这个属性，否则在 Android 10 的设备上将无法正常读写外部存储上的文件

```xml
<application
    android:requestLegacyExternalStorage="true">
```
    
* 最后直接调用下面这句代码

```java
XXPermissions.with(MainActivity.this)
        // 不适配 Android 11 可以这样写
        //.permission(Permission.Group.STORAGE)
        // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
        .permission(Permission.MANAGE_EXTERNAL_STORAGE)
        .request(new OnPermission() {

            @Override
            public void hasPermission(List<String> granted, boolean all) {
                if (all) {
                    toast("获取存储权限成功");
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean never) {
                if (never) {
                    toast("被永久拒绝授权，请手动授予存储权限");
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(MainActivity.this, denied);
                } else {
                    toast("获取存储权限失败");
                }
            }
        });
```

![](picture/7.jpg)

#### 不同权限请求框架之间的对比

|     功能及细节对比    | [XXPermissions](https://github.com/getActivity/XXPermissions)  | [AndPermission](https://github.com/yanzhenjie/AndPermission) | [RxPermissions](https://github.com/tbruyelle/RxPermissions) | [PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher) |  [EasyPermissions](https://github.com/googlesamples/easypermissions) | [PermissionX](https://github.com/guolindev/PermissionX) 
| :--------: | :------------: | :------------: | :------------: | :------------: | :------------: | :------------: |
|    对应版本  |  9.0 |  2.0.3  |  0.12   |   4.8.0  |  3.0.0   |  1.4.0    |
|    aar 包大小  |  [20 KB](https://bintray.com/getactivity/maven/xxpermissions#files/com/hjq/xxpermissions)  | [127 KB](https://mvnrepository.com/artifact/com.yanzhenjie/permission)  |  [28 KB](https://jitpack.io/#com.github.tbruyelle/rxpermissions)  |   [22 KB](https://bintray.com/hotchemi/org.permissionsdispatcher/permissionsdispatcher#files/org/permissionsdispatcher/permissionsdispatcher)  |  [48 KB](https://bintray.com/easygoogle/EasyPermissions/easypermissions#files/pub/devrel/easypermissions)   |   [32 KB](https://bintray.com/guolindev/maven/permissionx#files/com/permissionx/guolindev/permissionx)  |
|    minSdk 要求  |  API 11+ |  API 14+  |  API 14+   |   API 14+   |  API 14+   |  API 15+    |
|    targetSdk 要求  |  API 23+ |  API 29+  |  API 29+   |   API 29+  |  API 30+   |  API 30+   |
|    class 文件数量  |  8 个  | 110 个  |  3 个  |   37 个  |   15 个  |  16 个   |
|   是否有依赖  |  无任何依赖  | 依赖 Support  |  依赖 AndroidX |  依赖 AndroidX   |   依赖 AndroidX  |   依赖 AndroidX  |
|   安装包权限   |  支持  |  支持  |  不支持  |  不支持   |  不支持   |  不支持   |
|   悬浮窗权限   |  支持  |  支持  |  不支持  |  不支持   |  不支持   |  不支持   |
|   通知栏权限   |  支持  |  出现崩溃  |  不支持  |  不支持   |   不支持  |  不支持   |
|   系统设置权限   |  支持  |  支持  |  不支持  |  不支持   |   不支持  |  不支持   |
|   Android 8.0 两个新危险权限   |  已适配  |  已适配  |  未适配  |   已适配  |  未适配   |   已适配  |
|   Android 10.0 三个新危险权限   |  已适配  |  部分适配  |  未适配  |   已适配  |  未适配   |   已适配  |
|   Android 11 新版存储权限   |  已适配  |  未适配  |  未适配  |   未适配  |  未适配   |   未适配  |
|   Android 11 新版定位策略   |  已适配  |  未适配  |  未适配  |   未适配  |  未适配   |   未适配  |
|   国产手机权限设置界面   |  已适配  |  已适配 |  未适配  |  未适配   |  未适配   |  未适配   |

#### 作者的其他开源项目

* 安卓技术中台：[AndroidProject](https://github.com/getActivity/AndroidProject)

* 网络框架：[EasyHttp](https://github.com/getActivity/EasyHttp)

* 日志框架：[Logcat](https://github.com/getActivity/Logcat)

* 吐司框架：[ToastUtils](https://github.com/getActivity/ToastUtils)

* 标题栏框架：[TitleBar](https://github.com/getActivity/TitleBar)

* 国际化框架：[MultiLanguages](https://github.com/getActivity/MultiLanguages)

* 悬浮窗框架：[XToast](https://github.com/getActivity/XToast)

#### Android技术讨论Q群：78797078

#### 如果您觉得我的开源库帮你节省了大量的开发时间，请扫描下方的二维码随意打赏，要是能打赏个 10.24 :monkey_face:就太:thumbsup:了。您的支持将鼓励我继续创作:octocat:

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_ali.png) ![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_wechat.png)

#### [点击查看捐赠列表](https://github.com/getActivity/Donate)

## License

```text
Copyright 2018 Huang JinQun

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
