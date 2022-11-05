# إطار طلب الإذن

![](logo.png)

-   عنوان المشروع:[جيثب](https://github.com/getActivity/XXPermissions)

-   عنوان المدونة:[رمز واحد للحصول على طلب إذن لم يكن أسهل من أي وقت مضى](https://www.jianshu.com/p/c69ff8a445ed)

-   يمكنك مسح الرمز ضوئيًا لتنزيل العرض التوضيحي للتوضيح أو الاختبار. إذا تعذر تنزيل رمز المسح الضوئي ،[انقر هنا للتحميل مباشرة](https://github.com/getActivity/XXPermissions/releases/download/16.2/XXPermissions.apk)

![](picture/demo_code.png)

-   بالإضافة إلى ذلك ، إذا كنت تريد معرفة المزيد عن أذونات Android 6.0 ، فيمكنك قراءة هذه المقالة[قرار إذن وقت تشغيل Android 6.0](https://www.jianshu.com/p/6a4dff744031)

![](picture/1.jpg)![](picture/2.jpg)![](picture/3.jpg)

![](picture/4.jpg)![](picture/5.jpg)![](picture/6.jpg)

![](picture/7.jpg)![](picture/8.jpg)![](picture/9.jpg)

![](picture/10.jpg)![](picture/11.jpg)![](picture/12.jpg)

![](picture/13.jpg)![](picture/14.jpg)![](picture/15.jpg)

#### خطوات الدمج

-   إذا كان تكوين Gradle الخاص بالمشروع قيد التشغيل`7.0 以下`، يجب أن يكون في`build.gradle`تمت إضافة الملف

```groovy
allprojects {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

-   إذا كان تكوين Gradle الخاص بك هو`7.0 及以上`، أنت بحاجه إلى`settings.gradle`تمت إضافة الملف

```groovy
dependencyResolutionManagement {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

-   بعد تكوين المستودع البعيد ، ضمن وحدة تطبيق المشروع`build.gradle`أضف التبعيات البعيدة إلى الملف

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

#### متوافق مع AndroidX

-   إذا كان المشروع يعتمد على**AndroidX**الحزمة ، من فضلك في هذا البند`gradle.properties`تمت إضافة الملف

```text
# 表示将第三方库迁移到 AndroidX
android.enableJetifier = true
```

-   إذا كان المشروع يعتمد على**الدعم**لا يلزم إضافة الحزم إلى هذا التكوين

#### تخزين التقسيم

-   إذا تم تكييف المشروع مع ميزة تخزين قسم Android 10 ، فيرجى الانتقال إلى`AndroidManifest.xml`إنضم إلى

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

-   إذا كان المشروع الحالي لا يتكيف مع هذه الميزة ، فيمكن تجاهل هذه الخطوة

-   وتجدر الإشارة إلى أن هذا الخيار يستخدمه إطار العمل لتحديد ما إذا كان المشروع الحالي قد تم تكييفه مع وحدة تخزين القسم. وتجدر الإشارة إلى أنه إذا تم تكييف مشروعك مع ميزة تخزين القسم ، فيمكنك استخدام`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`للتقدم بطلب للحصول على إذن ، إذا لم يتكيف مشروعك بعد مع ميزة التقسيم ، حتى لو تقدمت بطلب`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`ستؤدي الأذونات أيضًا إلى تعذر قراءة الملفات الموجودة على وحدة التخزين الخارجية بشكل طبيعي. إذا كان مشروعك غير مناسب لتخزين القسم ، فالرجاء استخدام`MANAGE_EXTERNAL_STORAGE`لتقديم طلب للحصول على إذن ، بحيث يمكن قراءة الملفات الموجودة على وحدة التخزين الخارجية بشكل طبيعي.إذا كنت تريد معرفة المزيد حول ميزات تخزين قسم Android 10 ، فيمكنك[انقر هنا للمشاهدة والتعلم](https://github.com/getActivity/AndroidVersionAdapter#android-100)。

#### رمز واحد للحصول على طلب إذن لم يكن أسهل من أي وقت مضى

-   مثال على استخدام جافا

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

-   مثال على استخدام Kotlin

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

#### مقدمة لواجهات برمجة التطبيقات الأخرى للإطار

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

#### حول وصف معلمة رد الاتصال مراقبة إذن

-   نعلم جميعًا أنه إذا منح المستخدم كل شيء فسوف يتصل فقط**على**الطريقة ، والتي سيتم استدعاؤها فقط إذا رفض المستخدم الكل**تم رفضه**طريقة.

-   ولكن هناك موقف آخر ، إذا تم طلب أذونات متعددة ، فلن يتم منح جميع الأذونات أو رفضها بالكامل ، ولكن يتم رفض بعض التراخيص جزئيًا. كيف سيتعامل إطار العمل مع رد الاتصال؟

-   سوف يستدعي الإطار أولاً**تم رفضه**الطريقة ، ثم الاتصال**على**طريقة. التي يمكننا المرور منها**على**في الطريقة**الكل**معلمات لتحديد ما إذا تم منح كافة الأذونات.

-   إذا كنت تريد معرفة ما إذا تم منح الإذن في رد الاتصال أم رفضه ، فيمكنك الاتصال**قائمة**في الفصل**يحتوي على (إذن ، XXX)**طريقة لتحديد ما إذا كانت المجموعة تحتوي على هذا الإذن.

## [للأسئلة المتكررة الأخرى ، الرجاء الضغط هنا](HelpDoc.md)

#### مقارنة بين أطر طلب الإذن المماثلة

|                   تفاصيل التكيف                  |                                   [XX الأذونات](https://github.com/getActivity/XXPermissions)                                  |                                     [والإذن](https://github.com/yanzhenjie/AndPermission)                                    |                                    [إذن X](https://github.com/guolindev/PermissionX)                                   |                              [كود Android UT IL](https://github.com/Blankj/AndroidUtilCode)                              |                                              [الأذونات](https://github.com/permissions-dispatcher/PermissionsDispatcher)                                             |                                 [RxPermissions](https://github.com/tbruyelle/RxPermissions)                                |                                   [EasyPermissions](https://github.com/googlesamples/easypermissions)                                  |
| :----------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------: |
|                  الإصدار المقابل                 |                                                              ١٦.أ                                                              |                                                             أ.٠.ع                                                            |                                                          ١.ط.٤                                                         |                                                          ١.٣١.٠                                                          |                                                                                 ٤.ص.أ                                                                                |                                                            ٠.١٢                                                            |                                                                  ع.٠.٠                                                                 |
|                    عدد القضايا                   | [![](https://img.shields.io/github/issues/getActivity/XXPermissions.svg)](https://github.com/getActivity/XXPermissions/issues) | [![](https://img.shields.io/github/issues/yanzhenjie/AndPermission.svg)](https://github.com/yanzhenjie/AndPermission/issues) | [![](https://img.shields.io/github/issues/guolindev/PermissionX.svg)](https://github.com/guolindev/PermissionX/issues) | [![](https://img.shields.io/github/issues/Blankj/AndroidUtilCode.svg)](https://github.com/Blankj/AndroidUtilCode/issues) | [![](https://img.shields.io/github/issues/permissions-dispatcher/PermissionsDispatcher.svg)](https://github.com/permissions-dispatcher/PermissionsDispatcher/issues) | [![](https://img.shields.io/github/issues/tbruyelle/RxPermissions.svg)](https://github.com/tbruyelle/RxPermissions/issues) | [![](https://img.shields.io/github/issues/googlesamples/easypermissions.svg)](https://github.com/googlesamples/easypermissions/issues) |
|                    حجم الإطار                    |                                                          52 كيلو بايت                                                          |                                                         127 كيلو بايت                                                        |                                                       90 كيلوبايت                                                      |                                                       500 كيلو بايت                                                      |                                                                             99 كيلو بايت                                                                             |                                                        28 كيلو بايت                                                        |                                                              48 كيلو بايت                                                              |
|                 حالة صيانة الإطار                |                                                         **في الصيانة**                                                         |                                                        توقف عن الصيانة                                                       |                                                     **في الصيانة**                                                     |                                                      توقف عن الصيانة                                                     |                                                                            توقف عن الصيانة                                                                           |                                                       توقف عن الصيانة                                                      |                                                             توقف عن الصيانة                                                            |
|                 إذن تذكير التنبيه                |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|             جميع أذونات إدارة الملفات            |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                تثبيت أذونات الحزمة               |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|               أذونات صورة داخل صورة              |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|              أذونات النافذة العائمة              |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ✅                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                أذونات إعداد النظام               |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ✅                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                أذونات شريط الإعلام               |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|              إذن مراقبة شريط الإعلام             |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                    لا تزعج إذن                   |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|             تجاهل إذن تحسين البطارية             |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|              عرض إذن استخدام التطبيق             |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                    أذونات VPN                    |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|              Android 13 أذونات خطيرة             |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|              Android 12 أذونات خطيرة             |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|              Android 11 أذونات خطيرة             |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|              Android 10 أذونات خطيرة             |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|           Android 9.0 Dangerous أذونات           |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|           Android 8.0 Dangerous أذونات           |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
| أذونات جديدة متوافقة تلقائيًا مع الأجهزة القديمة |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|           شاشة تكيف دوران اتجاه الشاشة           |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|          تعديل سيناريو إذن تطبيق الخلفية         |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|          إصلاح خطأ تسرب ذاكرة Android 12         |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                آلية الكشف عن الخطأ               |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |

#### إدخال أذونات جديدة متوافقة تلقائيًا مع الأجهزة القديمة

-   مع التحديث المستمر لإصدارات Android ، تتزايد الأذونات الخطيرة والأذونات الخاصة أيضًا ، لذلك ستكون هناك مشكلة في توافق الإصدار في هذا الوقت. تدعم أجهزة Android ذات الإصدار العالي تطبيق أذونات الإصدار الأقل ، ولكن أجهزة Android ذات الإصدار الأقل لا تفعل ذلك دعم التقدم للحصول على أذونات. إصدار أعلى من الأذونات ، ثم ستكون هناك مشكلة توافق في هذا الوقت.

-   بعد التحقق ، اختارت أطر الأذونات الأخرى الطريقة الأبسط والأكثر وقاحة ، أي عدم التوافق ، ولكن لمنحه للمتصل الخارجي للتوافق ، الأمر الذي يتطلب من المتصل الحكم أولاً على إصدار Android في الطبقة الخارجية ، و تمرير في الإصدار الأعلى. يتم منح الإذن الجديد لإطار العمل ، ويتم تمرير الإذن القديم إلى إطار العمل في الإصدار الأدنى. تبدو هذه الطريقة بسيطة وغير مهذبة ، ولكن تجربة التطوير سيئة. وفي نفس الوقت ، فهي أيضًا يخفي حفرة يعرف المتصلون الخارجيون أن الإذن الجديد يتوافق مع ما هو الإذن القديم؟ لا أعتقد أن الجميع يعرف ذلك ، وبمجرد أن يكون التصور خاطئًا ، فإنه سيؤدي حتما إلى نتائج خاطئة.

-   أعتقد أن أفضل طريقة هي ترك الأمر لإطار العمل للقيام بذلك ،**XX الأذونات**هذا هو بالضبط ما يتم فعله.عندما يتقدم متصل خارجي للحصول على إذن إصدار أعلى ، فإن الجهاز ذو الإصدار الأقل سيضيف تلقائيًا إذن إصدار أقل للتطبيق. لأبسط مثال ، ظهر Android 11`MANAGE_EXTERNAL_STORAGE`إذن جديد ، إذا تم تطبيقه لهذا الإذن على الأجهزة التي تعمل بنظام Android 10 وما دونه ، فسيضيفه إطار العمل تلقائيًا`READ_EXTERNAL_STORAGE`و`WRITE_EXTERNAL_STORAGE`للتقديم ، على الأجهزة التي تعمل بنظام Android 10 وما دونه ، يمكننا وضع ملفات`MANAGE_EXTERNAL_STORAGE`كما`READ_EXTERNAL_STORAGE`و`WRITE_EXTERNAL_STORAGE`لاستخدامها ، لأن`MANAGE_EXTERNAL_STORAGE`ما يمكن عمله ، على الأجهزة التي تعمل بنظام Android 10 وما دونه ، استخدم`READ_EXTERNAL_STORAGE`و`WRITE_EXTERNAL_STORAGE`للقيام بذلك.

-   لذلك كل شخص يستخدم**XX الأذونات**عندما تريد التقدم بطلب للحصول على إذن جديد ، فأنت لست بحاجة إلى الاهتمام بتوافق الأذونات القديمة والجديدة على الإطلاق. سيعالجها إطار العمل تلقائيًا نيابة عنك. على عكس الأطر الأخرى ، ما أريد القيام به أكثر هو دع الجميع يكتب رمزًا. احصل على طلبات إذن ، يمكن لإطار العمل القيام بذلك ، وكل ذلك إلى إطار العمل للمعالجة.

#### مقدمة التكيف مشهد دوران الشاشة

-   سيؤدي تدوير شاشة النشاط بعد انبثاق مربع حوار تطبيق إذن النظام إلى فشل إعادة استدعاء تطبيق الإذن ، لأن تدوير الشاشة سيؤدي إلى إتلاف الجزء في إطار العمل وإعادة بنائه ، مما يؤدي إلى كائن رد الاتصال الموجود فيه يتم إعادة تدويرها مباشرة ، مما يؤدي في النهاية إلى أن يكون رد الاتصال غير طبيعي. هناك عدة حلول ، أحدها هو إضافة في ملف البيان`android:configChanges="orientation"`السمة ، بحيث لا يتم إتلاف النشاط والجزء وإعادة بنائهما عند تدوير الشاشة. والثاني هو إصلاح اتجاه عرض النشاط مباشرةً في ملف البيان ، ولكن يجب التعامل مع الحلين المذكورين أعلاه بواسطة الأشخاص الذين يستخدمون إطار العمل ، والذي من الواضح أنه غير مرن بدرجة كافية. لا تزال بحاجة إلى ربط الجرس ، يجب حل مشكلة الإطار من خلال الإطار ، و**RxPermissions**الحل هو تعيين كائن PermissionFragment`fragment.setRetainInstance(true)`، لذلك حتى إذا تم تدوير الشاشة ، فسيتم تدمير كائن النشاط وإعادة بنائه ، ولن يتم إتلاف الجزء وإعادة بنائه ، وسيظل الكائن السابق يُعاد استخدامه ، ولكن هناك مشكلة ، إذا تمت إعادة كتابة النشاط**onSaveInstanceState**ستؤدي الطريقة مباشرة إلى فشل هذه الطريقة ، والتي من الواضح أنها مجرد حل مؤقت ، ولكنها ليست السبب الجذري.**XX الأذونات**سيكون الطريق أكثر مباشرة ، في**إذن ، جزء**عندما يكون مرتبطًا بنشاط ما ، فإن النشاط الحالي**اتجاه الشاشة ثابت**، بعد انتهاء طلب الإذن ،**إعادة تعيين اتجاه الشاشة**。

-   في جميع أطر عمل طلبات الأذونات ، تحدث هذه المشكلة طالما يتم استخدام التجزئة للتقدم للحصول على أذونات ، ويتم تطبيق AndPermission فعليًا على الأذونات من خلال إنشاء نشاط جديد ، لذلك لا تحدث هذه المشكلة. للحصول على إذن ، لذلك لا توجد مثل هذه المشكلة ، وتعتمد PermissionX مباشرة على حل XXPermissions ، يرجى الاطلاع على[XX الأذونات / القضايا / 49](https://github.com/getActivity/XXPermissions/issues/49)、[إذن X / قضايا / 51](https://github.com/guolindev/PermissionX/issues/51)。

#### مقدمة سيناريو إذن تطبيق الخلفية

-   عندما نتقدم بطلب للحصول على أذونات بعد إجراء عمليات تستغرق وقتًا طويلاً (على سبيل المثال ، الحصول على اتفاقية الخصوصية على صفحة شاشة البداية ثم التقدم بطلب للحصول على أذونات) ، وإعادة النشاط إلى سطح المكتب (مرة أخرى إلى الخلفية) أثناء عملية طلب الشبكة ، و ثم يتسبب في أن يكون طلب الإذن في حالة الخلفية في هذا الوقت ، قد يكون تطبيق الإذن غير طبيعي ، ولن يتم عرض مربع حوار التفويض. ستؤدي المعالجة غير الصحيحة أيضًا إلى حدوث عطل ، مثل[RxPeremission / قضايا / 249](https://github.com/tbruyelle/RxPermissions/issues/249). والسبب هو أن PermissionFragment في إطار العمل موجود في**الالتزام / الالتزام الآن**عند وصوله إلى النشاط ، سيتم الكشف عنه. إذا كانت حالة النشاط غير مرئية ، فسيتم طرح استثناء ، و**RxPeremission.RxPeremission**مستخدمة بالضبط**الالتزام الآن**سوف يسبب تحطم ، استخدام**الالتزام بالسماح للدولة بفقدان / الالتزام الآن بالسماح بفقدان الدولة**يمكنك تجنب هذا الاكتشاف ، على الرغم من أن هذا يمكن أن يتجنب الأعطال ، ولكن ستكون هناك مشكلة أخرى ، كما يوفرها النظام**طلب الأذونات**لن يؤدي استدعاء واجهة برمجة التطبيقات عندما يكون النشاط غير مرئي إلى ظهور مربع حوار التفويض ،**XX الأذونات**الحل هو**طلب الأذونات**توقيت من**خلق**انتقل الى**سيرة ذاتية**، نظرًا لأن أساليب دورة حياة النشاط والجزء مجمعة معًا ، إذا كان النشاط غير مرئي ، فعندئذٍ حتى إذا تم إنشاء الجزء ، فسيتم استدعاؤه فقط**عند الإنشاء**طريقة دون استدعاء**على الأقل**، وأخيرًا عندما يعود النشاط من الخلفية إلى المقدمة ، فلن يتم تشغيله فقط**النشاط على الاستئناف**الطريقة التي تؤدي أيضًا**إذن ، جزء**من**على الأقل**الطريقة ، التقدم بطلب للحصول على إذن بهذه الطريقة يمكن أن يضمن النهائي**طلب الأذونات**توقيت المكالمة في النشاط**في حالة مرئية**أسفل.

#### مقدمة إصلاح مشكلة تسرب ذاكرة Android 12

-   سألني شخص ما مؤخرًا عن تسرب للذاكرة[XXPermissions / القضايا / 133](https://github.com/getActivity/XXPermissions/issues/133)، لقد أكدت أن هذه المشكلة موجودة بالفعل بعد الممارسة ، ولكن بالنظر إلى مكدس الكود ، وجدت أن هذه المشكلة ناتجة عن كود النظام ، والشروط التالية مطلوبة للتسبب في هذه المشكلة:

    1.  استخدم على أجهزة Android 12

    2.  اتصل`Activity.shouldShowRequestPermissionRationale`

    3.  بعد ذلك ، تم استدعاء طريقة activity.finish بشكل نشط في الكود

-   عملية التحقيق: بعد تتبع الكود ، وجد أن مكدس استدعاء الكود هو مثل هذا

    -   Activity.shouldShowRequestPermission الأساس المنطقي

    -   PackageManager.shouldShowRequestPermissionRationale (كائن التنفيذ هو ApplicationPackageManager)

    -   PermissionManager.shouldShowRequestPermissionRationale

    -   PermissionManager الجديد (سياق السياق)

    -   PermissionUsageHelper جديد (سياق سياق)

    -   AppOpsManager.startWatchingStarted

-   الجاني في الواقع**إذن الاستخدام**امسك كائن السياق كحقل وقم بتسميته في المنشئ`AppOpsManager.startWatchingStarted`شغّل الاستماع حتى تتم إضافة كائن PermissionUsageHelper إلى ملف`AppOpsManager#mStartedWatchers`في المجموعة ، ينتج عن هذا أنه عند انتهاء نشاط المكالمات النشطة ، لا يتم استخدام stopWatchingStarted لإزالة المراقبة ، مما يؤدي إلى الاحتفاظ بكائن النشاط`AppOpsManager#mStartedWatchers`يتم الاحتفاظ به في المجموعة ، لذلك لا يمكن للنظام إعادة تدوير كائن النشاط بشكل غير مباشر.

-   كما أن معالجة هذه المشكلة بسيطة للغاية وغير مهذبة ، أي أنها ستنتقل من الطبقة الخارجية.**سياق**المعلمة من**نشاط**الكائن مع**طلب**قد يقول بعض الأشخاص أن الهدف كافٍ في النشاط فقط`shouldShowRequestPermissionRationale`الطريقة ، ولكن ماذا لو لم يكن هناك مثل هذه الطريقة في التطبيق؟ بالنظر إلى تنفيذ هذه الطريقة ، في الواقع ، ستستدعي هذه الطريقة في النهاية`PackageManager.shouldShowRequestPermissionRationale`طريقة(**إخفاء API ، ولكن ليس في القائمة السوداء**) ، طالما يمكنك الحصول عليها**مدير مجموعة**الهدف كافٍ ، وأخيراً استخدم الانعكاس لتنفيذ هذه الطريقة ، وذلك لتجنب تسرب الذاكرة.

-   لحسن الحظ ، لم تقم Google بتضمين PackageManager.shouldShowRequestPermissionRationale في القائمة السوداء للانعكاس ، وإلا فلا توجد طريقة لمسح Google ass هذه المرة ، وإلا لا يمكن تنفيذها إلا عن طريق تعديل شفرة مصدر النظام ، ولكن لا يمكن تنفيذ هذه الطريقة إلا بواسطة Google في المتابعة تم إصلاح إصدار Android من Android أعلاه ، ولكن لحسن الحظ ، بعد إصدار Android 12 L ، تم إصلاح هذه المشكلة ،[يمكن الاطلاع على سجل الإرسال المحدد بالضغط هنا](https://cs.android.com/android/_/android/platform/frameworks/base/+/0d47a03bfa8f4ca54b883ff3c664cd4ea4a624d9:core/java/android/permission/PermissionUsageHelper.java;dlc=cec069482f80019c12f3c06c817d33fc5ad6151f)، ولكن بالنسبة لنظام التشغيل Android 12 ، لا تزال هذه مشكلة قديمة.

-   من الجدير بالذكر أن XXPermissions هو حاليًا الإطار الأول والوحيد من نوعه لإصلاح هذه المشكلة. بالإضافة إلى ذلك ، بالنسبة لهذه المشكلة ، أعطيت Google أيضًا[AndroidX](https://github.com/androidx/androidx/pull/435)قدم المشروع حلاً مجانًا ، وتم دمج طلب الدمج الآن في الفرع الرئيسي.أعتقد أنه من خلال هذه الخطوة ، سيتم حل مشكلة تسرب الذاكرة على ما يقرب من مليار جهاز يعمل بنظام Android 12 حول العالم.

#### مقدمة في آلية كشف الخطأ

-   في الصيانة اليومية للإطار ، أبلغني العديد من الأشخاص بوجود أخطاء في الإطار ، ولكن بعد التحقيق وتحديد المواقع ، وجد أن 95٪ من المشكلات ناتجة عن بعض العمليات غير المنتظمة للمتصل ، والتي لا تقتصر على تسبب لي في الكثير من المشاكل. المتاعب ، وأيضًا إهدار كبير لوقت وطاقة العديد من الأصدقاء ، لذلك أضفت الكثير من عناصر المراجعة إلى إطار العمل ، في**وضع التصحيح**、**وضع التصحيح**、**وضع التصحيح**بمجرد أن لا تتوافق بعض العمليات مع المواصفات ، سيرمي إطار العمل استثناءً إلى المتصل مباشرةً ، ويوجه المتصل بشكل صحيح لتصحيح الخطأ في معلومات الاستثناء ، على سبيل المثال:

    -   مثيل السياق الوارد ليس كائن نشاط ، أو سيرمي إطار العمل استثناءً ، أو أن حالة النشاط الوارد غير طبيعية (بالفعل**التشطيب**أو**دمرت**) ، يحدث هذا الموقف عمومًا بسبب التقديم غير المتزامن للأذونات ، وسيعمل إطار العمل أيضًا على استثناء. يرجى التقدم بطلب للحصول على الإذن في الوقت المناسب. إذا تعذر تقدير توقيت التطبيق ، فالرجاء إصدار حكم جيد بشأن النشاط الحالة في الطبقة الخارجية قبل التقدم بطلب للحصول على الإذن.

    -   إذا تقدم المتصل بطلب للحصول على أذونات دون تمرير أي أذونات ، فسيرمي إطار العمل استثناءً ، أو إذا كانت الأذونات التي تم تمريرها من قبل المتصل ليست أذونات خطيرة أو أذونات خاصة ، فسيرمي إطار العمل أيضًا استثناءً ، لأن بعض الأشخاص سيستخدمون العادي الأذونات عند تمرير إذن خطير إلى إطار العمل ، سيرفضه النظام مباشرةً.

    -   إذا كان المشروع الحالي غير مناسب للتخزين التقسيمي ، تقدم بطلب`READ_EXTERNAL_STORAGE`و`WRITE_EXTERNAL_STORAGE`الإذن

        -   عندما المشروع`targetSdkVersion >= 29`، يجب تسجيله في ملف البيان`android:requestLegacyExternalStorage="true"`السمة ، وإلا فإن إطار العمل سوف يطرح استثناءً. إذا لم يكن الأمر كذلك ، فسوف يتسبب ذلك في مشكلة. من الواضح أنه تم الحصول على إذن التخزين ، ولكن لا يمكن قراءة الملفات الموجودة على وحدة التخزين الخارجية وكتابتها بشكل طبيعي على جهاز Android 10.

        -   عندما المشروع`targetSdkVersion >= 30`، لا يمكنك التقديم`READ_EXTERNAL_STORAGE`و`WRITE_EXTERNAL_STORAGE`إذن ، ولكن يجب التقدم للحصول عليه`MANAGE_EXTERNAL_STORAGE`الإذن

        -   إذا تم تكييف المشروع الحالي مع تخزين القسم ، فأنت بحاجة فقط إلى تسجيل سمة البيانات الوصفية في ملف البيان:`<meta-data android:name="ScopedStorage" android:value="true" />`

    -   إذا كان الإذن المطلوب يتضمن إذنًا لتحديد المواقع في الخلفية ، فلا يمكن أن يتضمن أذونات غير مرتبطة بالموضع ، وإلا فإن الإطار سيرمي استثناءً ، لأن`ACCESS_BACKGROUND_LOCATION`إذا كان التطبيق ممزوجًا بأذونات أخرى غير متعلقة بالموقع ، فسيتم رفض التطبيق مباشرة على Android 11.

    -   إذا طلب الإذن والمشروع**targetSdkVersion**لا ، يطرح إطار العمل استثناءً لأن**targetSdkVersion**إنه يمثل إصدار Android الذي تم تكييف المشروع معه ، وسيقوم النظام تلقائيًا بإجراء التوافق مع الإصدارات السابقة.من المفترض أن الأذونات المطبقة متاحة فقط في Android 11 ، ولكن**targetSdkVersion**لا يزال التطبيق في 29 ، ثم التطبيق في بعض الطرز سيحتوي على استثناء ترخيص ، أي أن المستخدم قد أذن بوضوح ، لكن النظام دائمًا ما يعرض خطأ.

    -   إذا لم يكن الإذن المطلوب ديناميكيًا في`AndroidManifest.xml`إذا لم تقم بذلك ، يمكنك التقدم بطلب للحصول على إذن ، ولكن لن تظهر نافذة التفويض المنبثقة ، وسيتم رفضها مباشرة من قبل النظام ، ولن يقوم النظام بإعطاء أي نوافذ منبثقة ومطالبات ، وهذا مشكلة في كل نموذج**يجب رؤيته**。

    -   إذا كان الإذن المطلوب ديناميكيًا`AndroidManifest.xml`مسجلة في ، ولكن غير مناسب`android:maxSdkVersion`قيمة الخاصية ، سوف يطرح إطار العمل استثناءً ، على سبيل المثال:`<uses-permission android:name="xxxx" android:maxSdkVersion="29" />`، سينتج عن هذا الإعداد Android 11 (`Build.VERSION.SDK_INT >= 30`) وما فوق لتقديم طلب للحصول على إذن ، سيعتبر النظام أن هذا الإذن غير مسجل في ملف البيان ، وسيرفض طلب الإذن هذا مباشرةً ، ولن يعطي أي نوافذ منبثقة ومطالبات. هذه المشكلة أيضًا لا مفر منها.

    -   إذا قمت بالتقديم في نفس الوقت`MANAGE_EXTERNAL_STORAGE`、`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`بالنسبة لهذه الأذونات الثلاثة ، سيرمي إطار العمل استثناءً ، ويخبرك بعدم التقدم للحصول على هذه الأذونات الثلاثة في نفس الوقت ، وذلك لأنه على الأجهزة التي تعمل بنظام Android 11 وما فوق ، فإن التطبيق`MANAGE_EXTERNAL_STORAGE`إذن ، لا يوجد تطبيق`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`إذن ضروري ، وهذا لأن التطبيق`MANAGE_EXTERNAL_STORAGE`الإذن يعادل أكثر من`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`المزيد من القدرات القوية ، إذا أصررت على القيام بذلك ، فسيؤدي ذلك إلى نتائج عكسية. وبافتراض أن إطار العمل يسمح بذلك ، سيكون هناك طريقتان للترخيص في نفس الوقت ، أحدهما هو تخويل النافذة المنبثقة ، والآخر هو تخويل صفحة الانتقال يجب على المستخدم تنفيذ تفويضين ، ولكن في الحقيقة هناك`MANAGE_EXTERNAL_STORAGE`الإذن كافٍ للاستخدام. في هذا الوقت ، قد يكون لديك سؤال في ذهنك. أنت لا تقدم طلبًا.`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`أذونات ليست أقل من Android 11`MANAGE_EXTERNAL_STORAGE`ألن تكون هذه مشكلة في هذا الإذن؟ يمكنك أن تطمئن إلى هذه المسألة ، فإن إطار العمل سيصدر أحكامًا ، إذا تقدمت بطلب`MANAGE_EXTERNAL_STORAGE`ستتم إضافة الأذونات تلقائيًا في أطر عمل أقل من Android 11`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`تعال لتقديم الطلب ، لذلك لن يكون غير متاح بسبب نقص الأذونات في الإصدار الأدنى.

    -   إذا لم تكن بحاجة إلى الاختبارات المذكورة أعلاه ، يمكنك الاتصال`unchecked`طريقة إيقاف التشغيل ، ولكن تجدر الإشارة إلى أنني لا أوصيك بإيقاف تشغيل هذا الاكتشاف ، لأنه في**وضع الافراج**عندما يتم إغلاقه ، لا تحتاج إلى إغلاقه يدويًا ، وهو موجود فقط في ملف**وضع التصحيح**سيتم تشغيل هذه الاكتشافات فقط.

-   سبب هذه المشاكل هو أننا لسنا على دراية كبيرة بهذه الآليات ، وإذا كان الإطار لا يقيدها ، فستظهر جميع أنواع المشاكل الغريبة. مؤلم باعتباره مؤلف الإطار. لأن هذه المشاكل ليست ناتجة عن إطار العمل ، ولكنها ناتجة عن بعض العمليات غير المنتظمة للمتصل. أعتقد أن أفضل حل لهذه المشكلة هو جعل الإطار يقوم بفحص موحد ، لأنني مؤلف الإطار ، ولدي معرفة حول طلب الإذن.**قدرة مهنية قوية وخبرة كافية**، تعرف ما يجب فعله وما لا تفعله ، حتى تتمكن من اعتراض هذه العمليات الشائنة واحدة تلو الأخرى.

-   عندما تكون هناك مشكلة في طلب الإذن ، هل تريد أن يأتي شخص ما لتذكيرك ويخبرك بالخطأ الذي حدث؟ كيف يتم تصحيحه؟ ومع ذلك ، فإن XXPermissions قد أنجزت ذلك. من بين جميع أطر طلب الإذن ، أنا أول شخص يقوم بذلك ، على ما أعتقد**اصنع إطارًا**لا يقتصر الأمر على القيام بعمل جيد للوظائف والتعامل مع السيناريوهات المعقدة ، ولكن الأهم من ذلك ، القيام بذلك**موجه نحو الناس**لأن إطار العمل نفسه يهدف إلى خدمة الناس ، فإن ما نحتاج إليه ليس فقط تلبية احتياجات الجميع ، ولكن أيضًا لمساعدة الجميع على تجنب الالتفافات في هذه العملية.

#### يسلط الضوء على الإطار

-   إطار طلب الإذن الأول لنظام Android 13

-   إطار عمل طلب الإذن الأول والوحيد لجميع إصدارات Android

-   موجز وسهل الاستخدام: استخدم طريقة الاتصال المتسلسل ، واستخدم سطرًا واحدًا فقط من التعليمات البرمجية

-   حجم مثير للإعجاب: الوظيفة هي الأكثر اكتمالًا في نفس النوع من الإطار ، لكن حجم الإطار هو الجزء السفلي

-   التكيف مع المواقف المتطرفة: بغض النظر عن مدى تطرف البيئة في التقدم للحصول على إذن ، لا يزال إطار العمل قويًا

-   سمات متوافقة مع الإصدارات السابقة: يمكن تطبيق أذونات جديدة في النظام القديم بشكل طبيعي ، وسوف يتكيف إطار العمل تلقائيًا دون الحاجة إلى تكيف المتصل

-   اكتشاف الأخطاء تلقائيًا: إذا كان هناك خطأ ، فسيقوم إطار العمل بنشاط بطرح استثناء للمتصل (يتم الحكم عليه فقط في ظل التصحيح ، قتل الخطأ في المهد)

#### مشاريع أخرى مفتوحة المصدر للمؤلف

-   مركز تكنولوجيا Android:[مشروع أندرويد](https://github.com/getActivity/AndroidProject)![](https://img.shields.io/github/stars/getActivity/AndroidProject.svg)![](https://img.shields.io/github/forks/getActivity/AndroidProject.svg)

-   إصدار Kt للمنصة الوسطى لتكنولوجيا Android:[AndroidProject-Kotlin](https://github.com/getActivity/AndroidProject-Kotlin)![](https://img.shields.io/github/stars/getActivity/AndroidProject-Kotlin.svg)![](https://img.shields.io/github/forks/getActivity/AndroidProject-Kotlin.svg)

-   إطار نخب:[توست أوتيلس](https://github.com/getActivity/ToastUtils)![](https://img.shields.io/github/stars/getActivity/ToastUtils.svg)![](https://img.shields.io/github/forks/getActivity/ToastUtils.svg)

-   إطار عمل الويب:[EasyHttp](https://github.com/getActivity/EasyHttp)![](https://img.shields.io/github/stars/getActivity/EasyHttp.svg)![](https://img.shields.io/github/forks/getActivity/EasyHttp.svg)

-   إطار كتلة العنوان:[شريط العنوان](https://github.com/getActivity/TitleBar)![](https://img.shields.io/github/stars/getActivity/TitleBar.svg)![](https://img.shields.io/github/forks/getActivity/TitleBar.svg)

-   إطار النافذة العائمة:[إكس توست](https://github.com/getActivity/XToast)![](https://img.shields.io/github/stars/getActivity/XToast.svg)![](https://img.shields.io/github/forks/getActivity/XToast.svg)

-   إطار الشكل:[شيبفيو](https://github.com/getActivity/ShapeView)![](https://img.shields.io/github/stars/getActivity/ShapeView.svg)![](https://img.shields.io/github/forks/getActivity/ShapeView.svg)

-   إطار تبديل اللغة:[متعدد اللغات](https://github.com/getActivity/MultiLanguages)![](https://img.shields.io/github/stars/getActivity/MultiLanguages.svg)![](https://img.shields.io/github/forks/getActivity/MultiLanguages.svg)

-   التسامح مع خطأ تحليل Gson:[مصنع GsonFactory](https://github.com/getActivity/GsonFactory)![](https://img.shields.io/github/stars/getActivity/GsonFactory.svg)![](https://img.shields.io/github/forks/getActivity/GsonFactory.svg)

-   إطار عرض السجل:[لوجكات](https://github.com/getActivity/Logcat)![](https://img.shields.io/github/stars/getActivity/Logcat.svg)![](https://img.shields.io/github/forks/getActivity/Logcat.svg)

-   التكيف مع إصدار Android:[AndroidVersionAdapter](https://github.com/getActivity/AndroidVersionAdapter)![](https://img.shields.io/github/stars/getActivity/AndroidVersionAdapter.svg)![](https://img.shields.io/github/forks/getActivity/AndroidVersionAdapter.svg)

-   مواصفات كود Android:[AndroidCodeStandard](https://github.com/getActivity/AndroidCodeStandard)![](https://img.shields.io/github/stars/getActivity/AndroidCodeStandard.svg)![](https://img.shields.io/github/forks/getActivity/AndroidCodeStandard.svg)

-   لوحة صدارة Android مفتوحة المصدر:[AndroidGithubBoss](https://github.com/getActivity/AndroidGithubBoss)![](https://img.shields.io/github/stars/getActivity/AndroidGithubBoss.svg)![](https://img.shields.io/github/forks/getActivity/AndroidGithubBoss.svg)

-   ملحقات بوتيك الاستوديو:[StudioPlugins](https://github.com/getActivity/StudioPlugins)![](https://img.shields.io/github/stars/getActivity/StudioPlugins.svg)![](https://img.shields.io/github/forks/getActivity/StudioPlugins.svg)

-   مجموعة كبيرة من حزم التعبير:[الظل emoji pa c](https://github.com/getActivity/EmojiPackage)![](https://img.shields.io/github/stars/getActivity/EmojiPackage.svg)![](https://img.shields.io/github/forks/getActivity/EmojiPackage.svg)

-   بيانات Json للمحافظات والمدن:[مقاطعة](https://github.com/getActivity/ProvinceJson)![](https://img.shields.io/github/stars/getActivity/ProvinceJson.svg)![](https://img.shields.io/github/forks/getActivity/ProvinceJson.svg)

#### حساب WeChat العام: Android wheel brother

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/official_ccount.png)

#### مجموعة تقنية Android Q: 10047167

#### إذا كنت تعتقد أن مكتبتي مفتوحة المصدر قد ساعدتك في توفير الكثير من وقت التطوير ، فالرجاء مسح رمز الاستجابة السريعة أدناه لمنح مكافأة ، إذا كان بإمكانك تقديم مكافأة 10.24: monkey_face: سيكون أيضًا: thumbsup :. سيشجعني دعمك على مواصلة إنشاء: octocat:

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_ali.png)![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_wechat.png)

#### [انقر لعرض قائمة التبرعات](https://github.com/getActivity/Donate)

## رخصة

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
