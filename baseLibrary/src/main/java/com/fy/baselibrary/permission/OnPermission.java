package com.fy.baselibrary.permission;

import java.util.List;

/**
 * 权限管理 回调接口
 * Created by fangs on 2018/8/27 15:36.
 */
public interface OnPermission {

    /**
     * 有权限被授予时回调（部分或全部授予）
     *
     * @param denyList    请求失败的权限组
     * @param isAll       是否全部授予了
     */
    void hasPermission(List<String> denyList, boolean isAll);

    /**
     * 权限被全部拒绝时回调
     *
     * @param denyList 请求失败的权限组
     */
    void noPermission(List<String> denyList);

}
