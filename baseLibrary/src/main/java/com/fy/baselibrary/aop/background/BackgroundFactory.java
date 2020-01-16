package com.fy.baselibrary.aop.background;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fy.baselibrary.R;
import com.fy.baselibrary.utils.notify.L;
import com.fy.baselibrary.utils.drawable.TintUtils;

import java.lang.reflect.Constructor;
import java.util.Map;

/**
 * describe： 自定义 Factory
 * Created by fangs on 2018/12/20 18:22.
 */
public class BackgroundFactory implements LayoutInflater.Factory2 {

    private LayoutInflater.Factory mViewCreateFactory;
    private LayoutInflater.Factory2 mViewCreateFactory2;

    private static final Class<?>[] sConstructorSignature = new Class[]{Context.class, AttributeSet.class};
    private final Object[] mConstructorArgs = new Object[2];
    private static final Map<String, Constructor<? extends View>> sConstructorMap = new ArrayMap<>();

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = null;
        if (null != mViewCreateFactory2) {
            view = mViewCreateFactory2.onCreateView(name, context, attrs);
            if (view == null) {
                view = mViewCreateFactory2.onCreateView(null, name, context, attrs);
            }
        } else if (null != mViewCreateFactory) {
            view = mViewCreateFactory.onCreateView(name, context, attrs);
        }

        TypedArray svgCompat = context.obtainStyledAttributes(attrs, R.styleable.svgCompat);

        try {
            if (svgCompat.getIndexCount() <= 0) return view;

            if (view == null) view = createViewFromTag(context, name, attrs);

            if (view == null) return null;

            addDrawable(view, svgCompat);
        } catch (Exception e) {
//            e.printStackTrace();
            L.e(e.toString());
        } finally {
            svgCompat.recycle();
        }

        return view;
    }


    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return onCreateView(name, context, attrs);
    }


    public void setInterceptFactory(LayoutInflater.Factory factory) {
        mViewCreateFactory = factory;
    }

    public void setInterceptFactory2(LayoutInflater.Factory2 factory) {
        mViewCreateFactory2 = factory;
    }




    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }

        try {
            mConstructorArgs[0] = context;
            mConstructorArgs[1] = attrs;

            if (-1 == name.indexOf('.')) {
                View view = null;
                if ("View".equals(name)) {
                    view = createView(context, name, "android.view.");
                }
                if (view == null) {
                    view = createView(context, name, "android.widget.");
                }
                if (view == null) {
                    view = createView(context, name, "android.webkit.");
                }
                return view;
            } else {
                return createView(context, name, null);
            }
        } catch (Exception e) {
            L.i("BackgroundLibrary", "cannot create 【" + name + "】 : ");
            return null;
        } finally {
            mConstructorArgs[0] = null;
            mConstructorArgs[1] = null;
        }
    }

    private View createView(Context context, String name, String prefix) throws InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);
        try {
            if (constructor == null) {
                Class<? extends View> clazz = context.getClassLoader()
                        .loadClass(prefix != null ? (prefix + name) : name)
                        .asSubclass(View.class);

                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(mConstructorArgs);
        } catch (Exception e) {
            L.i("BackgroundLibrary", "cannot create 【" + name + "】 : ");
            return null;
        }
    }

    /**
     * 通过 自定义属性容器，对 View 设置样式
     * @param view
     * @param svgCompat
     */
    private void addDrawable(View view, TypedArray svgCompat) {
        @DrawableRes
        int svgDrawable = 0;
        int svgTintColor = 0;
        int drawableType = 0;
        int iconLocationType = 0;

        for (int i = 0; i < svgCompat.getIndexCount(); i++) {
            int attr = svgCompat.getIndex(i);
            if (attr == R.styleable.svgCompat_svgDrawable) {
                svgDrawable = svgCompat.getResourceId(attr, 0);
            } else if (attr == R.styleable.svgCompat_svgTintColor){
                svgTintColor = svgCompat.getColor(attr, Color.WHITE);
            } else if (attr == R.styleable.svgCompat_drawableType){
                drawableType = svgCompat.getInt(attr, 0);
            } else if (attr == R.styleable.svgCompat_iconLocationType){
                iconLocationType = svgCompat.getInt(attr, 0);
            }
        }

        if(svgDrawable == 0)return;

        Drawable drawable;

        if (svgTintColor == 0){
            drawable = TintUtils.getDrawable(svgDrawable, drawableType);
        } else {
            drawable = TintUtils.getTintColorDrawable(svgDrawable, drawableType, svgTintColor);
        }

        if (iconLocationType == 0){
            view.setBackground(drawable);
        } else {
            if (view instanceof TextView){
                TintUtils.setTxtIconLocal((TextView) view, drawable, iconLocationType);
            }
        }
    }
}
