# 權限請求框架

![](logo.png)

-   項目地址：[Github](https://github.com/getActivity/XXPermissions)

-   博文地址：[一句代碼搞定權限請求，從未如此簡單](https://www.jianshu.com/p/c69ff8a445ed)

-   可以掃碼下載 Demo 進行演示或者測試，如果掃碼下載不了的，[點擊此處可直接下載](https://github.com/getActivity/XXPermissions/releases/download/16.2/XXPermissions.apk)

![](picture/demo_code.png)

-   另外想對 Android 6.0 權限需要深入了解的，可以看這篇文章[Android 6.0 運行權限解析](https://www.jianshu.com/p/6a4dff744031)

![](picture/1.jpg)![](picture/2.jpg)![](picture/3.jpg)

![](picture/4.jpg)![](picture/5.jpg)![](picture/6.jpg)

![](picture/7.jpg)![](picture/8.jpg)![](picture/9.jpg)

![](picture/10.jpg)![](picture/11.jpg)![](picture/12.jpg)

![](picture/13.jpg)![](picture/14.jpg)![](picture/15.jpg)

#### 集成步驟

-   如果你的項目 Gradle 配置是在`7.0 以下`，需要在`build.gradle`文件中加入

```groovy
allprojects {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

-   如果你的 Gradle 配置是`7.0 及以上`，則需要在`settings.gradle`文件中加入

```groovy
dependencyResolutionManagement {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

-   配置完遠程倉庫後，在項目 app 模塊下的`build.gradle`文件中加入遠程依賴

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
    implementation 'com.github.getActivity:XXPermissions:16.2'
}
```

#### AndroidX 兼容

-   如果項目是基於**安卓X**包，請在項目`gradle.properties`文件中加入

```text
# 表示将第三方库迁移到 AndroidX
android.enableJetifier = true
```

-   如果項目是基於**支持**包則不需要加入此配置

#### 分區存儲

-   如果項目已經適配了 Android 10 分區存儲特性，請在`AndroidManifest.xml`中加入

```xml
<manifest>

    <application>

        <!-- 表示当前项目已经适配了分区存储特性 -->
        <meta-data
            android:name="ScopedStorage"
            android:value="true" />

    </application>

</manifest>
```

-   如果當前項目沒有適配這特性，那麼這一步驟可以忽略

-   需要注意的是：這個選項是框架用於判斷當前項目是否適配了分區存儲，需要注意的是，如果你的項目已經適配了分區存儲特性，可以使用`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`來申請權限，如果你的項目還沒有適配分區特性，就算申請了`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`權限也會導致無法正常讀取外部存儲上面的文件，如果你的項目沒有適配分區存儲，請使用`MANAGE_EXTERNAL_STORAGE`來申請權限，這樣才能正常讀取外部存儲上面的文件，你如果想了解更多關於 Android 10 分區存儲的特性，可以[點擊此處查看和學習](https://github.com/getActivity/AndroidVersionAdapter#android-100)。

#### 一句代碼搞定權限請求，從未如此簡單

-   Java 用法示例

```java
XXPermissions.with(this)
        // 申请单个权限
        .permission(Permission.RECORD_AUDIO)
        // 申请多个权限
        .permission(Permission.Group.CALENDAR)
        // 设置权限请求拦截器（局部设置）
        //.interceptor(new PermissionInterceptor())
        // 设置不触发错误检测机制（局部设置）
        //.unchecked()
        .request(new OnPermissionCallback() {

            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (!all) {
                    toast("获取部分权限成功，但部分权限未正常授予");
                    return;
                }
                toast("获取录音和日历权限成功");
            }

            @Override
            public void onDenied(List<String> permissions, boolean never) {
                if (never) {
                    toast("被永久拒绝授权，请手动授予录音和日历权限");
                    // 如果是被永久拒绝就跳转到应用权限系统设置页面
                    XXPermissions.startPermissionActivity(context, permissions);
                } else {
                    toast("获取录音和日历权限失败");
                }
            }
        });
```

-   Kotlin 用法示例

```kotlin
XXPermissions.with(this)
    // 申请单个权限
    .permission(Permission.RECORD_AUDIO)
    // 申请多个权限
    .permission(Permission.Group.CALENDAR)
    // 设置权限请求拦截器（局部设置）
    //.interceptor(new PermissionInterceptor())
    // 设置不触发错误检测机制（局部设置）
    //.unchecked()
    .request(object : OnPermissionCallback {

        override fun onGranted(permissions: MutableList<String>, all: Boolean) {
            if (!all) {
                toast("获取部分权限成功，但部分权限未正常授予")
                return
            }
            toast("获取录音和日历权限成功")
        }

        override fun onDenied(permissions: MutableList<String>, never: Boolean) {
            if (never) {
                toast("被永久拒绝授权，请手动授予录音和日历权限")
                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                XXPermissions.startPermissionActivity(context, permissions)
            } else {
                toast("获取录音和日历权限失败")
            }
        }
    })
```

#### 框架其他 API 介紹

```java
// 判断一个或多个权限是否全部授予了
XXPermissions.isGranted(Context context, String... permissions);

// 获取没有授予的权限
XXPermissions.getDenied(Context context, String... permissions);

// 判断某个权限是否为特殊权限
XXPermissions.isSpecial(String permission);

// 判断一个或多个权限是否被永久拒绝了
XXPermissions.isPermanentDenied(Activity activity, String... permissions);

// 跳转到应用权限设置页
XXPermissions.startPermissionActivity(Context context, String... permissions);
XXPermissions.startPermissionActivity(Activity activity, String... permissions);
XXPermissions.startPermissionActivity(Activity activity, String... permission, OnPermissionPageCallback callback);
XXPermissions.startPermissionActivity(Fragment fragment, String... permissions);
XXPermissions.startPermissionActivity(Fragment fragment, String... permissions, OnPermissionPageCallback callback);

// 设置不触发错误检测机制（全局设置）
XXPermissions.setCheckMode(false);
// 设置权限申请拦截器（全局设置）
XXPermissions.setInterceptor(new IPermissionInterceptor() {});
```

#### 關於權限監聽回調參數說明

-   我們都知道，如果用戶全部授予只會調用**授予**方法，如果用戶全部拒絕只會調用**拒絕**方法。

-   但是還有一種情況，如果在請求多個權限的情況下，這些權限不是被全部授予或者全部拒絕了，而是部分授權部分拒絕這種情況，框架會如何處理回調呢？

-   框架會先調用**拒絕**方法，再調用**授予**方法。其中我們可以通過**授予**方法中的**全部**參數來判斷權限是否全部授予了。

-   如果想知道回調中的某個權限是否被授權或者拒絕，可以調用**列表**類中的**包含（權限.XXX）**方法來判斷這個集合中是否包含了這個權限。

## [其他常見疑問請點擊此處查看](HelpDoc.md)

#### 同類權限請求框架之間的對比

|         適配細節        |                                      [XX權限](https://github.com/getActivity/XXPermissions)                                      |                                      [和權限](https://github.com/yanzhenjie/AndPermission)                                      |                                     [許可X](https://github.com/guolindev/PermissionX)                                    |                              [Android UT IL code](https://github.com/Blankj/AndroidUtilCode)                             |                                               [權限調度程序](https://github.com/permissions-dispatcher/PermissionsDispatcher)                                              |                                     [接收權限](https://github.com/tbruyelle/RxPermissions)                                     |                                   [EasyPermissions](https://github.com/googlesamples/easypermissions)                                  |
| :-----------------: | :----------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------: |
|         對應版本        |                                                              16.2                                                              |                                                             2.0.3                                                            |                                                          1.6.4                                                         |                                                          1.31.0                                                          |                                                                                 4.9.2                                                                                |                                                            0.12                                                            |                                                                  3.0.0                                                                 |
|       issues 數      | [![](https://img.shields.io/github/issues/getActivity/XXPermissions.svg)](https://github.com/getActivity/XXPermissions/issues) | [![](https://img.shields.io/github/issues/yanzhenjie/AndPermission.svg)](https://github.com/yanzhenjie/AndPermission/issues) | [![](https://img.shields.io/github/issues/guolindev/PermissionX.svg)](https://github.com/guolindev/PermissionX/issues) | [![](https://img.shields.io/github/issues/Blankj/AndroidUtilCode.svg)](https://github.com/Blankj/AndroidUtilCode/issues) | [![](https://img.shields.io/github/issues/permissions-dispatcher/PermissionsDispatcher.svg)](https://github.com/permissions-dispatcher/PermissionsDispatcher/issues) | [![](https://img.shields.io/github/issues/tbruyelle/RxPermissions.svg)](https://github.com/tbruyelle/RxPermissions/issues) | [![](https://img.shields.io/github/issues/googlesamples/easypermissions.svg)](https://github.com/googlesamples/easypermissions/issues) |
|         框架體積        |                                                              52 KB                                                             |                                                            127 KB                                                            |                                                          90 KB                                                         |                                                          500 KB                                                          |                                                                                 99 KB                                                                                |                                                            28 KB                                                           |                                                                  48 KB                                                                 |
|        框架維護狀態       |                                                             **維護中**                                                            |                                                             停止維護                                                             |                                                         **維護中**                                                        |                                                           停止維護                                                           |                                                                                 停止維護                                                                                 |                                                            停止維護                                                            |                                                                  停止維護                                                                  |
|        鬧鐘提醒權限       |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|       所有文件管理權限      |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|        安裝包權限        |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|        畫中畫權限        |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|        懸浮窗權限        |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ✅                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|        系統設置權限       |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ✅                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|        通知欄權限        |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|       通知欄監聽權限       |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|         勿擾權限        |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|       忽略電池優化權限      |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|      查看應用使用情況權限     |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|        VPN 權限       |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|   Android 13 危險權限   |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|   Android 12 危險權限   |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|   Android 11 危險權限   |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|   Android 10 危險權限   |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|   Android 9.0 危險權限  |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|   Android 8.0 危險權限  |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|      新權限自動兼容舊設備     |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|      屏幕方向旋轉場景適配     |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|      後台申請權限場景適配     |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
| Android 12 內存洩漏問題修復 |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|        錯誤檢測機制       |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |

#### 新權限自動兼容舊設備介紹

-   隨著 Android 版本的不斷更新，危險權限和特殊權限也在增加，那麼這個時候會有一個版本兼容問題，高版本的安卓設備是支持申請低版本的權限，但是低版本的安卓設備是不支持申請高版本的權限，那麼這個時候會出現一個兼容性的問題。

-   經過核查，其他權限框架選擇了一種最簡單粗暴的方式，就是不去做兼容，而是交給外層的調用者做兼容，需要調用者在外層先判斷安卓版本，在高版本上面傳入新權限給框架，而在低版本上面傳入舊權限給框架，這種方式看似簡單粗暴，但是開發體驗差，同時也暗藏了一個坑，外層的調用者他們知道這個新權限對應著的舊權限是哪個嗎？我覺得不是每個人都知道，而一旦認知出現錯誤，必然會導致結果出現錯誤。

-   我覺得最好的做法是交給框架來做，**XX權限**正是那麼做的，外層調用者申請高版本權限的時候，那麼在低版本設備上面，會自動添加低版本的權限進行申請，舉個最簡單的例子，Android 11 出現的`MANAGE_EXTERNAL_STORAGE`新權限，如果是在 Android 10 及以下的設備申請這個權限時，框架會自動添加`READ_EXTERNAL_STORAGE`和`WRITE_EXTERNAL_STORAGE`進行申請，在 Android 10 及以下的設備上面，我們可以直接把`MANAGE_EXTERNAL_STORAGE`當做`READ_EXTERNAL_STORAGE`和`WRITE_EXTERNAL_STORAGE`來用，因為`MANAGE_EXTERNAL_STORAGE`能幹的事情，在 Android 10 及以下的設備上面，要用`READ_EXTERNAL_STORAGE`和`WRITE_EXTERNAL_STORAGE`才能做得了。

-   所以大家在使用**XX權限**的時候，直接拿新的權限去申請就可以了，完全不需要關心新舊權限的兼容問題，框架會自動幫你做處理的，與其他框架不同的是，我更想做的是讓大家一句代碼搞定權限請求，框架能做到的，統統交給框架做處理。

#### 屏幕旋轉場景適配介紹

-   當系統權限申請對話框彈出後對 Activity 進行屏幕旋轉，會導致權限申請回調失效，因為屏幕旋轉會導致框架中的 Fragment 銷毀重建，這樣會導致裡面的回調對象直接被回收，最終導致回調不正常。解決方案有幾種，一是在清單文件中添加`android:configChanges="orientation"`屬性，這樣屏幕旋轉時不會導致 Activity 和 Fragment 銷毀重建，二是直接在清單文件中固定 Activity 顯示的方向，但是以上兩種方案都要使用框架的人處理，這樣顯然是不夠靈活的，解鈴還須繫鈴人，框架的問題應當由框架來解決，而**接收權限**的解決方式是給 PermissionFragment 對象設置`fragment.setRetainInstance(true)`，這樣就算屏幕旋轉了，Activity 對象會銷毀重建，而 Fragment 也不會跟著銷毀重建，還是複用著之前那個對象，但是存在一個問題，如果 Activity 重寫了**onSaveInstanceState**方法會直接導致這種方式失效，這樣做顯然只是治標不治本，而**XX權限**的方式會更直接點，在**權限片段**綁定到 Activity 上面時，把當前 Activity 的**屏幕方向固定住**，在權限申請結束後再把**屏幕方向還原回去**。

-   在所有的權限請求框架中，只要使用了 Fragment 申請權限都會出現這個問題，而 AndPermission 其實是通過創建新的 Activity 來申請權限，所以不會出現這個問題，PermissionsDispatcher 則是採用了 APT 生成代碼的形式來申請權限，所以也沒有這個問題，而 PermissionX 則是直接借鑒了 XXPermissions 的解決方案，詳情請見[XXPermissions/issues/49](https://github.com/getActivity/XXPermissions/issues/49)、[PermissionX/問題/51](https://github.com/guolindev/PermissionX/issues/51)。

#### 後台申請權限場景介紹

-   當我們做耗時操作之後申請權限（例如在閃屏頁獲取隱私協議再申請權限），在網絡請求的過程中將 Activity 返回桌面去（退到後台），然後會導致權限請求是在後台狀態中進行，在這個時機上就可能會導致權限申請不正常，表現為不會顯示授權對話框，處理不當的還會導致崩潰，例如[RxPeremission/問題/249](https://github.com/tbruyelle/RxPermissions/issues/249)。原因在於框架中的 PermissionFragment 在**提交/提交現在**到 Activity 的時候會做一個檢測，如果 Activity 的狀態是不可見時則會拋出異常，而**接收許可**正是使用了**現在提交**才會導致崩潰 ，使用**commitAllowingStateLoss / commitNowAllowingStateLoss**則可以避開這個檢測，雖然這樣可以避免崩潰，但是會出現另外一個問題，系統提供的**請求權限**API 在 Activity 不可見時調用也不會彈出授權對話框，**XX權限**的解決方式是將**請求權限**時機從**創造**轉移到了**恢復**，因為 Activity 和 Fragment 的生命週期方法是捆綁在一起的，如果 Activity 是不可見的，那麼就算創建了 Fragment 也只會調用**創建**方法，而不會去調用它的**在線**方法，最後當 Activity 從後台返回到前台時，不僅會觸發**Activity.onResume**方法，同時也會觸發**權限片段**的**在線**方法，在這個方法申請權限就可以保證最終**請求權限**調用的時機是在 Activity**處於可見狀態的情況**下。

#### Android 12 內存洩漏問題修復介紹

-   最近有人跟我提了一個內存洩漏的問題[XXPermissions/issues/133](https://github.com/getActivity/XXPermissions/issues/133)，我經過實踐後確認這個問題真實存在，但是通過查看代碼堆棧，發現這個問題是系統的代碼引起的，引發這個問題需要以下幾個條件：

    1.  在 Android 12 的設備上使用

    2.  調用了`Activity.shouldShowRequestPermissionRationale`

    3.  在這之後又主動在代碼調用了 activity.finish 方法

-   排查的過程：經過對代碼的追踪，發現代碼調用棧是這樣的

    -   Activity.shouldShowRequestPermissionRationale

    -   PackageManager.shouldShowRequestPermissionRationale（實現對象為 ApplicationPackageManager）

    -   PermissionManager.shouldShowRequestPermissionRationale

    -   新的 PermissionManager（上下文上下文）

    -   新 PermissionUsageHelper（上下文上下文）

    -   AppOpsManager.startWatchingStarted

-   罪魁禍首其實是**PermissionUsageHelper**將 Context 對像作為字段持有著，並在構造函數中調用`AppOpsManager.startWatchingStarted`開啟監聽，這樣 PermissionUsageHelper 對象就會被添加進`AppOpsManager#mStartedWatchers`集合中，這樣導致在 Activity 主動調用 finish 的時候，並沒有使用 stopWatchingStarted 來移除監聽，導致 Activity 對像一直被`AppOpsManager#mStartedWatchers`集合中持有著，所以間接導致了 Activity 對象無法被系統回收。

-   針對這個問題處理也很簡單粗暴，就是將在外層傳入的**語境**參數從**活動**對像給替換成**應用**對象即可，有人可能會說了，Activity 裡面才有`shouldShowRequestPermissionRationale`方法，而 Application 裡面沒有這個方法怎麼辦？看了一下這個方法的實現，其實那個方法最終會調用`PackageManager.shouldShowRequestPermissionRationale`方法（**隱藏 API，但是並不在黑名單中**）裡面去，所以只要能獲取到**包管理器**對象即可，最後再使用反射去執行這個方法，這樣就能避免出現內存洩漏。

-   幸好 Google 沒有將 PackageManager.shouldShowRequestPermissionRationale 列入到反射黑名單中，否則這次想給 Google 擦屁股都沒有辦法了，要不然只能用修改系統源碼實現的方式，但這種方式只能等谷歌在後續的 Android 版本上面修復了，不過慶幸的是，在 Android 12 L 的版本之後，這個問題被修復了，[具體的提交記錄可以點擊此處查看](https://cs.android.com/android/_/android/platform/frameworks/base/+/0d47a03bfa8f4ca54b883ff3c664cd4ea4a624d9:core/java/android/permission/PermissionUsageHelper.java;dlc=cec069482f80019c12f3c06c817d33fc5ad6151f)，但是對於 Android 12 而言，這仍是一個歷史遺留問題。

-   值得注意的是：XXPermissions 是目前同類框架第一款也是唯一一款修復這個問題的框架，另外針對這個問題，我還給谷歌的[安卓X](https://github.com/androidx/androidx/pull/435)項目無償提供了解決方案，目前 Merge Request 已被合入主分支，我相信通過這一舉措，將解決全球近 10 億台 Android 12 設備出現的內存洩露問題。

#### 錯誤檢測機制介紹

-   在框架的日常維護中，有很多人跟我反饋過框架有 Bug，但是經過排查和定位發現，這其中有 95% 的問題來自於調用者一些不規範操作導致的，這不僅對我造成很大的困擾，同時也極大浪費了很多小伙伴的時間和精力，於是我在框架中加入了很多審查元素，在**debug 模式**、**debug 模式**、**debug 模式**下，一旦有某些操作不符合規範，那麼框架會直接拋出異常給調用者，並在異常信息中正確指引調用者糾正錯誤，例如：

    -   傳入的 Context 實例不是 Activity 對象，框架會拋出異常，又或者傳入的 Activity 的狀態異常（已經**精加工**或者**毀壞**），這種情況一般是在異步申請權限導致的，框架也會拋出異常，請在合適的時機申請權限，如果申請的時機無法預估，請在外層做好  Activity 狀態判斷再進行權限申請。

    -   如果調用者沒有傳入任何權限就申請權限的話，框架會拋出異常，又或者如果調用者傳入的權限不是危險權限或者特殊權限，框架也會拋出異常，因為有的人會把普通權限當做危險權限傳給框架，系統會直接拒絕。

    -   如果當前項目在沒有適配分區存儲的情況下，申請`READ_EXTERNAL_STORAGE`和`WRITE_EXTERNAL_STORAGE`權限

        -   當項目的`targetSdkVersion >= 29`時，需要在清單文件中註冊`android:requestLegacyExternalStorage="true"`屬性，否則框架會拋出異常，如果不加會導致一個問題，明明已經獲取到存儲權限，但是無法在 Android 10 的設備上面正常讀寫外部存儲上的文件。

        -   當項目的`targetSdkVersion >= 30`時，則不能申請`READ_EXTERNAL_STORAGE`和`WRITE_EXTERNAL_STORAGE`權限，而是應該申請`MANAGE_EXTERNAL_STORAGE`權限

        -   如果當前項目已經適配了分區存儲，那麼只需要在清單文件中註冊一個 meta-data 屬性即可：`<meta-data android:name="ScopedStorage" android:value="true" />`

    -   如果申請的權限中包含後台定位權限， 那麼這裡面則不能包含和定位無關的權限，否則框架會拋出異常，因為`ACCESS_BACKGROUND_LOCATION`和其他非定位權限定位摻雜在一起申請，在 Android 11 上會出現不申請直接被拒絕的情況。

    -   如果申請的權限和項目中的**目標SDK版本**對不上，框架會拋出異常，是因為**目標SDK版本**代表著項目適配到哪個 Android 版本，系統會自動做向下兼容，假設申請的權限是 Android 11 才出現的，但是**目標SDK版本**還停留在 29，那麼在某些機型上的申請，會出現授權異常的情況，也就是用戶明明授權了，但是系統返回的始終是 false。

    -   如果動態申請的權限沒有在`AndroidManifest.xml`中進行註冊，框架會拋出異常，因為如果不這麼做，是可以進行申請權限，但是不會出現授權彈窗，直接被系統拒絕，並且系統不會給出任何彈窗和提示，並且這個問題在每個機型上面都是**必現的**。

    -   如果動態申請的權限有在`AndroidManifest.xml`中進行註冊，但是設定了不恰當的`android:maxSdkVersion`屬性值，框架會拋出異常，舉個例子：`<uses-permission android:name="xxxx" android:maxSdkVersion="29" />`，這樣的設定會導致在 Android 11 （`Build.VERSION.SDK_INT >= 30`）及以上的設備申請權限，系統會認為這個權限沒有在清單文件中註冊，直接拒絕本次的權限申請，並且也是不會給出任何彈窗和提示，這個問題也是必現的。

    -   如果你同時申請了`MANAGE_EXTERNAL_STORAGE`、`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`這三個權限，框架會拋出異常，告訴你不要同時申請這三個權限，這是因為在 Android 11 及以上設備上面，申請了`MANAGE_EXTERNAL_STORAGE`權限，則沒有申請`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`權限的必要，這是因為申請了`MANAGE_EXTERNAL_STORAGE`權限，就等於擁有了比`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`更加強大的能力，如果硬要那麼做反而適得其反，假設框架允許的情況下，會同時出現兩種授權方式，一種是彈窗授權，另一種是跳頁面授權，用戶要進行兩次授權，但是實際上面有了`MANAGE_EXTERNAL_STORAGE`權限就滿足使用了，這個時候大家可能心中有一個疑問了，你不申請`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`權限，Android 11 以下又沒有`MANAGE_EXTERNAL_STORAGE`這個權限，那不是會有問題？關於這個問題大家可以放心，框架會做判斷，如果你申請了`MANAGE_EXTERNAL_STORAGE`權限，在 Android 11 以下框架會自動添加`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`來申請，所以在低版本下也不會因為沒有權限導致的無法使用。

    -   如果你不需要上面這些檢測，可通過調用`unchecked`方法來關閉，但是需要注意的是，我並不建議你去關閉這個檢測，因為在**release 模式**時它是關閉狀態，不需要你手動關閉，而它只在**debug 模式**下才會觸發這些檢測。

-   出現這些問題的原因是，我們對這些機制不太熟悉，而如果框架不加以限制，那麼引發各種奇奇怪怪的問題出現，作為框架的作者，表示不僅你們很痛苦，作為框架作者表示也很受傷。因為這些問題不是框架導致的，而是調用者的某些操作不規範導致的。我覺得這個問題最好的解決方式是，由框架做統一的檢查，因為我是框架的作者，對權限申請這塊知識點有**較強的專業能力和足夠的經驗**，知道什麼該做，什麼不該做，這樣就可以對這些騷操作進行一一攔截。

-   當權限申請出現問題時，你希不希望能有個人過來提醒你，告訴你哪裡錯了？該怎麼去糾正？然而這些 XXPermissions 都做到了，在所有的權限請求框架中，我算是第一個做這件事的人，我認為**做好一個框架**不僅僅是要把功能做好，把複雜的場景處理好，更重要的是要**以人為本**，因為框架本身就是為人服務的，要做的不僅僅是解決大家的需求，還要幫助大家在這個過程中少走彎路。

#### 框架亮點

-   首款適配 Android 13 的權限請求框架

-   首款也是唯一一款適配所有 Android 版本的權限請求框架

-   簡潔易用：採用鍊式調用的方式，使用只需一句代碼

-   體積感人：功能在同類框架中是最全的，但是框架體積是墊底的

-   適配極端情況：無論在多麼極端惡劣的環境下申請權限，框架依然堅挺

-   向下兼容屬性：新權限在舊系統可以正常申請，框架會做自動適配，無需調用者適配

-   自動檢測錯誤：如果出現錯誤框架會主動拋出異常給調用者（僅在 Debug 下判斷，把 Bug 扼殺在搖籃中）

#### 作者的其他開源項目

-   安卓技術中台：[安卓項目](https://github.com/getActivity/AndroidProject)![](https://img.shields.io/github/stars/getActivity/AndroidProject.svg)![](https://img.shields.io/github/forks/getActivity/AndroidProject.svg)

-   安卓技術中台 Kt 版：[AndroidProject-Kotlin](https://github.com/getActivity/AndroidProject-Kotlin)![](https://img.shields.io/github/stars/getActivity/AndroidProject-Kotlin.svg)![](https://img.shields.io/github/forks/getActivity/AndroidProject-Kotlin.svg)

-   吐司框架：[ToastUtils](https://github.com/getActivity/ToastUtils)![](https://img.shields.io/github/stars/getActivity/ToastUtils.svg)![](https://img.shields.io/github/forks/getActivity/ToastUtils.svg)

-   網絡框架：[EasyHttp](https://github.com/getActivity/EasyHttp)![](https://img.shields.io/github/stars/getActivity/EasyHttp.svg)![](https://img.shields.io/github/forks/getActivity/EasyHttp.svg)

-   標題欄框架：[標題欄](https://github.com/getActivity/TitleBar)![](https://img.shields.io/github/stars/getActivity/TitleBar.svg)![](https://img.shields.io/github/forks/getActivity/TitleBar.svg)

-   懸浮窗框架：[X吐司](https://github.com/getActivity/XToast)![](https://img.shields.io/github/stars/getActivity/XToast.svg)![](https://img.shields.io/github/forks/getActivity/XToast.svg)

-   Shape 框架：[形狀視圖](https://github.com/getActivity/ShapeView)![](https://img.shields.io/github/stars/getActivity/ShapeView.svg)![](https://img.shields.io/github/forks/getActivity/ShapeView.svg)

-   語種切換框架：[多種語言](https://github.com/getActivity/MultiLanguages)![](https://img.shields.io/github/stars/getActivity/MultiLanguages.svg)![](https://img.shields.io/github/forks/getActivity/MultiLanguages.svg)

-   Gson 解析容錯：[格森工廠](https://github.com/getActivity/GsonFactory)![](https://img.shields.io/github/stars/getActivity/GsonFactory.svg)![](https://img.shields.io/github/forks/getActivity/GsonFactory.svg)

-   日誌查看框架：[日誌貓](https://github.com/getActivity/Logcat)![](https://img.shields.io/github/stars/getActivity/Logcat.svg)![](https://img.shields.io/github/forks/getActivity/Logcat.svg)

-   Android 版本適配：[Android版本適配器](https://github.com/getActivity/AndroidVersionAdapter)![](https://img.shields.io/github/stars/getActivity/AndroidVersionAdapter.svg)![](https://img.shields.io/github/forks/getActivity/AndroidVersionAdapter.svg)

-   Android 代碼規範：[安卓代碼標準](https://github.com/getActivity/AndroidCodeStandard)![](https://img.shields.io/github/stars/getActivity/AndroidCodeStandard.svg)![](https://img.shields.io/github/forks/getActivity/AndroidCodeStandard.svg)

-   Android 開源排行榜：[安卓GithubBoss](https://github.com/getActivity/AndroidGithubBoss)![](https://img.shields.io/github/stars/getActivity/AndroidGithubBoss.svg)![](https://img.shields.io/github/forks/getActivity/AndroidGithubBoss.svg)

-   Studio 精品插件：[工作室插件](https://github.com/getActivity/StudioPlugins)![](https://img.shields.io/github/stars/getActivity/StudioPlugins.svg)![](https://img.shields.io/github/forks/getActivity/StudioPlugins.svg)

-   表情包大集合：[表情符號 pa c 陰影](https://github.com/getActivity/EmojiPackage)![](https://img.shields.io/github/stars/getActivity/EmojiPackage.svg)![](https://img.shields.io/github/forks/getActivity/EmojiPackage.svg)

-   省市區 Json 數據：[省Json](https://github.com/getActivity/ProvinceJson)![](https://img.shields.io/github/stars/getActivity/ProvinceJson.svg)![](https://img.shields.io/github/forks/getActivity/ProvinceJson.svg)

#### 微信公眾號：Android輪子哥

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/official_ccount.png)

#### Android 技術 Q 群：10047167

#### 如果您覺得我的開源庫幫你節省了大量的開發時間，請掃描下方的二維碼隨意打賞，要是能打賞個 10.24 :monkey_face:就太:thumbsup:了。您的支持將鼓勵我繼續創作:octocat:

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_ali.png)![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_wechat.png)

#### [點擊查看捐贈列表](https://github.com/getActivity/Donate)

## 執照

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
