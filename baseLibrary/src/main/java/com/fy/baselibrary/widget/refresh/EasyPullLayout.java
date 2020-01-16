package com.fy.baselibrary.widget.refresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.fy.baselibrary.R;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * 支持上下左右 拉动刷新控件
 * http://blog.csdn.net/u012199331/article/details/77014607（核心来源）
 */
public class EasyPullLayout extends ViewGroup {

    private int trigger_offset_left = 0;
    private int trigger_offset_top = 0;
    private int trigger_offset_right = 0;
    private int trigger_offset_bottom = 0;
    private int max_offset_left = 0;
    private int max_offset_top = 0;
    private int max_offset_right = 0;
    private int max_offset_bottom = 0;
    private boolean fixed_content_left = false;
    private boolean fixed_content_top = false;
    private boolean fixed_content_right = false;
    private boolean fixed_content_bottom = false;
    private long roll_back_duration = 0L; // default 300
    private float sticky_factor = 0f; // default 0.66f (0f~1f)

    private String animLeftClass;
    private String animTopClass;
    private String animRightClass;
    private String animBottomClass;


    /** key对应View，value对应View的一些参数 */
    private HashMap<View, ChildViewAttr> childViews = new HashMap<>(4);

    private float downX = 0f;
    private float downY = 0f;
    private float offsetX = 0f;
    private float offsetY = 0f;
    private float lastPullFraction = 0f;

    private int currentType = TYPE_NONE;
    private int currentState = STATE_IDLE;

    private ValueAnimator horizontalAnimator = null;
    private ValueAnimator verticalAnimator = null;

    public final static int TYPE_NONE = -1; // not being controlled by EasyPullLayout
    public final static int TYPE_EDGE_LEFT = 0;
    public final static int TYPE_EDGE_TOP = 1;
    public final static int TYPE_EDGE_RIGHT = 2;
    public final static int TYPE_EDGE_BOTTOM = 3;
    public final static int TYPE_CONTENT = 4;
    public final static int TYPE_STATUS_CONTENT = 5;

    private final static int STATE_IDLE = 0;
    private final static int STATE_ROLLING = 1;
    private final static int STATE_TRIGGERING = 2;

    private int pullTypeMask = PULL_TYPE_LEFT | PULL_TYPE_TOP | PULL_TYPE_RIGHT | PULL_TYPE_BOTTOM;
    public static final int PULL_TYPE_LEFT = 1 << 0;
    public static final int PULL_TYPE_TOP = 1 << 1;
    public static final int PULL_TYPE_RIGHT = 1 << 2;
    public static final int PULL_TYPE_BOTTOM = 1 << 3;

    public static final int ROLL_BACK_TYPE_LEFT = 0;
    public static final int ROLL_BACK_TYPE_TOP = 1;
    public static final int ROLL_BACK_TYPE_RIGHT = 2;
    public static final int ROLL_BACK_TYPE_BOTTOM = 3;

    private OnEdgeListener onEdgeListener;
    private OnPullListenerAdapter onPullListenerAdapter;
    private OnRefreshLoadMoreListener onRefreshLoadMoreListener;
    private OnRefreshListener onRefreshListener;
    private OnLoadMoreListener onLoadMoreListener;

    public EasyPullLayout(Context context) {
        this(context, null);
    }

