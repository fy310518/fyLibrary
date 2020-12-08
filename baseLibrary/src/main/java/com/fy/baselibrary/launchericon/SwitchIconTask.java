package com.fy.baselibrary.launchericon;

/**
 * description 切换图标任务 实体类
 * Created by fangs on 2020/12/7 17:34.
 */
public class SwitchIconTask {

    private String launcherComponentClassName;  // 启动器组件类名
    private String aliasComponentClassName;     // 别名组件类名
    private long presetTime;                    // 预设时间 【预设时间 要 小于 过期时间】
    private long outDateTime;                   // 过期时间

    public SwitchIconTask(String launcherComponentClassName, String aliasComponentClassName, long presetTime, long outDateTime) {
        this.launcherComponentClassName = launcherComponentClassName;
        this.aliasComponentClassName = aliasComponentClassName;
        this.presetTime = presetTime;
        this.outDateTime = outDateTime;
    }

    public String getLauncherComponentClassName() {
        return launcherComponentClassName == null ? "" : launcherComponentClassName;
    }

    public void setLauncherComponentClassName(String launcherComponentClassName) {
        this.launcherComponentClassName = launcherComponentClassName;
    }

    public String getAliasComponentClassName() {
        return aliasComponentClassName == null ? "" : aliasComponentClassName;
    }

    public void setAliasComponentClassName(String aliasComponentClassName) {
        this.aliasComponentClassName = aliasComponentClassName;
    }

    public long getPresetTime() {
        return presetTime;
    }

    public void setPresetTime(long presetTime) {
        this.presetTime = presetTime;
    }

    public long getOutDateTime() {
        return outDateTime;
    }

    public void setOutDateTime(long outDateTime) {
        this.outDateTime = outDateTime;
    }
}
