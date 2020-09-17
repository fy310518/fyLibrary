package com.fy.baselibrary.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ListView;

import com.fy.baselibrary.R;
import com.fy.baselibrary.aop.annotation.NeedPermission;
import com.fy.baselibrary.application.ioc.ConfigUtils;
import com.fy.baselibrary.base.ViewHolder;
import com.fy.baselibrary.base.dialog.CommonDialog;
import com.fy.baselibrary.base.dialog.DialogConvertListener;
import com.fy.baselibrary.base.dialog.NiceDialog;
import com.fy.baselibrary.base.fragment.BaseFragment;
import com.fy.baselibrary.utils.AppUtils;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.drawable.ShapeBuilder;
import com.fy.baselibrary.utils.os.OSUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 动态权限管理 fragment
 * Created by fangs on 2018/8/27 15:36.
 */
public class PermissionFragment extends BaseFragment {

    public final static String KEY_PERMISSIONS_ARRAY = "key_permission_array";
    public final static String KEY_FIRST_MESSAGE = "key_first_message";
    public final static String KEY_ALWAYS_MESSAGE = "key_always_message";

    /** 权限请求 状态码 */
    public final static int PERMISSION_REQUEST_CODE = 0x01;

    /** 权限请求成功 状态码 */
    public final static int CALL_BACK_RESULT_CODE_SUCCESS = 0x02;
    /** 权限请求失败 状态码*/
    public final static int CALL_BACK_RESULE_CODE_FAILURE = 0x03;

    private String appName;
    /** 第一次拒绝该权限的提示信息。 */
    private String mFirstRefuseMessage;
    /** 永久拒绝权限提醒的提示信息 */
    private String mAlwaysRefuseMessage;

    private String[] mPermissions;

    private boolean isToSettingPermission;

    private OnPermission call;

    @Override
    protected int setContentLayout() {
        return 0;
    }

    @Override
    protected void baseInit() {
        appName = AppUtils.getAppName(getContext(), AppUtils.getLocalPackageName());
        mFirstRefuseMessage = getString(R.string.default_always_message);

        Bundle bundle = getArguments();
        if (null != bundle) {
            mPermissions = bundle.getStringArray(KEY_PERMISSIONS_ARRAY);
            mFirstRefuseMessage = bundle.getString(KEY_FIRST_MESSAGE);
            mAlwaysRefuseMessage = bundle.getString(KEY_ALWAYS_MESSAGE);
        }

        if (TextUtils.isEmpty(mFirstRefuseMessage)) {
            mFirstRefuseMessage = appName + getString(R.string.default_first_message);
        } else {
            mFirstRefuseMessage = appName + mFirstRefuseMessage;
        }


        if (TextUtils.isEmpty(mAlwaysRefuseMessage)) {
            mAlwaysRefuseMessage = appName + getString(R.string.default_always_message);
        } else {
            mAlwaysRefuseMessage = appName + mAlwaysRefuseMessage;
        }

        checkPermission(mPermissions);
    }

