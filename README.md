# 权限请求框架

![](XXPermissions.gif)

>[点击此处下载Demo](https://raw.githubusercontent.com/getActivity/XXPermissions/master/XXPermissions.apk)，[博客地址](https://www.jianshu.com/p/c69ff8a445ed)

#### 集成步骤

    dependencies {
        compile 'com.hjq:xxpermissions:1.3'
    }

#### 一句代码搞定权限请求，从未如此简单

> 若不指定请求的权限，会自动获取清单文件中的危险权限进行请求

    XXPermissions.with(this)
            .permission(Permission.Group.STORAGE)
            .request(new OnPermission() {

                @Override
                public void hasPermission(List<String> granted) {
                    
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
