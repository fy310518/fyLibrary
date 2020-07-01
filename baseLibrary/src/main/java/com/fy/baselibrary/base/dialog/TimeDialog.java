package com.fy.baselibrary.base.dialog;

import android.support.annotation.ArrayRes;
import android.view.Gravity;
import android.view.View;

import com.fy.baselibrary.base.ViewHolder;
import com.fy.baselibrary.utils.ResUtils;
import com.fy.baselibrary.utils.TimeUtils;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.widget.wheel.OnItemSelectedListener;
import com.fy.baselibrary.widget.wheel.WheelView;
import com.fy.baselibrary.widget.wheel.simple.NumericWheelAdapter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * description 定义 公历 选择时间 弹窗抽象类，
 * 【注意：使用此抽象类  滚轮控件 个数 必须是 6 个（表示 年月日时分秒）】
 * Created by fangs on 2020/7/1 11:09.
 */
public abstract class TimeDialog extends CommonDialog {
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2200;
    private static final int DEFAULT_START_MONTH = 1;
    private static final int DEFAULT_END_MONTH = 12;
    private static final int DEFAULT_START_DAY = 1;
    private static final int DEFAULT_END_DAY = 31;

    private int startYear = DEFAULT_START_YEAR;
    private int endYear = DEFAULT_END_YEAR;
    private int startMonth = DEFAULT_START_MONTH;
    private int endMonth = DEFAULT_END_MONTH;
    private int startDay = DEFAULT_START_DAY;
    private int endDay = DEFAULT_END_DAY; //表示31天的


    protected WheelView[] wvs;
    protected WheelView wvYear, wvMonth, wvDay, wvHour, wvMin, wvSecond;
    //年 滑动停止监听；月 滑动停止监听；日 滑动停止监听；
    private OnItemSelectedListener yearSelectedListener, monthSelectedListener, daySelectedListener;

    private int currentYear, currentMonth, currentDay, currentHour, currentMin, currentSecond;//选中的 日期 数据

    private OnSelectTimeListener onSelectTimeListener;
    private boolean[] type = new boolean[]{true, true, true, false, false, false};//显示类型，默认显示：年月日;
    private @ArrayRes int label;//显示单位，如：年月日时分秒;
    private Calendar date;//初始显示时间
    private boolean isLoop;//是否循环【首尾相连：可以一直滚动】

    /**
     * 定义抽象方法，用来实例化 滚轮控件 wvYear, wvMonth, wvDay, wvHour, wvMin, wvSecond（年月日时分秒）
     * @param holder
     */
    protected abstract void initWheelView(ViewHolder holder);

    /**
     * 定义抽象方法，用来 设置 滚轮控件其它 属性
     * @param wheelView 滚轮控件
     */
    protected abstract void setOtherAttribute(WheelView wheelView);

    /**
     * 定义抽象方法，子类自行 控制 相关业务
     */
    protected abstract void customerView(ViewHolder holder);

    @Override
    public void convertView(ViewHolder holder, CommonDialog dialog) {
        initWheelView(holder);
        if (null == wvYear || null == wvMonth || null == wvDay || null == wvHour || null == wvMin || null == wvSecond) {
            throw new UnsupportedOperationException("Six wheel views cannot be null");
        }

        wvs = new WheelView[]{wvYear, wvMonth, wvDay, wvHour, wvMin, wvSecond};

        initData();
        baseSet();
        setWvListener();

        customerView(holder);
    }