    @Override
    public void onResume() {
        super.onResume();
        //如果是从权限设置界面回来，重新检查权限
        if (isToSettingPermission) {
            isToSettingPermission = false;
            checkPermission(mPermissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && null != grantResults && grantResults.length > 0) {
            List<Integer> failurePermissionCount = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    failurePermissionCount.add(grantResults[i]);
                }
            }

            if (failurePermissionCount.size() == 0) {//权限请求失败数为0，则全部成功
                permissionEnd(CALL_BACK_RESULT_CODE_SUCCESS, true);
            } else {
                //失败
                List<String> rationaleList = PermissionUtils.getShouldRationaleList(getActivity(), permissions);
                if (null != rationaleList && rationaleList.size() > 0) {
                    if (rationaleList.size() < permissions.length){
                        showPermissionDialog(rationaleList, true, false);//部分永久拒绝
                    } else {
                        showPermissionDialog(rationaleList, true, true);//全部永久拒绝
                    }
                } else {
                    showPermissionDialog(Arrays.asList(permissions), false, true);
                }
            }
        }
    }

    /** 请求多个权限 */
    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission(String... permissions) {
        if (null != permissions) {
            if(ConfigUtils.isDEBUG()) PermissionUtils.checkPermissions(getActivity(), permissions);

            List<String> requestPermission = PermissionUtils.getRequestPermissionList(getContext(), permissions);

            // 是否需要申请特殊权限
            boolean requestSpecialPermission = false;
            // 判断当前是否包含特殊权限
            if (PermissionUtils.containsSpecialPermission(requestPermission)) {
                if (requestPermission.contains(Permission.MANAGE_EXTERNAL_STORAGE) && !PermissionUtils.hasStoragePermission(getActivity())) {
                    // 当前必须是 Android 11 及以上版本，因为 hasStoragePermission 在旧版本上是拿旧权限做的判断，所以这里需要多判断一次版本
                    if (OSUtils.isAndroid11()) {
                        requestSpecialPermission = isToSettingPermission = true;
                        // 存储权限设置界面
                        showSpecialPermissionDialog(Permission.MANAGE_EXTERNAL_STORAGE);
                    } else {
                        requestPermission.remove(Permission.MANAGE_EXTERNAL_STORAGE);
                        requestPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        requestPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }

                if (requestPermission.contains(Permission.REQUEST_INSTALL_PACKAGES) && !PermissionUtils.hasInstallPermission(getActivity())) {
                    requestSpecialPermission = isToSettingPermission = true;
                    // 跳转到安装权限设置界面
                    showSpecialPermissionDialog(Permission.REQUEST_INSTALL_PACKAGES);
                }

                if (requestPermission.contains(Permission.SYSTEM_ALERT_WINDOW) && !PermissionUtils.hasWindowPermission(getActivity())) {
                    requestSpecialPermission = isToSettingPermission = true;
                    // 跳转到悬浮窗设置页面
                    showSpecialPermissionDialog(Permission.SYSTEM_ALERT_WINDOW);
                }

                if (requestPermission.contains(Permission.NOTIFICATION_SERVICE) && !PermissionUtils.hasNotifyPermission(getActivity())) {
                    requestSpecialPermission = isToSettingPermission = true;
                    // 跳转到通知栏权限设置页面
                    showSpecialPermissionDialog(Permission.NOTIFICATION_SERVICE);
                }

                if (requestPermission.contains(Permission.WRITE_SETTINGS) && !PermissionUtils.hasSettingPermission(getActivity())) {
                    requestSpecialPermission = isToSettingPermission = true;
                    // 跳转到系统设置权限设置页面
                    showSpecialPermissionDialog(Permission.WRITE_SETTINGS);
                }
            }

            // 当前必须没有跳转到悬浮窗或者安装权限界面
            if (!requestSpecialPermission) {
                if (requestPermission.size() > 0) {
                    requestPermissions(requestPermission.toArray(new String[0]), PERMISSION_REQUEST_CODE);
                } else {
                    permissionEnd(CALL_BACK_RESULT_CODE_SUCCESS, true);
                }
            }
        } else {
            permissionEnd(CALL_BACK_RESULT_CODE_SUCCESS, true);
        }
    }


    /**
     * 调用系统弹窗请求权限
     * @param isRefuse     是否勾选了（“不在提示”多选框）
     */
    public void onSurePermission(boolean isRefuse) {
        if (isRefuse) {
            isToSettingPermission = true;
            List<String> rationaleList = PermissionUtils.getShouldRationaleList(getActivity(), mPermissions);
            PermissionUtils.startPermissionActivity(this, rationaleList);
        } else {
            checkPermission(mPermissions);
        }
    }

    /**
     * 权限请求结束
     * @param resultCode
     * @param isStatus   是否全部成功或者是否全部失败（根据第一个参数判断：如参数1 表示“成功”状态码，则参数2表示 是否全部成功）
     */
    public void permissionEnd(int resultCode, boolean isStatus) {
        if (null != call){
            if (resultCode == CALL_BACK_RESULT_CODE_SUCCESS && isStatus) {
                call.hasPermission(Arrays.asList(mPermissions), isStatus);
            } else if (resultCode == CALL_BACK_RESULE_CODE_FAILURE && isStatus){
                call.noPermission(Arrays.asList(mPermissions));
            } else {
                call.hasPermission(PermissionUtils.getRequestPermissionList(getContext(), mPermissions), isStatus);
            }
        }
    }


    /**
     * 危险权限请求失败 给予用户提示 自定义弹窗
     * @param isAlwaysRefuse    是否勾选了（“不在提示”多选框）
     * @param isAllSuccess      权限请求是否 全部成功
     */
    public void showPermissionDialog(List<String> rationaleList, final boolean isAlwaysRefuse, boolean isAllSuccess) {
        NiceDialog.init()
                .setLayoutId(R.layout.dialog_permission)
                .setDialogConvertListener(new DialogConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, CommonDialog dialog) {
                        ShapeBuilder.create()
                                .solid(R.color.white)
                                .radius(36)
                                .setBackBg(holder.getView(R.id.permissionLayout));

                        //生成 权限组 列表，然后传给 listView 适配器
                        List<String> listData = new ArrayList<>();
                        for (String permission : rationaleList){
                            String group = PermissionUtils.getPermissionGroup(permission);
                            if (!TextUtils.isEmpty(group) && !listData.contains(group)) listData.add(group);
                        }

                        ListView lvRefusePermission = holder.getView(R.id.lvRefusePermission);
                        PermissionTipsListAdapter permissionTipsListAdapter = new PermissionTipsListAdapter(getContext(), listData);
                        lvRefusePermission.setAdapter(permissionTipsListAdapter);

                        holder.setText(R.id.tvPermissionDescribe, isAlwaysRefuse ? mAlwaysRefuseMessage : mFirstRefuseMessage);

                        holder.setText(R.id.tvpermissionConfirm, isAlwaysRefuse ? R.string.set : R.string.ok);
                        holder.setOnClickListener(R.id.tvpermissionConfirm, v -> {
                            onSurePermission(isAlwaysRefuse);
                            dialog.dismiss(false);
                        });

                        holder.setText(R.id.tvPermissionCancel, R.string.cancel);
                        holder.setOnClickListener(R.id.tvPermissionCancel, v -> {
                            permissionEnd(CALL_BACK_RESULE_CODE_FAILURE, isAllSuccess);
                            dialog.dismiss(false);
                        });
                    }
                })
                .setWidthPixels(-1)
                .setGravity(Gravity.BOTTOM)
                .setAnim(R.style.AnimUp)
                .setKeyBack(true)
                .show(getFragmentManager(), "PermissionFragment");
    }

    /**
     * 特殊权限 申请弹窗
     * @param specialPermission
     */
    public void showSpecialPermissionDialog(String specialPermission) {
        String[] info = Permission.specialPermission.get(specialPermission);
        if (null == info) {
            removePermission(specialPermission);
            return;
        }

        NiceDialog.init()
                .setLayoutId(R.layout.dialog_permission)
                .setDialogConvertListener(new DialogConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, CommonDialog dialog) {
                        ShapeBuilder.create()
                                .solid(R.color.white)
                                .radius(36)
                                .setBackBg(holder.getView(R.id.permissionLayout));

                        holder.setVisibility(R.id.lvRefusePermission, false);
                        holder.setVisibility(R.id.txtSpecialPermission, true);

                        String title = ResUtils.getReplaceStr(R.string.default_special_permission, appName, info[0]);
                        holder.setText(R.id.tvPermissionDescribe, title);//标题

                        String content = ResUtils.getReplaceStr(R.string.default_special_permission_content, info[0], appName, info[1]);
                        holder.setText(R.id.txtSpecialPermission, content);//特殊权限用途 说明

                        holder.setText(R.id.tvpermissionConfirm, R.string.set);
                        holder.setOnClickListener(R.id.tvpermissionConfirm, v -> {
                            List<String> rationaleList = PermissionUtils.getShouldRationaleList(getActivity(), specialPermission);
                            PermissionUtils.startPermissionActivity(PermissionFragment.this, rationaleList);

                            removePermission(specialPermission);
                            dialog.dismiss(false);
                        });

                        holder.setText(R.id.tvPermissionCancel, R.string.cancel);
                        holder.setOnClickListener(R.id.tvPermissionCancel, v -> {
                            permissionEnd(CALL_BACK_RESULE_CODE_FAILURE, false);
                            dialog.dismiss(false);
                        });
                    }
                })
                .setWidthPixels(-1)
                .setGravity(Gravity.BOTTOM)
                .setAnim(R.style.AnimUp)
                .setKeyBack(true)
                .show(getFragmentManager(), "PermissionFragment");
    }

    //从全局 mPermissions 中移除 指定的权限
    private void removePermission(String permission){
        List<String> tempList = new ArrayList<>(Arrays.asList(mPermissions));
        tempList.remove(permission);
        String[] tempStrArray = new String[tempList.size()];
        tempList.toArray(tempStrArray);
        mPermissions = tempStrArray;
    }

    /**
     * 准备请求权限
     * @param object
     * @param needPermission
     */
    public static void newInstant(Object object, NeedPermission needPermission, OnPermission callListener) {
        Bundle bundle = new Bundle();
        bundle.putStringArray(KEY_PERMISSIONS_ARRAY, needPermission.value());
        if (needPermission.firstRefuseMsg() != 0)bundle.putString(KEY_FIRST_MESSAGE, ResUtils.getStr(needPermission.firstRefuseMsg()));
        if (needPermission.alwaysRefuseMsg() != 0)bundle.putString(KEY_ALWAYS_MESSAGE, ResUtils.getStr(needPermission.alwaysRefuseMsg()));

        PermissionFragment fragment = new PermissionFragment();
        fragment.call = callListener;
        fragment.setArguments(bundle);

        FragmentManager manager = null;
        if (object instanceof AppCompatActivity) {
            AppCompatActivity act = ((AppCompatActivity)object);
            manager = act.getSupportFragmentManager();

        } else if (object instanceof Fragment) {
            Fragment fm = ((Fragment)object);
            manager = fm.getFragmentManager();
        }

        assert manager != null;
        manager.beginTransaction().add(fragment, "PermissionFragment").commitAllowingStateLoss();
    }

}
