# 权限请求框架

![](XXPermissions.gif)

>[点击此处下载Demo](https://raw.githubusercontent.com/getActivity/XXPermissions/master/XXPermissions.apk)

#### 集成步骤

    dependencies {
        compile 'com.hjq:xxpermissions:1.0'
    }

#### 一句代码搞定权限请求，从未如此简单

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

