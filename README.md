# 权限请求框架

![](picture/logo.png)

* 码云地址：[Gitee](https://gitee.com/getActivity/XXPermissions)

* 博文地址：[一句代码搞定权限请求，从未如此简单](https://www.jianshu.com/p/c69ff8a445ed)

* 点击此处 [下载 Demo](XXPermissions.apk) 进行演示或者测试

* 另外想对 Android 6.0 权限需要深入了解的，可以看这篇文章[Android 6.0 运行权限解析](https://www.jianshu.com/p/6a4dff744031)

![](picture/1.jpg) ![](picture/2.jpg) ![](picture/3.jpg)
![](picture/4.jpg) ![](picture/5.jpg) ![](picture/6.jpg)

#### 集成步骤

* 在项目根目录下的 `build.gradle` 文件中加入

```groovy
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

* 在项目 app 模块下的 `build.gradle` 文件中加入

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
    implementation 'com.github.getActivity:XXPermissions:11.6'
}
```

#### AndroidX

* 如果项目是基于 **AndroidX** 包，请在项目 `gradle.properties` 文件中加入

```groovy
# 表示将第三方库迁移到 AndroidX
android.enableJetifier = true
```

* 如果项目是基于 **Support** 包则不需要加入此配置

#### 分区存储

* 如果项目已经适配了 Android 10 分区存储特性，请在 Application 初始化时加入

```java
public final class XxxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 当前项目是否已经适配了分区存储的特性
        XXPermissions.setScopedStorage(true);
    }
}
```

* 如果当前项目没有适配这特性，那么这一步骤可以忽略

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
            if (XXPermissions.isGranted(this, Permission.RECORD_AUDIO) &&
                    XXPermissions.isGranted(this, Permission.Group.CALENDAR)) {
                toast("用户已经在权限设置页授予了录音和日历权限");
            } else {
                toast("用户没有在权限设置页授予权限");
            }
        }
    }
}
```

#### 关于权限监听回调参数说明

* 我们都知道，如果用户全部授予只会调用 **onGranted** 方法，如果用户全部拒绝只会调用 **onDenied** 方法。

* 但是还有一种情况，如果在请求多个权限的情况下，这些权限不是被全部授予或者全部拒绝了，而是部分授权部分拒绝这种情况，框架会如何处理回调呢？

* 框架会先调用 **onDenied** 方法，再调用 **onGranted** 方法。其中我们可以通过 **onGranted** 方法中的 **all** 参数来判断权限是否全部授予了。

* 如果想知道回调中的某个权限是否被授权或者拒绝，可以调用 **List** 类中的 **contains(Permission.XXX)** 方法来判断这个集合中是否包含了这个权限。

### [关于 Android 11 适配及常见疑问请点击这里查看](HelpDoc.md)

#### 不同权限请求框架之间的对比

|     功能及细节    | [XXPermissions](https://github.com/getActivity/XXPermissions)  | [AndPermission](https://github.com/yanzhenjie/AndPermission) | [RxPermissions](https://github.com/tbruyelle/RxPermissions) | [PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher) |  [EasyPermissions](https://github.com/googlesamples/easypermissions) | [PermissionX](https://github.com/guolindev/PermissionX) |  [AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)   |
| :--------: | :------------: | :------------: | :------------: | :------------: | :------------: | :------------: | :------------: |
|    对应版本  |  11.6 |  2.0.3  |  0.12   |   4.8.0  |  3.0.0   |  1.4.0    |  1.30.5    |
|    框架体积  |  [28 KB](https://bintray.com/getactivity/maven/xxpermissions#files/com/hjq/xxpermissions)  | [127 KB](https://mvnrepository.com/artifact/com.yanzhenjie/permission)  |  [28 KB](https://jitpack.io/#com.github.tbruyelle/rxpermissions)  |   [91 KB](https://bintray.com/hotchemi/org.permissionsdispatcher/permissionsdispatcher-processor#files/org/permissionsdispatcher/permissionsdispatcher-processor)  |  [48 KB](https://bintray.com/easygoogle/EasyPermissions/easypermissions#files/pub/devrel/easypermissions)   |   [32 KB](https://bintray.com/guolindev/maven/permissionx#files/com/permissionx/guolindev/permissionx)  |   [483 KB](https://bintray.com/blankj/maven/UtilCode#files/com/blankj/utilcode)  |
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
|   低级错误检测机制   |  ✅  |  ❌  |  ❌  |  ❌   |   ❌  |  ❌   |  ❌   |

#### 屏幕旋转场景适配介绍

* 当系统权限申请对话框弹出后对 Activity 进行屏幕旋转，会导致权限申请回调失效，因为屏幕旋转会导致框架中的 Fragment 销毁重建，这样会导致里面的回调对象直接被回收，最终导致回调不正常。解决方案有几种，一是在清单文件中添加  `android:configChanges="orientation"` 属性，这样屏幕旋转时不会导致 Activity 和 Fragment 销毁重建，二是直接在清单文件中固定 Activity 显示的方向，但是以上两种方案都要使用框架的人处理，这样显然是不够灵活的，解铃还须系铃人，框架的问题应当由框架来解决，而 **RxPermissions** 的解决方式是给 PermissionFragment 对象设置 `fragment.setRetainInstance(true)`，这样就算屏幕旋转了，Activity 对象会销毁重建，而 Fragment 也不会跟着销毁重建，还是复用着之前那个对象，但是存在一个问题，如果 Activity 重写了 **onSaveInstanceState** 方法会直接导致这种方式失效，这样做显然只是治标不治本，而 **XXPermissions** 的方式会更直接点，在 **PermissionFragment** 绑定到 Activity 上面时，把当前 Activity 的**屏幕方向固定住**，在权限申请结束后再把**屏幕方向还原回去**。

#### 后台申请权限场景介绍

* 当我们做耗时操作之后申请权限（例如在闪屏页获取隐私协议再申请权限），在网络请求的过程中将 Activity 返回桌面去（退到后台），然后会导致权限请求是在后台状态中进行，在这个时机上就可能会导致权限申请不正常，表现为不会显示授权对话框，处理不当的还会导致崩溃，例如 [RxPeremission/issues/249](https://github.com/tbruyelle/RxPermissions/issues/249)。原因在于框架中的 PermissionFragment 在 **commit / commitNow** 到 Activity 的时候会做一个检测，如果 Activity 的状态是不可见时则会抛出异常，而 **RxPeremission** 正是使用了 **commitNow** 才会导致崩溃 ，使用 **commitAllowingStateLoss / commitNowAllowingStateLoss** 则可以避开这个检测，虽然这样可以避免崩溃，但是会出现另外一个问题，系统提供的 **requestPermissions** API 在 Activity 不可见时调用也不会弹出授权对话框，**XXPermissions** 的解决方式是将 **requestPermissions** 时机从 **create** 转移到了 **resume**，因为 Activity 和 Fragment 的生命周期方法是捆绑在一起的，如果 Activity 是不可见的，那么就算创建了 Fragment 也只会调用 **onCreate** 方法，而不会去调用它的 **onResume** 方法，最后当 Activity 从后台返回到前台时，不仅会触发 **Activity.onResume** 方法，同时也会触发 **PermissionFragment** 的 **onResume** 方法，在这个方法申请权限就可以保证最终 **requestPermissions** 申请的时机是在 Activity **处于可见状态的情况**下。

#### 低级错误检测机制介绍

* 在框架的日常维护中，有很多人跟我反馈过框架有 Bug，但是经过排查和定位发现，这其中有 95% 的问题来自于调用者一些不规范操作导致的，这不仅对我造成很大的困扰，同时也极大浪费了很多小伙伴的时间和精力，于是我在框架中加入了很多审查元素，在 **Debug** 模式下，一旦有某些操作不符合规范，那么框架会直接抛出异常给调用者，并在异常信息中正确指引调用者纠正错误，例如：

    1. 传入的 Context 实例不是 Activity 对象，框架会抛出异常，又或者传入的 Activity 的状态异常（已经 **Finishing** 或者 **Destroyed**），这种情况一般是在异步申请权限导致的，框架也会抛出异常，请在合适的时机申请权限，如果申请的时机无法预估，请在外层做好  Activity 状态判断再进行权限申请。

    2. 如果调用者没有传入任何权限就申请权限的话，框架会抛出异常，如果调用者传入的权限不是危险权限或者特殊权限，框架也会抛出异常，因为有的人会把普通权限当做危险权限传给框架，系统会直接拒绝。

    3. 如果在没有适配分区存储的情况下申请外部存储权限，必须在清单文件中注册 `android:requestLegacyExternalStorage="true"`，否则框架会抛出异常，如果不加会导致获取到存储权限，但是无法在 Android 10 的设备上面正常读写外部存储上的文件。

    4. 如果申请的权限中包含后台定位权限， 那么这里面则不能包含和定位无关的权限，否则框架会抛出异常，因为 `ACCESS_BACKGROUND_LOCATION` 和其他非定位权限定位掺和在一起申请，在 Android 11 上会出现不申请直接被拒绝的情况。

    5. 如果申请的权限和项目中的 **targetSdkVersion** 对不上，框架会抛出异常，是因为 **targetSdkVersion** 代表着项目适配到哪个 Android 版本，系统会自动做向下兼容，假设申请的权限是 Android 11 才出现的，但是 **targetSdkVersion** 还停留在 29，那么在某些机型上的申请，会出现授权异常的情况，也就是用户明明授权了，但是系统返回的始终是 false。

    6. 如果动态申请的权限没有进行在清单文件中注册，那么框架会抛出异常，因为如果不这么做，是可以进行申请权限，但是不会出现授权弹窗，直接被系统拒绝，并且系统不会给出任何弹窗和提示，并且这个问题在每个机型上面都是**必现的**。

* 出现这些问题的原因是，我们对这些机制不太熟悉，而如果框架不加以限制，那么引发各种奇奇怪怪的问题出现，作为框架的作者，表示不仅你们很痛苦，作为框架作者表示也很受伤。因为这些问题不是框架导致的，而是调用者的某些操作不规范导致的。我觉得这个问题最好的解决方式是，由框架做统一的检查，因为我是框架的作者，对权限申请这块知识点有**较强的专业能力和足够的经验**，知道什么该做，什么不该做，这样就可以对这些骚操作进行一一拦截。

* 当权限申请出现问题时，你希不希望能有个人过来提醒你，告诉你哪里错了？该怎么去纠正？然而这些 XXPermissions 都做到了，在所有的权限请求框架中，我算是第一个做这件事的人，我认为**做好一个框架**不仅仅是要把功能做好，把复杂的场景处理好，更重要的是要**以人为本**，因为框架本身就是为人服务的，要做的不仅仅是解决大家的需求，还要帮助大家在这个过程中少走弯路。

#### 框架亮点

* 首款也是唯一一款适配 Android 11 的权限请求框架

* 首款也是唯一一款适配所有 Android 版本的权限请求框架

* 简洁易用：采用链式调用的方式，使用只需一句代码

* 体积感人：功能在同类框架中最全的，但是体积是最小的

* 适配极端情况：无论在多么极端恶劣的环境下申请权限，框架依然坚挺

* 向下兼容属性：新权限在旧系统可以正常申请，框架会做自动适配，无需调用者适配

* 自动检测错误：如果出现低级错误框架会主动抛出异常给调用者（仅在 Debug 下判断，把 Bug 扼杀在摇篮中）

#### 作者的其他开源项目

* 安卓技术中台：[AndroidProject](https://github.com/getActivity/AndroidProject)

* 网络框架：[EasyHttp](https://github.com/getActivity/EasyHttp)

* 吐司框架：[ToastUtils](https://github.com/getActivity/ToastUtils)

* 标题栏框架：[TitleBar](https://github.com/getActivity/TitleBar)

* 国际化框架：[MultiLanguages](https://github.com/getActivity/MultiLanguages)

* 悬浮窗框架：[XToast](https://github.com/getActivity/XToast)

* Gson 解析容错：[GsonFactory](https://github.com/getActivity/GsonFactory)

* 日志查看框架：[Logcat](https://github.com/getActivity/Logcat)

#### 微信公众号：Android轮子哥

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/official_ccount.png)

#### Android 技术分享 QQ 群：78797078

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