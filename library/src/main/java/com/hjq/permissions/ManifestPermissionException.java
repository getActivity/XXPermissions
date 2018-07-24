package com.hjq.permissions;

/**
 * Created by HJQ on 2018-7-18.
 */
class ManifestPermissionException extends RuntimeException {

    ManifestPermissionException(String permission) {
        super(permission == null ?
                "No permissions are registered in the manifest file" :
                (permission + ": Permissions are not registered in the manifest file"));
    }
}