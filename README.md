# 权限请求框架

* 码云地址：[Gitee](https://gitee.com/getActivity/XXPermissions)

* 博文地址：[一句代码搞定权限请求，从未如此简单](https://www.jianshu.com/p/c69ff8a445ed)

* 点击此处 [下载 Demo](XXPermissions.apk) 进行演示或者测试

* 另外想对 Android 6.0 权限需要深入了解的，可以看这篇文章[Android 6.0 运行权限解析](https://www.jianshu.com/p/6a4dff744031)

![](picture/1.jpg) ![](picture/2.jpg) ![](picture/3.jpg)
![](picture/4.jpg) ![](picture/5.jpg) ![](picture/6.jpg)

#### 集成步骤

```groovy
android {
    // 支持 JDK 1.8
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    // 权限请求框架：https://github.com/getActivity/XXPermissions
    implementation 'com.hjq:xxpermissions:9.8'
}
```

#### AndroidX

* 如果项目是基于 **AndroidX** 包，请在 `gradle.properties` 中加入

```groovy
# 表示将第三方库迁移到 AndroidX
android.enableJetifier = true
```

* 如果项目是基于 **Support** 包则不需要加入此配置

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
        .request(new OnPermissionCallback() {

            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (all) {
                    toast("获取录音和日历权限成功");
                } else {
                    toast("获取部分权限成功，但部分权限未正常授予");
                }
            }

            @Override
            public void onDenied(List<String> permissions, boolean never) {
                if (never) {
                    toast("被永久拒绝授权，请手动授予录音和日历权限");
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(MainActivity.this, permissions);
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
            if (XXPermissions.isGrantedPermission(this, Permission.RECORD_AUDIO) &&
                    XXPermissions.isGrantedPermission(this, Permission.Group.CALENDAR)) {
                toast("用户已经在权限设置页授予了录音和日历权限");
            } else {
                toast("用户没有在权限设置页授予权限");
            }
        }
    }
}
```

#### 关于权限监听回调参数说明

* 我们都知道，如果用户全部授予只会调用 `onGranted` 方法，如果用户全部拒绝只会调用 `onDenied` 方法。

* 但是还有一种情况，如果在请求多组权限的情况下，这些权限不是被全部授予或者全部拒绝了，而是部分授权部分拒绝这种情况，框架会如何处理回调呢？

* 框架会先调用 `onDenied` 方法，再调用 `onGranted` 方法。其中我们可以通过 `onGranted` 方法中的 `all` 参数来判断权限是否全部授予了。

* 如果想知道回调中的某个权限是否被授权或者拒绝，可以调用 `List` 类中的 `contains(Permission.XXX)` 方法来判断这个集合中是否包含了这个权限。

### [关于 Android 11 适配及常见疑问请点击这里查看](HelpDoc.md)

#### 不同权限请求框架之间的对比

|     功能及细节    | [XXPermissions](https://github.com/getActivity/XXPermissions)  | [AndPermission](https://github.com/yanzhenjie/AndPermission) | [RxPermissions](https://github.com/tbruyelle/RxPermissions) | [PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher) |  [EasyPermissions](https://github.com/googlesamples/easypermissions) | [PermissionX](https://github.com/guolindev/PermissionX) |  [PermissionUtils](https://github.com/Blankj/AndroidUtilCode)   |
| :--------: | :------------: | :------------: | :------------: | :------------: | :------------: | :------------: | :------------: |
|    对应版本  |  9.8 |  2.0.3  |  0.12   |   4.8.0  |  3.0.0   |  1.4.0    |  1.30.5    |
|    aar 包大小  |  [21 KB](https://bintray.com/getactivity/maven/xxpermissions#files/com/hjq/xxpermissions)  | [127 KB](https://mvnrepository.com/artifact/com.yanzhenjie/permission)  |  [28 KB](https://jitpack.io/#com.github.tbruyelle/rxpermissions)  |   [22 KB](https://bintray.com/hotchemi/org.permissionsdispatcher/permissionsdispatcher#files/org/permissionsdispatcher/permissionsdispatcher)  |  [48 KB](https://bintray.com/easygoogle/EasyPermissions/easypermissions#files/pub/devrel/easypermissions)   |   [32 KB](https://bintray.com/guolindev/maven/permissionx#files/com/permissionx/guolindev/permissionx)  |   [483 KB](https://bintray.com/blankj/maven/UtilCode#files/com/blankj/utilcode)  |
|   安装包权限   |  ✅  |  ✅  |  ❌  |  ❌   |  ❌   |  ❌   |  ❌   |
|   悬浮窗权限   |  ✅  |  ✅  |  ❌  |  ❌   |  ❌   |  ❌   |   ✅  |
|   通知栏权限   |  ✅  |  ✅  |  ❌  |  ❌   |   ❌  |  ❌   |  ❌   |
|   系统设置权限   |  ✅  |  ✅  |  ❌  |  ❌   |   ❌  |  ❌   |   ✅  |
|   Android 8.0 权限适配   |  ✅  |  ✅  |  ❌  |   ✅  |  ❌   |   ✅  |  ❌   |
|   Android 9.0 权限适配   |  ✅  |  ❌  |  ❌  |   ✅  |  ❌   |   ❌  |  ❌   |
|   Android 10.0 权限适配  |  ✅  |  ✅  |  ❌  |   ✅  |  ❌   |   ✅  |  ❌   |
|   Android 11 新版存储权限   |  ✅  |  ❌  |  ❌  |   ❌  |  ❌   |   ❌  |  ❌   |
|   Android 11 新版定位策略   |  ✅  |  ❌  |  ❌  |   ❌  |  ❌   |   ❌  |  ❌   |
|   屏幕方向旋转场景适配   |  ✅  |  ✅  |  ❌  |  ✅   |   ❌  |  ❌   |  ❌   |
|   后台申请权限场景适配   |  ✅  |  ❌  |  ❌  |  ❌   |   ❌  |  ❌   |  ❌   |

* 屏幕旋转场景适配介绍：当系统权限申请对话框弹出后对 Activity 进行屏幕旋转，会导致权限申请回调失效，因为屏幕旋转会导致框架中的 Fragment 销毁重建，这样会导致里面的回调对象直接被回收，最终导致回调不正常。解决方案有几种，一是在清单文件中添加  `android:configChanges="orientation"` 属性，这样屏幕旋转时不会导致 Activity 和 Fragment 销毁重建，二是直接在清单文件中固定 Activity 显示的方向，但是以上两种方案都要使用框架的人处理，这样显然是不够灵活的，解铃还须系铃人，框架的问题应当由框架来解决，而 **RxPermissions** 的解决方式是给 PermissionFragment 对象设置 `fragment.setRetainInstance(true)`，这样就算屏幕旋转了，Activity 对象会销毁重建，而 Fragment 也不会跟着销毁重建，还是复用着之前那个对象，但是存在一个问题，如果 Activity 重写了 `onSaveInstanceState` 方法会直接导致这种方式失效，这样做显然只是治标不治本，而 **XXPermissions** 的方式会更直接点，在 PermissionFragment 绑定到 Activity 上面时，把当前 Activity 的**屏幕方向固定住**，在权限申请结束后再把**屏幕方向还原回去**。

* 后台申请权限场景介绍：当我们做耗时操作之后申请权限（例如在闪屏页获取隐私协议再申请权限），在网络请求的过程中将 Activity 返回桌面去（退到后台），然后会导致权限请求是在后台状态中进行，在这个时机上就可能会导致权限申请不正常，表现为不会显示授权对话框，处理不当的还会导致崩溃，例如 [RxPeremission/issues/249](https://github.com/tbruyelle/RxPermissions/issues/249)。原因在于框架中的 PermissionFragment 在 `commit / commitNow` 到 Activity 的时候会做一个检测，如果 Activity 的状态是不可见时则会抛出异常，而 **RxPeremission** 正是使用了 `commitNow` 才会导致崩溃 ，使用 `commitAllowingStateLoss / commitNowAllowingStateLoss` 则可以避开这个检测，虽然这样可以避免崩溃，但是会出现另外一个问题，系统提供的 `requestPermissions`  API 在 Activity 不可见时调用也不会弹出授权对话框，**XXPermissions** 的解决方式是将 `requestPermissions` 时机从 **create** 转移到了 **resume**，因为 Activity 和 Fragment 的生命周期方法是捆绑在一起的，如果 Activity 是不可见的，那么就算创建了 Fragment 也只会调用 **onCreate** 方法，而不会去调用它的 **onResume** 方法，最后当 Activity 从后台返回到前台时，不仅会触发 **Activity.onResume** 方法，同时也会触发 **PermissionFragment.onResume** 方法，在这个方法申请权限就可以保证 `requestPermissions` 申请的时机是在 Activity **处于可见状态的情况**下。

* **XXPermissions** 最强大的地方在于，不止是支持了所有危险权限和特殊权限的申请，还对复杂的场景进行了思考和处理，我的想法很简单，让每一个用 **XXPermissions** 的开发者都不需要关心和处理这些问题，能处理的问题框架已经处理好了，使用者只需要用**一句代码搞定权限请求**即可，这便是我做这个框架的初心，因为我相信这样的权限请求框架才是大家真正想要的。

#### 框架亮点

* 首款也是唯一一款适配 Android 11 的权限请求框架

* 首款也是唯一一款适配所有 Android 版本的权限请求框架

* 简洁易用，采用链式调用的方式，使用只需一句代码

* 支持单个权限、多个权限、单个权限组、多个权限组请求

* 向下兼容属性：新权限在旧系统可以正常申请，框架会做自动适配，无需调用者适配

* 自动检测低级错误：如果出现低级错误框架会抛出异常给调用者（仅在 Debug 模式下判断，把 Bug 扼杀在摇篮中）

#### 作者的其他开源项目

* 安卓技术中台：[AndroidProject](https://github.com/getActivity/AndroidProject)

* 网络框架：[EasyHttp](https://github.com/getActivity/EasyHttp)

* 吐司框架：[ToastUtils](https://github.com/getActivity/ToastUtils)

* 标题栏框架：[TitleBar](https://github.com/getActivity/TitleBar)

* 国际化框架：[MultiLanguages](https://github.com/getActivity/MultiLanguages)

* 悬浮窗框架：[XToast](https://github.com/getActivity/XToast)

* Gson 解析容错：[GsonFactory](https://github.com/getActivity/GsonFactory)

* 日志查看框架：[Logcat](https://github.com/getActivity/Logcat)

#### Android技术讨论Q群：78797078

#### 微信公众号：Android轮子哥

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/official_ccount.png)

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