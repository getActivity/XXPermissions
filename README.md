# 权限请求框架

![](XXPermissions.gif)

#### 集成步骤

> 在Module工程的build.gradle文件中添加依赖

    dependencies {
        compile 'com.hjq:xxpermissions:1.0'
    }

> 如果导入失败，请检查在Project工程的build.gradle文件的配置

    allprojects {

        repositories {
            //添加对JCenter仓库的支持
            jcenter()
            ......
        }
        ......
    }

#### 一句代码搞定权限请求，从未如此简单

    XXPermissions.with(this)
            .permission(Permission.Group.STORAGE)
            .request(new OnPermission() {

                @Override
                public void hasPermission(List<String> granted) {
                    
                }

                @Override
                public void noPermission(List<String> denied, boolean permanent) {
                    
                }
            });