    //设置 联动监听
    private void setWvListener(){
        yearSelectedListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                currentYear = startYear + index;
                monthSelectedListener.onItemSelected(wvMonth.getCurrentItem());
            }
        };

        monthSelectedListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                currentMonth = startMonth + index;
                daySelectedListener.onItemSelected(wvDay.getCurrentItem());
            }
        };

        daySelectedListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                currentDay = startDay + index;

                int endDay = setDayNum();//当月 最大天数
                wvDay.setAdapter(new NumericWheelAdapter(startDay, endDay));

                int position = wvDay.getCurrentItem();
                if (position > endDay){
                    position = endDay - 1;
                }

                wvDay.setCurrentItem(position);
            }
        };

        wvYear.setOnItemSelectedListener(yearSelectedListener);
        wvMonth.setOnItemSelectedListener(monthSelectedListener);
        wvDay.setOnItemSelectedListener(daySelectedListener);

        wvHour.setOnItemSelectedListener(index -> currentHour = index);
        wvMin.setOnItemSelectedListener(index -> currentMin = index);
        wvSecond.setOnItemSelectedListener(index -> currentSecond = index);
    }

    // 公历
    private void initData(){
        if (date == null) {//计算时间 数据 年 月 日 时 分 秒
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            currentYear = calendar.get(Calendar.YEAR);
            currentMonth = calendar.get(Calendar.MONTH) + 1;
            currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            currentMin = calendar.get(Calendar.MINUTE);
            currentSecond = calendar.get(Calendar.SECOND);
        } else {
            currentYear = date.get(Calendar.YEAR);
            currentMonth = date.get(Calendar.MONTH) + 1;
            currentDay = date.get(Calendar.DAY_OF_MONTH);
            currentHour = date.get(Calendar.HOUR_OF_DAY);
            currentMin = date.get(Calendar.MINUTE);
            currentSecond = date.get(Calendar.SECOND);
        }

        setDate();
        setDayNum();
//        日
        wvDay.setAdapter(new NumericWheelAdapter(startDay, endDay));
        wvDay.setCurrentItem(currentDay - startDay);

        setTime(currentHour, currentMin, currentSecond);
    }

    private void setDate(){
//        年
        wvYear.setAdapter(new NumericWheelAdapter(startYear, endYear));
        wvYear.setCurrentItem(currentYear - startYear);

//        月
        wvMonth.setAdapter(new NumericWheelAdapter(startMonth, endMonth));
        wvMonth.setCurrentItem(currentMonth - 1);//月份 显示时候【下标 减 一】
    }

    //设置 月份的 天数【大小月，润年 平年】
    private int setDayNum(){
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};
        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        if (list_big.contains(String.valueOf(currentMonth))){// 大月份【31天】
            endDay = 31;
        } else if (list_little.contains(String.valueOf(currentMonth))){// 小月份【30天】
            endDay = 30;
        } else {
            boolean leapYear = (currentYear % 4 == 0 && currentYear % 100 != 0) || currentYear % 400 == 0;//计算是否闰年
            endDay = leapYear ? 29 : 28;
        }

        return endDay;
    }

    private void setTime(int hours, int minute, int seconds){
//        时
        wvHour.setAdapter(new NumericWheelAdapter(0, 23));
        wvHour.setCurrentItem(hours);

//        分
        wvMin.setAdapter(new NumericWheelAdapter(0, 59));
        wvMin.setCurrentItem(minute);

//        秒
        wvSecond.setAdapter(new NumericWheelAdapter(0, 59));
        wvSecond.setCurrentItem(seconds);
    }

    //基础属性设置，其它属性 自行在 子类 实现
    private void baseSet(){
        String[] labelStr = new String[]{"年", "月", "日", "时", "分", "秒"};
        if (label != 0) labelStr = ResUtils.getStrArray(label);

        for (int i = 0; i < wvs.length; i++){
            wvs[i].setGravity(Gravity.CENTER);
            wvs[i].setLabel(labelStr[i]);//设置 单位
            wvs[i].setItemsVisibleCount(7);//设置最大可见数目
            wvs[i].setCyclic(isLoop);//设置是否 循环
            wvs[i].setVisibility(type[i] ? View.VISIBLE : View.GONE);//设置是否 显示

            setOtherAttribute(wvs[i]);
        }
    }


    /**  设置基础属性 */
    //根据传递的 时间毫秒 创建 Calendar，并赋值给 date
    public TimeDialog setDate(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        this.date = calendar;
        return this;
    }

    //设置单位，如：年月日时分秒
    public TimeDialog setLabel(@ArrayRes int label) {
        this.label = label;
        return this;
    }

    //设置显示类型，默认显示：年月日
    public TimeDialog setType(boolean[] type) {
        this.type = type;
        return this;
    }

    public TimeDialog setLoop(boolean loop) {
        isLoop = loop;
        return this;
    }

    //设置 选择时间，回调接口
    public TimeDialog setOnSelectTimeListener(OnSelectTimeListener onSelectTimeListener) {
        this.onSelectTimeListener = onSelectTimeListener;
        return this;
    }

    /**
     * 执行回调方法
     */
    protected void runOnTime(){
        String month = currentMonth > 9 ? "" + currentMonth : "0" + currentMonth;
        String day   = currentDay   > 9 ? "" + currentDay   : "0" + currentDay;

        String timeStr = currentYear + "-" + month + "-" + day + " " + currentHour + ":" + currentMin + ":" + currentSecond;
        L.e("时间选择", timeStr);

        if (null != onSelectTimeListener) onSelectTimeListener.onTime(TimeUtils.timeString2long(timeStr, "yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 选择时间，回调接口
     */
    public interface OnSelectTimeListener{
        /**
         * 选择时间，回调方法
         * @param millis 返回的 时间戳【根据时间戳 生成不同格式的 时间】
         */
        void onTime(long millis);
    }
}