    public EasyPullLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyPullLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EasyPullLayout, defStyleAttr, 0);

        trigger_offset_left = a.getDimensionPixelOffset(R.styleable.EasyPullLayout_trigger_offset_left,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1f, context.getResources().getDisplayMetrics()));
        trigger_offset_top = a.getDimensionPixelOffset(R.styleable.EasyPullLayout_trigger_offset_top,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1f, context.getResources().getDisplayMetrics()));
        trigger_offset_right = a.getDimensionPixelOffset(R.styleable.EasyPullLayout_trigger_offset_right,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1f, context.getResources().getDisplayMetrics()));
        trigger_offset_bottom = a.getDimensionPixelOffset(R.styleable.EasyPullLayout_trigger_offset_bottom,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1f, context.getResources().getDisplayMetrics()));

        max_offset_left = a.getDimensionPixelOffset(R.styleable.EasyPullLayout_max_offset_left,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1f, context.getResources().getDisplayMetrics()));
        max_offset_top = a.getDimensionPixelOffset(R.styleable.EasyPullLayout_max_offset_top,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1f, context.getResources().getDisplayMetrics()));
        max_offset_right = a.getDimensionPixelOffset(R.styleable.EasyPullLayout_max_offset_right,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1f, context.getResources().getDisplayMetrics()));
        max_offset_bottom = a.getDimensionPixelOffset(R.styleable.EasyPullLayout_max_offset_bottom,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1f, context.getResources().getDisplayMetrics()));

        fixed_content_left = a.getBoolean(R.styleable.EasyPullLayout_fixed_content_left, false);
        fixed_content_top = a.getBoolean(R.styleable.EasyPullLayout_fixed_content_top, false);
        fixed_content_right = a.getBoolean(R.styleable.EasyPullLayout_fixed_content_right, false);
        fixed_content_bottom = a.getBoolean(R.styleable.EasyPullLayout_fixed_content_bottom, false);

        roll_back_duration = a.getInteger(R.styleable.EasyPullLayout_roll_back_duration, 300);
        sticky_factor = a.getFloat(R.styleable.EasyPullLayout_sticky_factor, 0.66f);
        sticky_factor = sticky_factor < 0f ? 0f : sticky_factor > 1f ? 1f : sticky_factor; // limit 0f~1f

        animLeftClass   = a.getString(R.styleable.EasyPullLayout_pull_animator_left);
        animTopClass    = a.getString(R.styleable.EasyPullLayout_pull_animator_top);
        animRightClass  = a.getString(R.styleable.EasyPullLayout_pull_animator_right);
        animBottomClass = a.getString(R.styleable.EasyPullLayout_pull_animator_bottom);
        a.recycle();

        init();
        addOnPullListenerAdapter();
    }

    /**
     * 将加载动画类路径 转换为对应的加载动画view 并添加到 EasyPullLayout
     */
    private void init(){
        SparseArray<String> animArray = new SparseArray<>();
        animArray.put(TYPE_EDGE_LEFT, animLeftClass);
        animArray.put(TYPE_EDGE_TOP, animTopClass);
        animArray.put(TYPE_EDGE_RIGHT, animRightClass);
        animArray.put(TYPE_EDGE_BOTTOM, animBottomClass);

        for (int i = 0; i < animArray.size(); i++){
            String animClass = animArray.valueAt(i);
            if (!TextUtils.isEmpty(animClass)){
                try {
                    Class<?> cls = Class.forName(animClass);
                    Constructor constructor = cls.getConstructor(Context.class);
                    RefreshAnimView view = (RefreshAnimView) constructor.newInstance(getContext());
                    LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                    layoutParams.type = animArray.keyAt(i);

                    view.setLayoutParams(layoutParams);
                    addView(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        //此处重写 addView 方法，为了动态添加子 view 时，把 view 保存在childViews 中，
        //解决 动态添加的子view 不显示问题
        if (params instanceof LayoutParams){
            LayoutParams lp = (LayoutParams) params;
            if (lp.type == TYPE_STATUS_CONTENT){
                childViews.put(child, new ChildViewAttr());// 存储子View
            }
        }

        addView(child, -1, params);
    }

    @Override//加载完xml后回调
    protected void onFinishInflate() {
        super.onFinishInflate();

        final int childCount = getChildCount();
        int i = 0;
        while (i < childCount) {
            View child = getChildAt(i++);

            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (null != getByType(lp.type)) {
                throw new IllegalArgumentException("Each child type can only be defined once!");
            } else {
                childViews.put(child, new ChildViewAttr());// 存储子View
            }
        }


        // 确保有一个子View的layout_type为content
        final View contentView = getByType(TYPE_CONTENT);
        if (null == contentView) throw new IllegalArgumentException("Child type \"content\" must be defined!");

        // 设置默认的OnEdgeListener，可以被覆盖
        setOnEdgeListener(new OnEdgeListener() {
            @Override
            public int onEdge() {
                if (null != getByType(TYPE_EDGE_LEFT) && !contentView.canScrollHorizontally(-1))
                    return TYPE_EDGE_LEFT;
                else if (null != getByType(TYPE_EDGE_RIGHT) && !contentView.canScrollHorizontally(1))
                    return TYPE_EDGE_RIGHT;
                else if (null != getByType(TYPE_EDGE_TOP) && !contentView.canScrollVertically(-1))
                    return TYPE_EDGE_TOP;
                else if (null != getByType(TYPE_EDGE_BOTTOM) && !contentView.canScrollVertically(1))
                    return TYPE_EDGE_BOTTOM;
                else
                    return TYPE_NONE;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 遍历子View
        for (Map.Entry<View, ChildViewAttr> entry : childViews.entrySet()) {
            View childView = entry.getKey();
            ChildViewAttr childViewAttr = entry.getValue();
            // 要求该子View进行测量
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            // 得到子View的LayoutParams对象
            LayoutParams lp = (LayoutParams) childView.getLayoutParams();
            switch (lp.type) {
                case TYPE_EDGE_LEFT:
                case TYPE_EDGE_RIGHT:// 类型为横向的子View
                    /**
                     * 把子View的size值记录下来，在摆放子View时会用到
                     * 横向size对应为宽度加左右margin
                     * 纵向size对应为高度加上下margin
                     */
                    childViewAttr.size = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                    trigger_offset_left = trigger_offset_left < 0 ? childViewAttr.size / 2 : trigger_offset_left;
                    trigger_offset_right = trigger_offset_right < 0 ? childViewAttr.size / 2 : trigger_offset_right;
                    max_offset_left = max_offset_left < 0 ? childViewAttr.size : max_offset_left;
                    max_offset_right = max_offset_right < 0 ? childViewAttr.size : max_offset_right;
                    break;
                case TYPE_EDGE_TOP:
                case TYPE_EDGE_BOTTOM:
                    childViewAttr.size = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                    trigger_offset_top = trigger_offset_top < 0 ? childViewAttr.size / 2 : trigger_offset_top;
                    trigger_offset_bottom = trigger_offset_bottom < 0 ? childViewAttr.size / 2 : trigger_offset_bottom;
                    max_offset_top = max_offset_top < 0 ? childViewAttr.size : max_offset_top;
                    max_offset_bottom = max_offset_bottom < 0 ? childViewAttr.size : max_offset_bottom;
                    break;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 首先获取content，得到宽高用于给其他子View做参考
        View contentView = getByType(TYPE_CONTENT);
        if (null == contentView)
            throw new IllegalArgumentException("EasyPullLayout must have and only have one layout_type \"content\"!");

        int contentWidth = contentView.getMeasuredWidth();
        int contentHeight = contentView.getMeasuredHeight();

        for (Map.Entry<View, ChildViewAttr> entry : childViews.entrySet()) {
            // 首先计算出子View的位置
            // 此时还未进行偏移，左上角都位于(0,0)
            View childView = entry.getKey();
            ChildViewAttr childViewAttr = entry.getValue();
            LayoutParams lp = (LayoutParams) childView.getLayoutParams();

            int left = getPaddingLeft() + lp.leftMargin;
            int top = getPaddingTop() + lp.topMargin;
            int right = left + childView.getMeasuredWidth();
            int bottom = top + childView.getMeasuredHeight();

            switch (lp.type) {
                case TYPE_EDGE_LEFT:// 左侧的子View应该向左偏移，摆放在左侧
                    left  -= childViewAttr.size;
                    right -= childViewAttr.size;
                    break;
                case TYPE_EDGE_TOP:
                    top -= childViewAttr.size;
                    bottom -= childViewAttr.size;
                    break;
                case TYPE_EDGE_RIGHT:
                    left += contentWidth;
                    right += contentWidth;
                    break;
                case TYPE_EDGE_BOTTOM:
                    top += contentHeight;
                    bottom += contentHeight;
                    break;
            }
            childViewAttr.setBounds(left, top, right, bottom); // child views' initial location
            childView.layout(left, top, right, bottom);
        }

        // bring the child to front if fixed
        View child = getByType(TYPE_EDGE_LEFT);

        if (fixed_content_left && null != child)// 若设置了左侧拖拽时固定
            child.bringToFront();// 改变左侧边缘视图z-order，使其在顶部
        child = getByType(TYPE_EDGE_TOP);
        if (fixed_content_top && null != child)
            child.bringToFront();
        child = getByType(TYPE_EDGE_RIGHT);
        if (fixed_content_right && null != child)
            child.bringToFront();
        child = getByType(TYPE_EDGE_BOTTOM);
        if (fixed_content_bottom && null != child)
            child.bringToFront();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (currentState != STATE_IDLE)
            return false;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int type = onEdgeListener.onEdge();
                float dx = ev.getX() - downX;
                float dy = ev.getY() - downY;
                currentType = type;
                if (type == TYPE_EDGE_LEFT && (pullTypeMask & PULL_TYPE_LEFT) != 0)
                    return ev.getX() > downX && Math.abs(dx) > Math.abs(dy);
                else if (type == TYPE_EDGE_RIGHT && (pullTypeMask & TYPE_EDGE_RIGHT) != 0)
                    return ev.getX() < downX && Math.abs(dx) > Math.abs(dy);
                else if (type == TYPE_EDGE_TOP && (pullTypeMask & TYPE_EDGE_TOP) != 0)
                    return ev.getY() > downY && Math.abs(dy) > Math.abs(dx);
                else if (type == TYPE_EDGE_BOTTOM && (pullTypeMask & TYPE_EDGE_BOTTOM) != 0)
                    return ev.getY() < downY && Math.abs(dy) > Math.abs(dx);
                else
                    return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // check state
        if (currentState != STATE_IDLE)
            return false;

        // check pull type
        if (currentType == TYPE_EDGE_LEFT && (pullTypeMask & PULL_TYPE_LEFT) == 0 ||
                currentType == TYPE_EDGE_TOP && (pullTypeMask & PULL_TYPE_TOP) == 0 ||
                currentType == TYPE_EDGE_RIGHT && (pullTypeMask & PULL_TYPE_RIGHT) == 0 ||
                currentType == TYPE_EDGE_BOTTOM && (pullTypeMask & PULL_TYPE_BOTTOM) == 0)
            return false;

        getParent().requestDisallowInterceptTouchEvent(true);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                offsetX = (x - downX) * (1 - sticky_factor * 0.75f);// 限制 offsetX 的最小和最大值
                offsetY = (y - downY) * (1 - sticky_factor * 0.75f);
                float pullFraction = 0f;// 计算出当前拖拽进度，未拖拽时为0，到达触发位置时为1

                // limit the offset
                switch (currentType) {
                    case TYPE_EDGE_LEFT:
                        offsetX = offsetX < 0 ? 0f : offsetX > max_offset_left ? max_offset_left : offsetX;
                        pullFraction = offsetX == 0f ? 0f : trigger_offset_left > offsetX ? offsetX / trigger_offset_left : 1f;
                        break;
                    case TYPE_EDGE_RIGHT:
                        offsetX = offsetX > 0 ? 0f : offsetX < -max_offset_right ? -max_offset_right : offsetX;
                        pullFraction = offsetX == 0f ? 0f : -trigger_offset_right < offsetX ? offsetX / -trigger_offset_right : 1f;
                        break;
                    case TYPE_EDGE_TOP:
                        offsetY = offsetY < 0 ? 0f : offsetY > max_offset_top ? max_offset_top : offsetY;
                        pullFraction = offsetY == 0f ? 0f : trigger_offset_top > offsetY ? offsetY / trigger_offset_top : 1f;
                        break;
                    case TYPE_EDGE_BOTTOM:
                        offsetY = offsetY > 0 ? 0f : offsetY < -max_offset_bottom ? -max_offset_bottom : offsetY;
                        pullFraction = offsetY == 0f ? 0f : -trigger_offset_bottom < offsetY ? offsetY / -trigger_offset_bottom : 1f;
                        break;
                }

                boolean changed = !(lastPullFraction < 1f && pullFraction < 1f || lastPullFraction == 1f && pullFraction == 1f);
                onPullListenerAdapter.onPull(currentType, pullFraction, changed);
                lastPullFraction = pullFraction;

                // do offset
                for (Map.Entry<View, ChildViewAttr> entry : childViews.entrySet()) {
                    View childView = entry.getKey();
                    ChildViewAttr childViewAttr = entry.getValue();
                    if (currentType == TYPE_EDGE_LEFT &&
                            (((LayoutParams) childView.getLayoutParams()).type != TYPE_CONTENT || !fixed_content_left))
                        childView.setX(childViewAttr.left + offsetX);
                    else if (currentType == TYPE_EDGE_RIGHT &&
                            (((LayoutParams) childView.getLayoutParams()).type != TYPE_CONTENT || !fixed_content_right))
                        childView.setX(childViewAttr.left + offsetX);
                    else if (currentType == TYPE_EDGE_TOP &&
                            ((LayoutParams) childView.getLayoutParams()).type != TYPE_CONTENT || !fixed_content_top)
                        childView.setY(childViewAttr.top + offsetY);
                    else if (currentType == TYPE_EDGE_BOTTOM &&
                            (((LayoutParams) childView.getLayoutParams()).type != TYPE_CONTENT || !fixed_content_bottom))
                        childView.setY(childViewAttr.top + offsetY);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                currentState = STATE_ROLLING;
                switch (currentType) {
                    case TYPE_EDGE_LEFT:
                    case TYPE_EDGE_RIGHT:
                        rollBackHorizontal();
                        break;
                    case TYPE_EDGE_TOP:
                    case TYPE_EDGE_BOTTOM:
                        rollBackVertical();
                        break;
                }
                break;
        }
        return true;
    }

    private void rollBackHorizontal() {
        final float rollBackOffset = offsetX > trigger_offset_left ? offsetX - trigger_offset_left :
                offsetX < -trigger_offset_right ? offsetX + trigger_offset_right : offsetX;

        final float triggerOffset = rollBackOffset != offsetX ?
                currentType == TYPE_EDGE_LEFT ? trigger_offset_left :
                        (currentType == TYPE_EDGE_RIGHT ? -trigger_offset_right : 0)
                : 0;

        horizontalAnimator = ValueAnimator.ofFloat(1f, 0f);
        horizontalAnimator.setDuration(roll_back_duration).setInterpolator(new DecelerateInterpolator());
        horizontalAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                switch (currentType) {
                    case TYPE_EDGE_LEFT:
                        for (Map.Entry<View, ChildViewAttr> entry : childViews.entrySet()) {
                            View childView = entry.getKey();
                            ChildViewAttr childViewAttr = entry.getValue();
                            if (((LayoutParams) childView.getLayoutParams()).type != TYPE_CONTENT || !fixed_content_left)
                                childView.setX(childViewAttr.left + triggerOffset + rollBackOffset * (float) animation.getAnimatedValue());
                        }
                        break;
                    case TYPE_EDGE_RIGHT:
                        for (Map.Entry<View, ChildViewAttr> entry : childViews.entrySet()) {
                            View childView = entry.getKey();
                            ChildViewAttr childViewAttr = entry.getValue();
                            if (((LayoutParams) childView.getLayoutParams()).type != TYPE_CONTENT || !fixed_content_right)
                                childView.setX(childViewAttr.left + triggerOffset + rollBackOffset * (float) animation.getAnimatedValue());
                        }
                        break;
                }
            }
        });
        horizontalAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (triggerOffset != 0 && currentState == STATE_ROLLING) {
                    currentState = STATE_TRIGGERING;
                    offsetX = triggerOffset;
                    onPullListenerAdapter.onTriggered(currentType);
                } else {
                    currentState = STATE_IDLE;
                    offsetX = 0f;
                }
            }
        });
        horizontalAnimator.start();
    }

    private void rollBackVertical() {
        // 需要还原的偏移量
        final float rollBackOffset = offsetY > trigger_offset_top ?
                offsetY - trigger_offset_top : offsetY < -trigger_offset_bottom ?
                offsetY + trigger_offset_bottom : offsetY;
        // 触发位置的偏移量
        final float triggerOffset = rollBackOffset != offsetY ?
                (currentType == TYPE_EDGE_TOP ?
                        trigger_offset_top : (currentType == TYPE_EDGE_BOTTOM ? -trigger_offset_bottom : 0)) : 0;

        verticalAnimator = ValueAnimator.ofFloat(1f, 0f);// 动画，值从1->0
        verticalAnimator.setDuration(roll_back_duration)
                .setInterpolator(new DecelerateInterpolator());
        verticalAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                switch (currentType) {
                    case TYPE_EDGE_TOP:
                        for (Map.Entry<View, ChildViewAttr> entry : childViews.entrySet()) {
                            View childView = entry.getKey();
                            ChildViewAttr childViewAttr = entry.getValue();
                            if (((LayoutParams) childView.getLayoutParams()).type != TYPE_CONTENT || !fixed_content_top)
                                childView.setY(childViewAttr.top + triggerOffset + rollBackOffset * (float) animation.getAnimatedValue());
                        }
                        break;

                    case TYPE_EDGE_BOTTOM:
                        for (Map.Entry<View, ChildViewAttr> entry : childViews.entrySet()) {
                            View childView = entry.getKey();
                            ChildViewAttr childViewAttr = entry.getValue();
                            if (((LayoutParams) childView.getLayoutParams()).type != TYPE_CONTENT || !fixed_content_bottom)
                                childView.setY(childViewAttr.top + triggerOffset + rollBackOffset * (float) animation.getAnimatedValue());
                        }
                        break;
                }
            }
        });

        // 动画结束后，还原一些参数，回调监听
        verticalAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (triggerOffset != 0 && currentState == STATE_ROLLING) {
                    // 还原到触发位置
                    currentState = STATE_TRIGGERING;
                    offsetY = triggerOffset;
                    onPullListenerAdapter.onTriggered(currentType);// 回调触发监听
                } else {
                    // 还原到初始位置
                    currentState = STATE_IDLE;
                    offsetY = 0f;
                    rollBackEnd();
                }
            }
        });
        verticalAnimator.start();
    }

    private void rollBackEnd() {
        switch (currentType) {
            case TYPE_EDGE_LEFT:
                onPullListenerAdapter.onRollBack(ROLL_BACK_TYPE_LEFT);
                break;
            case TYPE_EDGE_TOP:
                onPullListenerAdapter.onRollBack(ROLL_BACK_TYPE_TOP);
                break;
            case TYPE_EDGE_RIGHT:
                onPullListenerAdapter.onRollBack(ROLL_BACK_TYPE_RIGHT);
                break;
            case TYPE_EDGE_BOTTOM:
                onPullListenerAdapter.onRollBack(ROLL_BACK_TYPE_BOTTOM);
                break;
        }
    }

    /**
     * stop triggering
     */
    public void stop() {
        switch (currentType) {
            case TYPE_EDGE_LEFT:
            case TYPE_EDGE_RIGHT:
                rollBackHorizontal();
                break;
            case TYPE_EDGE_TOP:
            case TYPE_EDGE_BOTTOM:
                rollBackVertical();
                break;
        }
    }

    /**
     * 定义 启动动画
     * 设置 currentType （横向拉伸 or 纵向拉伸）
     */
    public void start(int currentType){
        this.currentType = currentType;
        switch (currentType) {
            case TYPE_EDGE_LEFT:
            case TYPE_EDGE_RIGHT:

                break;
            case TYPE_EDGE_BOTTOM:
            case TYPE_EDGE_TOP://一般只需要下拉 动画
                currentState = STATE_ROLLING;
                offsetY = max_offset_top;

                rollBackVertical();
                break;
        }
    }

    /**
     * Enable or disable one or more pull type
     *
     * @param pullType pull type to handle
     * @param enable   true if want to enable the type, other wise false
     */
    public void setPullType(int pullType, boolean enable) {
        if (pullType != PULL_TYPE_LEFT && pullType != PULL_TYPE_TOP &&
                pullType != PULL_TYPE_RIGHT && pullType != PULL_TYPE_BOTTOM)
            return;

        if (enable)
            pullTypeMask |= pullType;
        else
            pullTypeMask &= ~pullType;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return null != p && p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int type = TYPE_NONE;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.getTheme().obtainStyledAttributes(attrs, R.styleable.EasyPullLayout_LayoutParams, 0, 0);
            type = a.getInt(R.styleable.EasyPullLayout_LayoutParams_layout_type, TYPE_NONE);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }


    private class ChildViewAttr {
        int left, top, right, bottom, size;

        public ChildViewAttr() {
            left = 0;
            top = 0;
            right = 0;
            bottom = 0;
            size = 0;
        }

        public void setBounds(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    /**
     * 遍历 map 获取指定 type的 view
     * @param type
     * @return
     */
    public View getByType(int type) {
        for (Map.Entry<View, ChildViewAttr> entry : childViews.entrySet()) {
            if (((LayoutParams) entry.getKey().getLayoutParams()).type == type)
                return entry.getKey();
        }
        return null;
    }

    /**
     * 根据 type 获取对应的动画view
     * @param type
     * @return
     */
    public RefreshAnimView getAnimView(int type){
        RefreshAnimView view = null;
        if (type == TYPE_EDGE_TOP) {
            view = (RefreshAnimView) getByType(TYPE_EDGE_TOP);
        } else if (type == TYPE_EDGE_BOTTOM) {
            view = (RefreshAnimView) getByType(TYPE_EDGE_BOTTOM);
        } else if (type == TYPE_EDGE_LEFT) {
            view = (RefreshAnimView) getByType(TYPE_EDGE_LEFT);
        } else if (type == TYPE_EDGE_RIGHT) {
            view = (RefreshAnimView) getByType(TYPE_EDGE_RIGHT);
        }

        return view;
    }

    /**
     * 定义手势 滑动监听
     */
    public void addOnPullListenerAdapter() {
        this.onPullListenerAdapter = new OnPullListenerAdapter() {
            @Override
            public void onPull(int type, float fraction, boolean changed) {
                if (!changed) return;

                RefreshAnimView view = getAnimView(type);
                if (null != view) {
                    if (fraction == 1f) view.ready();
                    else view.idle();
                }
            }

            @Override
            public void onTriggered(int type) {
                RefreshAnimView view = getAnimView(type);
                if (null != view) view.triggered();

                if (type == TYPE_EDGE_LEFT || type == TYPE_EDGE_TOP) {
                    if (null != onRefreshLoadMoreListener) onRefreshLoadMoreListener.onRefresh();
                    if (null != onRefreshListener) onRefreshListener.onRefresh();
                } else if (type == TYPE_EDGE_RIGHT || type == TYPE_EDGE_BOTTOM) {
                    if (null != onRefreshLoadMoreListener) onRefreshLoadMoreListener.onLoadMore();
                    if (null != onLoadMoreListener) onLoadMoreListener.onLoadMore();
                }
            }

            @Override
            public void onRollBack(int type) {
                RefreshAnimView view = getAnimView(type);
                if (null != view) view.idle();
            }
        };
    }


    public void setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener onRefreshLoadMoreListener) {
        this.onRefreshLoadMoreListener = onRefreshLoadMoreListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnEdgeListener {
        /**
         * Is reaching the edge, will be set by default to detect whether the content view
         * is on edge or not, can be rewritten if you want to detect by your own logic
         *
         * @return One of the edge types TYPE_EDGE_LEFT, TYPE_EDGE_TOP,
         * TYPE_EDGE_RIGHT, TYPE_EDGE_BOTTOM or TYPE_NONE if not reaching the edge
         */
        int onEdge();
    }

    public void setOnEdgeListener(OnEdgeListener onEdgeListener) {
        this.onEdgeListener = onEdgeListener;
    }
}
