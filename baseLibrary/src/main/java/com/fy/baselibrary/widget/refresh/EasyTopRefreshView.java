package com.fy.baselibrary.widget.refresh;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.fy.baselibrary.R;
import com.fy.baselibrary.utils.TimeUtils;

/**
 * 列表刷新 视图（自定义组合控件）
 * Created by fangs on 2017/11/22.
 */
public class EasyTopRefreshView extends RefreshAnimView {

    ConstraintLayout topRefreshLayout;
    AppCompatImageView imgArrow;
    AppCompatImageView imgTurn;
    TextView tvLoadTip;
    TextView tvDate;

    public EasyTopRefreshView(Context context) {
        this(context, null);
    }

    public EasyTopRefreshView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyTopRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_top_refresh, this, true);

        topRefreshLayout = view.findViewById(R.id.topRefreshLayout);
        imgArrow = view.findViewById(R.id.imgArrow);
        imgTurn = view.findViewById(R.id.imgTurn);
        tvLoadTip = view.findViewById(R.id.tvLoadTip);
        tvDate = view.findViewById(R.id.tvDate);

        idle();
    }

    @Override
    public void idle() {
        tvLoadTip.setText(R.string.idle);
        tvDate.setText(TimeUtils.Long2DataString(System.currentTimeMillis(), "yyyy-MM-dd  hh:mm:ss"));

        imgTurn.setVisibility(GONE);
        imgArrow.setVisibility(VISIBLE);

        imgArrow.animate()
                .setInterpolator(new BounceInterpolator())
                .setDuration(300)
                .rotation(0)
                .start();
    }

    @Override
    public void ready() {
        tvLoadTip.setText(R.string.ready);

        imgTurn.setVisibility(GONE);
        imgArrow.setVisibility(VISIBLE);

        imgArrow.animate()
                .setInterpolator(new BounceInterpolator())
                .setDuration(300)
                .rotation(180)
                .start();

        bgAnim();
    }

    @Override
    public void triggered() {
        tvLoadTip.setText(R.string.data_loading);
        imgArrow.setVisibility(INVISIBLE);
        imgTurn.setVisibility(VISIBLE);

        Animator animator = AnimatorInflater.loadAnimator(getContext(), R.animator.refresh_rotate);
        animator.setTarget(imgTurn);
        animator.start();
    }


    public void bgAnim(){
        ValueAnimator verticalAnimator = ValueAnimator.ofFloat(1f, 0f);// 动画，值从1->0
        verticalAnimator.setDuration(300).setInterpolator(new DecelerateInterpolator());

//        verticalAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                for (Map.Entry<View, EasyPullLayout.ChildViewAttr> entry : childViews.entrySet()) {
//                    View childView = entry.getKey();
//                    EasyPullLayout.ChildViewAttr childViewAttr = entry.getValue();
//                    if (((EasyPullLayout.LayoutParams) childView.getLayoutParams()).type != TYPE_CONTENT || !fixed_content_top)
//                        childView.setY(childViewAttr.top + triggerOffset + rollBackOffset * (float) animation.getAnimatedValue());
//                }
//            }
//        });
    }

}
