# Cadre de demande d'autorisation

![](logo.png)

-   adresse du projet :[GithubGenericName](https://github.com/getActivity/XXPermissions)

-   Adresse du blog :[Un code pour obtenir une demande d'autorisation n'a jamais été aussi simple](https://www.jianshu.com/p/c69ff8a445ed)

-   Vous pouvez scanner le code pour télécharger la démo à des fins de démonstration ou de test. Si le code de numérisation ne peut pas être téléchargé,[Cliquez ici pour télécharger directement](https://github.com/getActivity/XXPermissions/releases/download/16.2/XXPermissions.apk)

![](picture/demo_code.png)

-   De plus, si vous voulez en savoir plus sur les autorisations d'Android 6.0, vous pouvez lire cet article[Résolution des autorisations d'exécution d'Android 6.0](https://www.jianshu.com/p/6a4dff744031)

![](picture/1.jpg)![](picture/2.jpg)![](picture/3.jpg)

![](picture/4.jpg)![](picture/5.jpg)![](picture/6.jpg)

![](picture/7.jpg)![](picture/8.jpg)![](picture/9.jpg)

![](picture/10.jpg)![](picture/11.jpg)![](picture/12.jpg)

![](picture/13.jpg)![](picture/14.jpg)![](picture/15.jpg)

#### Étapes d'intégration

-   Si la configuration de Gradle de votre projet est en`7.0 以下`, doit être dans`build.gradle`fichier ajouté

```groovy
allprojects {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

-   Si votre configuration Gradle est`7.0 及以上`, vous devez`settings.gradle`fichier ajouté

```groovy
dependencyResolutionManagement {
    repositories {
        // JitPack 远程仓库：https://jitpack.io
        maven { url 'https://jitpack.io' }
    }
}
```

-   Après avoir configuré l'entrepôt distant, sous le module d'application de projet`build.gradle`Ajouter des dépendances distantes au fichier

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

#### Compatible AndroidX

-   Si le projet est basé sur**AndroidX**paquet, s'il vous plaît dans l'article`gradle.properties`fichier ajouté

```text
# 表示将第三方库迁移到 AndroidX
android.enableJetifier = true
```

-   Si le projet est basé sur**Soutien**Les packages n'ont pas besoin d'être ajoutés à cette configuration

#### partition de stockage

-   Si le projet a été adapté à la fonctionnalité de stockage de partition Android 10, veuillez vous rendre sur`AndroidManifest.xml`se joindre à

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

-   Si le projet en cours ne s'adapte pas à cette fonctionnalité, cette étape peut être ignorée

-   Il convient de noter que cette option est utilisée par le framework pour déterminer si le projet en cours est adapté au stockage de partitions. Il convient de noter que si votre projet a été adapté à la fonctionnalité de stockage de partitions, vous pouvez utiliser`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`Pour demander une autorisation, si votre projet n'est pas encore adapté à la fonctionnalité de partition, même si vous postulez`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`Les autorisations empêcheront également la lecture normale des fichiers sur le stockage externe. Si votre projet n'est pas adapté au stockage de partition, veuillez utiliser`MANAGE_EXTERNAL_STORAGE`Pour demander une autorisation, afin que les fichiers sur le stockage externe puissent être lus normalement.Si vous souhaitez en savoir plus sur les fonctionnalités du stockage de partition Android 10, vous pouvez[Cliquez ici pour voir et apprendre](https://github.com/getActivity/AndroidVersionAdapter#android-100)。

#### Un code pour obtenir une demande d'autorisation n'a jamais été aussi simple

-   Exemple d'utilisation Java

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

-   Exemple d'utilisation de Kotlin

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

#### Introduction aux autres API du framework

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

#### À propos de la description du paramètre de rappel de surveillance des autorisations

-   Nous savons tous que si l'utilisateur accorde tout, il n'appellera que**onGranted**méthode, qui ne sera appelée que si l'utilisateur rejette tous**onDenied**méthode.

-   Mais il existe une autre situation. Si plusieurs autorisations sont demandées, ces autorisations ne sont pas toutes accordées ou toutes refusées, mais certaines des autorisations sont partiellement refusées. Comment le framework va-t-il gérer le rappel ?

-   Le cadre appellera d'abord**onDenied**méthode, puis appelez**onGranted**méthode. dont nous pouvons passer**onGranted**dans la méthode**tout**paramètres pour déterminer si toutes les autorisations sont accordées.

-   Si vous voulez savoir si une autorisation dans le rappel est accordée ou refusée, vous pouvez appeler**Liste**en classe**contient (Permission.XXX)**méthode pour déterminer si l'ensemble contient cette autorisation.

## [Pour d'autres questions fréquemment posées, veuillez cliquer ici](HelpDoc.md)

#### Comparaison entre des cadres de demande d'autorisation similaires

|                               Détails de l'adaptation                               |                                 [XXAutorisations](https://github.com/getActivity/XXPermissions)                                |                                 [EtAutorisation](https://github.com/yanzhenjie/AndPermission)                                |                                 [PermissionX](https://github.com/guolindev/PermissionX)                                |                              [Code Android UT IL](https://github.com/Blankj/AndroidUtilCode)                             |                                       [PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher)                                       |                                 [RxPermissions](https://github.com/tbruyelle/RxPermissions)                                |                                   [EasyPermissions](https://github.com/googlesamples/easypermissions)                                  |
| :---------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------: |
|                                version correspondante                               |                                                              16.2                                                              |                                                             2.0.3                                                            |                                                          1.6.4                                                         |                                                          1.31.0                                                          |                                                                                 4.9.2                                                                                |                                                            0.12                                                            |                                                                  3.0.0                                                                 |
|                                 nombre de problèmes                                 | [![](https://img.shields.io/github/issues/getActivity/XXPermissions.svg)](https://github.com/getActivity/XXPermissions/issues) | [![](https://img.shields.io/github/issues/yanzhenjie/AndPermission.svg)](https://github.com/yanzhenjie/AndPermission/issues) | [![](https://img.shields.io/github/issues/guolindev/PermissionX.svg)](https://github.com/guolindev/PermissionX/issues) | [![](https://img.shields.io/github/issues/Blankj/AndroidUtilCode.svg)](https://github.com/Blankj/AndroidUtilCode/issues) | [![](https://img.shields.io/github/issues/permissions-dispatcher/PermissionsDispatcher.svg)](https://github.com/permissions-dispatcher/PermissionsDispatcher/issues) | [![](https://img.shields.io/github/issues/tbruyelle/RxPermissions.svg)](https://github.com/tbruyelle/RxPermissions/issues) | [![](https://img.shields.io/github/issues/googlesamples/easypermissions.svg)](https://github.com/googlesamples/easypermissions/issues) |
|                                   volume du cadre                                   |                                                              52 Ko                                                             |                                                            127 Ko                                                            |                                                          90 Ko                                                         |                                                          500 Ko                                                          |                                                                                 99 Ko                                                                                |                                                            28 Ko                                                           |                                                                  48 Ko                                                                 |
|                            Statut de maintenance du cadre                           |                                                       **En maintenance**                                                       |                                                      arrêter l'entretien                                                     |                                                   **En maintenance**                                                   |                                                    arrêter l'entretien                                                   |                                                                          arrêter l'entretien                                                                         |                                                     arrêter l'entretien                                                    |                                                           arrêter l'entretien                                                          |
|                           Autorisation de rappel d'alarme                           |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                   Toutes les autorisations de gestion de fichiers                   |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                        Installer les autorisations du package                       |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                          Autorisations d'image dans l'image                         |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                          Autorisations de fenêtre flottante                         |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ✅                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                       Autorisations de paramétrage du système                       |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ✅                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                      Autorisations de la barre de notification                      |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|               Autorisation de surveillance de la barre de notification              |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                            Ne pas déranger l'autorisation                           |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                 Ignorer l'autorisation d'optimisation de la batterie                |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                Afficher l'autorisation d'utilisation de l'application               |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                                  Autorisations VPN                                  |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                        Autorisations dangereuses d'Android 13                       |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                        Autorisations dangereuses d'Android 12                       |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                        Autorisations dangereuses d'Android 11                       |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                        Autorisations dangereuses d'Android 10                       |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                       Autorisations dangereuses d'Android 9.0                       |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                       Autorisations dangereuses d'Android 8.0                       |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
| Nouvelles autorisations automatiquement compatibles avec les appareils plus anciens |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                Adaptation de scène de rotation d'orientation d'écran                |                                                                ✅                                                               |                                                               ✅                                                              |                                                            ✅                                                           |                                                             ❌                                                            |                                                                                   ✅                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|         Adaptation du scénario d'autorisation d'application en arrière-plan         |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                  Correction du bogue de fuite de mémoire Android 12                 |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |
|                           Mécanisme de détection d'erreur                           |                                                                ✅                                                               |                                                               ❌                                                              |                                                            ❌                                                           |                                                             ❌                                                            |                                                                                   ❌                                                                                  |                                                              ❌                                                             |                                                                    ❌                                                                   |

#### Introduction de nouvelles autorisations automatiquement compatibles avec les anciens appareils

-   Avec la mise à jour continue des versions d'Android, les autorisations dangereuses et les autorisations spéciales augmentent également, il y aura donc un problème de compatibilité de version en ce moment.Les appareils Android de haute version prennent en charge la demande d'autorisations de version inférieure, mais les appareils Android de version inférieure ne le font pas prend en charge la demande d'autorisations. Une version supérieure des autorisations entraînera un problème de compatibilité pour le moment.

-   Après vérification, d'autres cadres d'autorisation ont choisi la manière la plus simple et la plus grossière, c'est-à-dire de ne pas faire de compatibilité, mais de la donner à l'appelant externe pour compatibilité, ce qui oblige l'appelant à d'abord juger la version Android dans la couche externe, et passer dans la version supérieure. La nouvelle autorisation est donnée au framework et l'ancienne autorisation est transmise au framework sur la version inférieure. Cette méthode semble simple et grossière, mais l'expérience de développement est médiocre. En même temps, elle cache une fosse. Les appelants externes savent que la nouvelle autorisation correspond à Quelle est l'ancienne autorisation ? Je ne pense pas que tout le monde le sache, et une fois que la perception est fausse, cela conduira inévitablement à de mauvais résultats.

-   Je pense que la meilleure façon est de laisser le cadre le faire,**XXAutorisations**C'est exactement ce qui est fait. Lorsqu'un appelant externe demande une autorisation de version supérieure, l'appareil de version inférieure ajoutera automatiquement une autorisation de version inférieure à appliquer. Pour l'exemple le plus simple, l'Android 11 est apparu`MANAGE_EXTERNAL_STORAGE`Nouvelle autorisation, si elle est appliquée pour cette autorisation sur les appareils Android 10 et inférieurs, le framework l'ajoutera automatiquement`READ_EXTERNAL_STORAGE`et`WRITE_EXTERNAL_STORAGE`Pour postuler, sur les appareils Android 10 et inférieurs, nous pouvons directement mettre`MANAGE_EXTERNAL_STORAGE`comme`READ_EXTERNAL_STORAGE`et`WRITE_EXTERNAL_STORAGE`à utiliser, car`MANAGE_EXTERNAL_STORAGE`Ce qui peut être fait, sur les appareils Android 10 et inférieurs, utilisez`READ_EXTERNAL_STORAGE`et`WRITE_EXTERNAL_STORAGE`pour le faire.

-   Donc tout le monde utilise**XXAutorisations**Lorsque vous souhaitez demander une nouvelle autorisation, vous n'avez pas du tout besoin de vous soucier de la compatibilité des anciennes et des nouvelles autorisations. Le framework le gérera automatiquement pour vous. Contrairement à d'autres frameworks, ce que je veux faire de plus, c'est de laissez tout le monde écrire un code. Obtenez des demandes d'autorisation, le cadre peut le faire, tout cela pour le cadre de traitement.

#### Introduction à l'adaptation de scène de rotation d'écran

-   La rotation de l'écran de l'activité après l'apparition de la boîte de dialogue de l'application d'autorisation système entraînera l'échec du rappel de l'application d'autorisation, car la rotation de l'écran entraînera la destruction et la reconstruction du fragment dans le cadre, ce qui entraînera l'objet de rappel qu'il contient. être recyclé directement, et finalement rendre le rappel anormal. Il existe plusieurs solutions, l'une consiste à ajouter dans le fichier manifeste`android:configChanges="orientation"`Attribut, afin que l'activité et le fragment ne soient pas détruits et reconstruits lors de la rotation de l'écran. La seconde consiste à fixer le sens d'affichage de l'activité directement dans le fichier manifeste, mais les deux solutions ci-dessus doivent être gérées par des personnes qui utilisent l'attribut cadre, qui n'est évidemment pas assez souple. Encore faut-il nouer la cloche, le problème du cadre devrait être résolu par le cadre, et**RxPermissions**La solution consiste à définir l'objet PermissionFragment`fragment.setRetainInstance(true)`, donc même si l'écran est tourné, l'objet Activity sera détruit et reconstruit, et le Fragment ne sera pas détruit et reconstruit, et l'objet précédent sera toujours réutilisé, mais il y a un problème, si l'Activity est réécrit**onSaveInstanceState**La méthode conduira directement à l'échec de cette méthode, qui n'est évidemment qu'une solution temporaire, mais pas la cause première.**XXAutorisations**manière serait plus directe, en**PermissionFragment**Lorsqu'il est lié à une activité, l'activité en cours**orientation de l'écran fixe**, une fois la demande d'autorisation terminée,**réinitialiser l'orientation de l'écran**。

-   Dans tous les frameworks de demande d'autorisation, ce problème se produit tant que Fragment est utilisé pour demander des autorisations, et AndPermission applique réellement les autorisations en créant une nouvelle activité, de sorte que ce problème ne se produit pas. PermissionsDispatcher utilise la forme de code généré par APT pour appliquer pour obtenir l'autorisation, il n'y a donc pas de problème de ce type, et PermissionX s'appuie directement sur la solution de XXPermissions, veuillez consulter[XXAutorisations/problèmes/49](https://github.com/getActivity/XXPermissions/issues/49)、[AutorisationX/issues/51](https://github.com/guolindev/PermissionX/issues/51)。

#### Présentation du scénario d'autorisation d'application en arrière-plan

-   Lorsque nous demandons des autorisations après avoir effectué des opérations fastidieuses (par exemple, obtenir l'accord de confidentialité sur la page d'écran de démarrage, puis demander des autorisations), renvoyons l'activité sur le bureau (retour en arrière-plan) pendant le processus de demande de réseau, et puis faire passer la demande d'autorisation à l'état d'arrière-plan À ce stade, l'application d'autorisation peut être anormale et la boîte de dialogue d'autorisation ne s'affichera pas. Une mauvaise manipulation entraînera également un blocage, tel que[RxPeremission/issues/249](https://github.com/tbruyelle/RxPermissions/issues/249). La raison en est que le PermissionFragment dans le framework est en**s'engager / s'engager maintenant**Lorsqu'il arrivera à l'Activité, une détection sera faite. Si l'état de l'Activité est invisible, une exception sera levée, et**RxPeremission**exactement utilisé**s'engager maintenant**provoquera un plantage, utilisez**commitAllowingStateLoss / commitNowAllowingStateLoss**Vous pouvez éviter cette détection, même si cela peut éviter les plantages, mais il y aura un autre problème, le système fournit**requestPermissions**L'appel de l'API lorsque l'activité n'est pas visible ne fera pas apparaître la boîte de dialogue d'autorisation,**XXAutorisations**La solution est de**requestPermissions**chronométrage de**créer**déménagé à**CV**, parce que les méthodes de cycle de vie de l'activité et du fragment sont regroupées, si l'activité est invisible, alors même si le fragment est créé, il ne sera appelé que**surCréer**méthode sans l'appeler**Moins**méthode, et enfin lorsque l'activité revient de l'arrière-plan au premier plan, non seulement elle déclenchera**Activity.onResume**méthode, qui déclenche également**PermissionFragment**de**Moins**méthode, demander une autorisation dans cette méthode peut garantir la finale**requestPermissions**Le moment de l'appel est dans l'Activité**à l'état visible**Vers le bas.

#### Introduction à la réparation du problème de fuite de mémoire Android 12

-   Quelqu'un m'a récemment posé des questions sur une fuite de mémoire[XXAutorisations/problèmes/133](https://github.com/getActivity/XXPermissions/issues/133), j'ai confirmé que ce problème existe vraiment après la pratique, mais en regardant la pile de code, j'ai trouvé que ce problème est causé par le code du système, et les conditions suivantes sont requises pour provoquer ce problème :

    1.  Utilisation sur les appareils Android 12

    2.  appelé`Activity.shouldShowRequestPermissionRationale`

    3.  Après cela, la méthode activity.finish a été activement appelée dans le code

-   Le processus d'investigation : après avoir tracé le code, il s'avère que la pile d'appels de code ressemble à ceci

    -   Activity.shouldShowRequestPermissionRationale

    -   PackageManager.shouldShowRequestPermissionRationale (l'objet d'implémentation est ApplicationPackageManager)

    -   PermissionManager.shouldShowRequestPermissionRationale

    -   nouveau PermissionManager (contexte contextuel)

    -   nouveau PermissionUsageHelper (contexte contextuel)

    -   AppOpsManager.startWatchingStarted

-   Le coupable est en fait**PermissionUsageHelper**Maintenez l'objet Context en tant que champ et appelez-le dans le constructeur`AppOpsManager.startWatchingStarted`Activez l'écoute pour que l'objet PermissionUsageHelper soit ajouté au`AppOpsManager#mStartedWatchers`Dans la collection, cela se traduit par le fait que lorsque l'activité appelle activement finish, stopWatchingStarted n'est pas utilisé pour supprimer la surveillance, ce qui entraîne la conservation de l'objet Activity`AppOpsManager#mStartedWatchers`Il est conservé dans la collection, de sorte que l'objet Activity ne peut pas être recyclé indirectement par le système.

-   La gestion de ce problème est également très simple et grossière, c'est-à-dire qu'il sera transmis depuis la couche externe.**Le contexte**paramètre de**Activité**objet est remplacé par**Application**L'objet ne suffit, diront certains, que dans l'Activité`shouldShowRequestPermissionRationale`méthode, mais que se passe-t-il s'il n'y a pas une telle méthode dans Application ? En regardant l'implémentation de cette méthode, en fait, cette méthode appellera éventuellement`PackageManager.shouldShowRequestPermissionRationale`méthode(**Masquer l'API, mais pas dans la liste noire**), donc tant que vous pouvez obtenir**Directeur chargé d'emballage**L'objet suffit, et enfin utiliser la réflexion pour exécuter cette méthode, afin d'éviter les fuites de mémoire.

-   Heureusement, Google n'a pas inclus PackageManager.shouldShowRequestPermissionRationale dans la liste noire de réflexion, sinon il n'y a aucun moyen d'effacer le cul de Google cette fois, sinon il ne peut être implémenté qu'en modifiant le code source du système, mais cette méthode ne peut être implémentée que par Google dans le La version Android d'Android a été corrigée ci-dessus, mais heureusement, après la version d'Android 12 L, ce problème a été corrigé,[Le dossier de soumission spécifique peut être consulté en cliquant ici](https://cs.android.com/android/_/android/platform/frameworks/base/+/0d47a03bfa8f4ca54b883ff3c664cd4ea4a624d9:core/java/android/permission/PermissionUsageHelper.java;dlc=cec069482f80019c12f3c06c817d33fc5ad6151f), mais pour Android 12, il s'agit toujours d'un problème hérité.

-   Il convient de noter que XXPermissions est actuellement le premier et le seul framework du genre à résoudre ce problème. De plus, pour ce problème, j'ai également donné à Google[AndroidX](https://github.com/androidx/androidx/pull/435)Le projet a fourni une solution gratuite, et Merge Request a maintenant été fusionné dans la branche principale. Je pense que grâce à cette décision, le problème de fuite de mémoire sur près d'un milliard d'appareils Android 12 dans le monde sera résolu.

#### Introduction au mécanisme de détection d'erreurs

-   Dans la maintenance quotidienne du framework, de nombreuses personnes m'ont signalé qu'il y avait des bugs dans le framework, mais après enquête et positionnement, il s'avère que 95% des problèmes sont causés par certaines opérations irrégulières de l'appelant, qui non seulement m'a causé beaucoup de problèmes, d'ennuis, et aussi une grande perte de temps et d'énergie pour de nombreux amis, j'ai donc ajouté beaucoup d'éléments de révision au cadre, en**Mode débogage**、**Mode débogage**、**Mode débogage**Une fois que certaines opérations ne sont pas conformes à la spécification, le framework lèvera directement une exception à l'appelant et guidera correctement l'appelant pour corriger l'erreur dans les informations d'exception, par exemple :

    -   L'instance Context entrante n'est pas un objet Activity, le framework lèvera une exception, ou l'état de l'Activity entrant est anormal (déjà**Finition**ou**Détruit**), cette situation est généralement causée par une demande d'autorisations de manière asynchrone, et le framework lèvera également une exception. Veuillez demander l'autorisation au bon moment. Si le moment de l'application ne peut pas être estimé, veuillez porter un bon jugement sur l'activité statut à la couche externe avant de demander l'autorisation.

    -   Si l'appelant demande des autorisations sans transmettre aucune autorisation, le framework lèvera une exception, ou si les autorisations transmises par l'appelant ne sont pas des autorisations dangereuses ou des autorisations spéciales, le framework lèvera également une exception, car certaines personnes utiliseront des autorisations Lorsqu'une autorisation dangereuse est transmise au framework, le système la refuse directement.

    -   Si le projet en cours n'est pas adapté au stockage de partitions, demandez`READ_EXTERNAL_STORAGE`et`WRITE_EXTERNAL_STORAGE`autorisation

        -   quand le projet`targetSdkVersion >= 29`, il doit être enregistré dans le fichier manifeste`android:requestLegacyExternalStorage="true"`attribut, sinon le framework lèvera une exception. Sinon, cela causera un problème. Évidemment, l'autorisation de stockage a été obtenue, mais les fichiers sur le stockage externe ne peuvent pas être lus et écrits normalement sur l'appareil Android 10.

        -   quand le projet`targetSdkVersion >= 30`, vous ne pouvez pas postuler`READ_EXTERNAL_STORAGE`et`WRITE_EXTERNAL_STORAGE`l'autorisation, mais devrait demander`MANAGE_EXTERNAL_STORAGE`autorisation

        -   Si le projet en cours a été adapté au stockage de partitions, il vous suffit d'enregistrer un attribut de métadonnées dans le fichier manifeste :`<meta-data android:name="ScopedStorage" android:value="true" />`

    -   Si l'autorisation demandée inclut une autorisation de positionnement en arrière-plan, elle ne peut pas inclure d'autorisations non liées au positionnement, sinon le framework lèvera une exception, car`ACCESS_BACKGROUND_LOCATION`Si l'application est mélangée à d'autres autorisations non liées à la localisation, l'application sera directement rejetée sur Android 11.

    -   Si l'autorisation demandée et le projet**targetSdkVersion**Non, le framework lève une exception car**targetSdkVersion**Il représente la version d'Android à laquelle le projet est adapté et le système effectuera automatiquement la rétrocompatibilité. On suppose que les autorisations demandées ne sont disponibles que dans Android 11, mais**targetSdkVersion**Toujours en restant à 29, l'application sur certains modèles aura une exception d'autorisation, c'est-à-dire que l'utilisateur a clairement autorisé, mais le système renvoie toujours faux.

    -   Si l'autorisation demandée dynamiquement n'est pas dans`AndroidManifest.xml`Si vous ne le faites pas, vous pouvez demander une autorisation, mais la fenêtre contextuelle d'autorisation n'apparaîtra pas, et elle sera directement rejetée par le système, et le système n'affichera aucune fenêtre contextuelle ni invite, et cela problème sur tous les modèles**à voir**。

    -   Si l'autorisation demandée dynamiquement a`AndroidManifest.xml`enregistré dans le , mais inapproprié`android:maxSdkVersion`valeur de la propriété, le framework lèvera une exception, par exemple :`<uses-permission android:name="xxxx" android:maxSdkVersion="29" />`, un tel paramètre se traduira par Android 11 (`Build.VERSION.SDK_INT >= 30`) et ci-dessus pour demander une autorisation, le système considérera que cette autorisation n'est pas enregistrée dans le fichier manifeste, et rejettera directement cette demande d'autorisation, et n'affichera aucune fenêtre contextuelle ni invite. Ce problème est également inévitable.

    -   Si vous postulez en même temps`MANAGE_EXTERNAL_STORAGE`、`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`Pour ces trois autorisations, le framework lèvera une exception, vous indiquant de ne pas demander ces trois autorisations en même temps, car sur les appareils Android 11 et supérieurs, l'application`MANAGE_EXTERNAL_STORAGE`autorisation, pas de demande`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`L'autorisation est nécessaire, c'est parce que l'application`MANAGE_EXTERNAL_STORAGE`L'autorisation équivaut à avoir plus de`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`Des capacités plus puissantes, si vous insistez pour le faire, ce sera contre-productif. En supposant que le framework le permet, il y aura deux méthodes d'autorisation en même temps, l'une est l'autorisation de la fenêtre contextuelle et l'autre est l'autorisation de la page de saut. L'utilisateur doit effectuer deux autorisations, mais en fait il y a`MANAGE_EXTERNAL_STORAGE`L'autorisation est suffisante pour l'utilisation. À ce stade, vous avez peut-être une question en tête. Vous ne postulez pas.`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`Autorisations, pas en dessous d'Android 11`MANAGE_EXTERNAL_STORAGE`Ne serait-ce pas un problème avec cette autorisation ? Vous pouvez être assuré sur cette question, le cadre portera des jugements, si vous appliquez`MANAGE_EXTERNAL_STORAGE`Les autorisations seront ajoutées automatiquement dans les frameworks sous Android 11`READ_EXTERNAL_STORAGE`、`WRITE_EXTERNAL_STORAGE`Venez postuler, il ne sera donc pas indisponible en raison du manque d'autorisations dans la version inférieure.

    -   Si vous n'avez pas besoin des tests ci-dessus, vous pouvez appeler`unchecked`méthode pour désactiver, mais il convient de noter que je ne vous recommande pas de désactiver cette détection, car dans**mode de libération**Lorsqu'il est fermé, vous n'avez pas besoin de le fermer manuellement, et ce n'est que dans le**Mode débogage**Ces détections seront uniquement déclenchées.

-   La raison de ces problèmes est que nous ne connaissons pas très bien ces mécanismes, et si le framework ne le limite pas, toutes sortes de problèmes étranges apparaîtront. En tant qu'auteur du framework, non seulement vous êtes très pénible, mais aussi très douloureux que l'auteur du cadre. Parce que ces problèmes ne sont pas causés par le framework, mais par certaines opérations irrégulières de l'appelant. Je pense que la meilleure solution à ce problème est de faire en sorte que le framework effectue une vérification unifiée, car je suis l'auteur du framework et j'ai des connaissances sur l'application des autorisations.**Forte capacité professionnelle et expérience suffisante**, sachez quoi faire et quoi ne pas faire, afin de pouvoir intercepter une à une ces opérations salaces.

-   Lorsqu'il y a un problème avec la demande d'autorisation, voulez-vous que quelqu'un vienne vous rappeler et vous dire ce qui ne va pas ? Comment le corriger ? Cependant, ces XXPermissions l'ont fait. De tous les cadres de demande d'autorisation, je suis la première personne à le faire, je pense**faire un cadre**Il ne s'agit pas seulement de faire un bon travail de fonctions et de gérer des scénarios complexes, mais plus important encore, de**orientée vers les gens**, parce que le cadre lui-même est au service des gens, ce que nous devons faire n'est pas seulement de répondre aux besoins de chacun, mais aussi d'aider chacun à éviter les détours dans ce processus.

#### Faits saillants du cadre

-   Le premier cadre de demande d'autorisation pour Android 13

-   Le premier et unique cadre de demande d'autorisation pour toutes les versions d'Android

-   Concis et facile à utiliser : utilisez la méthode d'appel en chaîne, utilisez une seule ligne de code

-   Volume impressionnant : la fonction est la plus complète dans le même genre de cadre, mais le volume du cadre est le plus bas

-   S'adapter aux situations extrêmes : peu importe à quel point l'environnement est extrême pour demander une autorisation, le cadre est toujours solide

-   Attributs rétrocompatibles : de nouvelles autorisations peuvent être demandées dans l'ancien système normalement, et le cadre s'adaptera automatiquement sans que l'appelant ait besoin de s'adapter

-   Détecter automatiquement les erreurs : s'il y a une erreur, le framework lèvera activement une exception à l'appelant (uniquement jugé sous Debug, tue le bogue dans le berceau)

#### Autres projets open source de l'auteur

-   Centre de technologie Android :[Projet Android](https://github.com/getActivity/AndroidProject)![](https://img.shields.io/github/stars/getActivity/AndroidProject.svg)![](https://img.shields.io/github/forks/getActivity/AndroidProject.svg)

-   Version Kt de la plate-forme intermédiaire de technologie Android :[AndroidProject-Kotlin](https://github.com/getActivity/AndroidProject-Kotlin)![](https://img.shields.io/github/stars/getActivity/AndroidProject-Kotlin.svg)![](https://img.shields.io/github/forks/getActivity/AndroidProject-Kotlin.svg)

-   Cadre de pain grillé :[ToastUtils](https://github.com/getActivity/ToastUtils)![](https://img.shields.io/github/stars/getActivity/ToastUtils.svg)![](https://img.shields.io/github/forks/getActivity/ToastUtils.svg)

-   Cadre Web :[EasyHttp](https://github.com/getActivity/EasyHttp)![](https://img.shields.io/github/stars/getActivity/EasyHttp.svg)![](https://img.shields.io/github/forks/getActivity/EasyHttp.svg)

-   Cadre cartouche :[Barre de titre](https://github.com/getActivity/TitleBar)![](https://img.shields.io/github/stars/getActivity/TitleBar.svg)![](https://img.shields.io/github/forks/getActivity/TitleBar.svg)

-   Cadre de fenêtre flottant :[XToast](https://github.com/getActivity/XToast)![](https://img.shields.io/github/stars/getActivity/XToast.svg)![](https://img.shields.io/github/forks/getActivity/XToast.svg)

-   Cadre de forme :[ShapeView](https://github.com/getActivity/ShapeView)![](https://img.shields.io/github/stars/getActivity/ShapeView.svg)![](https://img.shields.io/github/forks/getActivity/ShapeView.svg)

-   Cadre de changement de langue :[Multi langues](https://github.com/getActivity/MultiLanguages)![](https://img.shields.io/github/stars/getActivity/MultiLanguages.svg)![](https://img.shields.io/github/forks/getActivity/MultiLanguages.svg)

-   Tolérance aux pannes d'analyse Gson :[GsonFactory](https://github.com/getActivity/GsonFactory)![](https://img.shields.io/github/stars/getActivity/GsonFactory.svg)![](https://img.shields.io/github/forks/getActivity/GsonFactory.svg)

-   Cadre de visualisation des journaux :[Logcat](https://github.com/getActivity/Logcat)![](https://img.shields.io/github/stars/getActivity/Logcat.svg)![](https://img.shields.io/github/forks/getActivity/Logcat.svg)

-   Adaptation de la version Android :[Adaptateur de version Android](https://github.com/getActivity/AndroidVersionAdapter)![](https://img.shields.io/github/stars/getActivity/AndroidVersionAdapter.svg)![](https://img.shields.io/github/forks/getActivity/AndroidVersionAdapter.svg)

-   Spécification du code Android :[AndroidCodeStandard](https://github.com/getActivity/AndroidCodeStandard)![](https://img.shields.io/github/stars/getActivity/AndroidCodeStandard.svg)![](https://img.shields.io/github/forks/getActivity/AndroidCodeStandard.svg)

-   Classement open source Android :[AndroidGithubBoss](https://github.com/getActivity/AndroidGithubBoss)![](https://img.shields.io/github/stars/getActivity/AndroidGithubBoss.svg)![](https://img.shields.io/github/forks/getActivity/AndroidGithubBoss.svg)

-   Plugins de la boutique Studio :[Plugins Studio](https://github.com/getActivity/StudioPlugins)![](https://img.shields.io/github/stars/getActivity/StudioPlugins.svg)![](https://img.shields.io/github/forks/getActivity/StudioPlugins.svg)

-   Une grande collection de packs d'expression :[emoji pa c ombre](https://github.com/getActivity/EmojiPackage)![](https://img.shields.io/github/stars/getActivity/EmojiPackage.svg)![](https://img.shields.io/github/forks/getActivity/EmojiPackage.svg)

-   Données Json des provinces et des villes :[ProvinceJson](https://github.com/getActivity/ProvinceJson)![](https://img.shields.io/github/stars/getActivity/ProvinceJson.svg)![](https://img.shields.io/github/forks/getActivity/ProvinceJson.svg)

#### Compte public WeChat : frère de la roue Android

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/official_ccount.png)

#### Groupe Q de la technologie Android : 10047167

#### Si vous pensez que ma bibliothèque open source vous a permis de gagner beaucoup de temps de développement, veuillez scanner le code QR ci-dessous pour donner une récompense, si vous pouvez donner une récompense 10.24 :monkey_face: ce serait trop :thumbsup:. Votre soutien m'encouragera à continuer à créer :octocat:

![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_ali.png)![](https://raw.githubusercontent.com/getActivity/Donate/master/picture/pay_wechat.png)

#### [Cliquez pour voir la liste des dons](https://github.com/getActivity/Donate)

## Licence

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
