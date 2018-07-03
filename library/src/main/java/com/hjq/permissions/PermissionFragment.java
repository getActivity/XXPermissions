package com.hjq.permissions;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by HJQ on 2018-6-15.
 */
public final class PermissionFragment extends Fragment {

    private static final String PERMISSIONS = "permissions";
    private static final String REQUEST_CODE ="request_code";

    public static PermissionFragment newInstant(ArrayList<String> permissions, int requestCode) {
        PermissionFragment fragment = new PermissionFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(PERMISSIONS, permissions);
        bundle.putInt(REQUEST_CODE, requestCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (PermissionUtils.isOverMarshmallow()) {
            ArrayList<String> permissions = getArguments().getStringArrayList(PERMISSIONS);
            requestPermissions(permissions.toArray(new String[permissions.size() - 1]), getArguments().getInt(REQUEST_CODE));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        XXPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    public void request(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, this.getClass().getName());
        ft.commit();
    }
}