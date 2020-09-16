package com.fy.baselibrary.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fy.baselibrary.R;
import com.fy.baselibrary.base.CommonAdapter;

import java.util.List;

/**
 * 给用户说明 申请的权限 列表 适配器
 * Created by fangs on 2017/6/30.
 */
public class PermissionTipsListAdapter extends CommonAdapter<String> {

    PackageManager packageManager;

    public PermissionTipsListAdapter(Context context, List<String> data) {
        super(context, data);

        packageManager = context.getPackageManager();
    }

    @Override
    public View getView(int position, View arg1, ViewGroup arg2) {
        View itemView = getViewCache().get(position);

        if (null == itemView) {
            final String permissionGroup = getData().get(position);
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_permission_item, null);

            TextView permissionText = itemView.findViewById(R.id.permissionText);
            ImageView iv_cover = itemView.findViewById(R.id.permissionIcon);
            try {
                permissionText.setText(packageManager.getPermissionGroupInfo(permissionGroup, 0).labelRes);
                iv_cover.setImageResource(packageManager.getPermissionGroupInfo(permissionGroup, 0).icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            itemView.setTag(permissionGroup);
            getViewCache().put(position, itemView);
        }

        return itemView;
    }

}
