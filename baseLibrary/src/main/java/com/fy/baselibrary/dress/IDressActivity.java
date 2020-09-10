package com.fy.baselibrary.dress;

/**
 * description 定义 是否使用 颜色处理 接口
 * 如果 activity 不实现此接口 默认 使用硬件加速【View.LAYER_TYPE_HARDWARE】；
 * 但是某些特殊功能【地图，自定义相机相关】就会有问题
 * Created by fangs on 2020/9/10 11:11.
 */
public interface IDressActivity {

    /**
     * 是否给当前界面 进行颜色处理
     */
    boolean isTint();
}
