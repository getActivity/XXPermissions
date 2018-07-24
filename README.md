# 权限请求框架

![](XXPermissions.gif)

>[点击此处下载Demo](https://raw.githubusercontent.com/getActivity/XXPermissions/master/XXPermissions.apk)，[博客地址](https://www.jianshu.com/p/c69ff8a445ed)

#### 集成步骤

    dependencies {
        compile 'com.hjq:xxpermissions:2.0'
    }

#### 一句代码搞定权限请求，从未如此简单

> 支持多个权限组进行请求，若不指定请求的权限，会自动获取清单文件中的危险权限进行请求

    XXPermissions.with(this)
            .permission(Permission.Group.STORAGE)
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

* 不指定权限就自动获取清单文件上的危险权限进行申请

* 如果动态申请的权限没有在清单文件中注册会抛出异常

* 支持大部分国产手机直接跳转到具体的权限设置页面

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
