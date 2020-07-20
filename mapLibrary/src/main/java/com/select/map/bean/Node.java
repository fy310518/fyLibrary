package com.select.map.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * description 省市 节点实体类
 * Created by fangs on 2020/7/14 15:42.
 */
public class Node {

    //当前id
    private int id;
    //父id
    private int pid;
    //等级
    private int level;
    //数据，此处放置的对应item的数据，在使用时，一定要直到存放的什么数据，否则强转出错
    private Object data;
    //父节点实例
    private Node parentNode;
    //子节点集合
    private List<Node> subNodeList;
    //展开状态
    private boolean isExpand;

    //-------------------------------这下面成员变量是扩展用，根据需求变更，主要的是在上面那些成员变量必须要-----------------------------------------
    //百分比
    private int percent;
    //是否可见进度条
    private boolean isVisible;
    //加载状态
    private boolean isLoading;
    //下载完成状态
    private boolean isDowned;

    public Node(int id, int pid) {
        this.id = id;
        this.pid = pid;
        this.parentNode = null;
        this.data = null;
        this.subNodeList = new ArrayList<>();
        this.isExpand = false;
        this.percent = 0;
        this.isVisible = false;
        this.isLoading = false;
        this.isDowned = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public List<Node> getSubNodeList() {
        if (subNodeList == null) {
            return new ArrayList<>();
        }
        return subNodeList;
    }

    public void setSubNodeList(List<Node> subNodeList) {
        this.subNodeList = subNodeList;
    }

    public boolean isSubNode() {
        return subNodeList.size() == 0;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isDowned() {
        return isDowned;
    }

    public void setDowned(boolean downed) {
        isDowned = downed;
    }

}
