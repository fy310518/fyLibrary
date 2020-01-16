package com.fy.baselibrary.base.dialog;

import android.os.Parcel;
import android.os.Parcelable;

import com.fy.baselibrary.base.ViewHolder;

/**
 * 绘制 dialog UI 回调
 * Created by fangs on 2018/3/22.
 */
public abstract class DialogConvertListener implements Parcelable {

    protected abstract void convertView(ViewHolder holder, CommonDialog dialog);


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public DialogConvertListener() {
    }

    protected DialogConvertListener(Parcel in) {
    }

    public static final Creator<DialogConvertListener> CREATOR = new Creator<DialogConvertListener>() {
        @Override
        public DialogConvertListener createFromParcel(Parcel source) {
            return new DialogConvertListener(source) {
                @Override
                protected void convertView(ViewHolder holder, CommonDialog dialog) {

                }
            };
        }

        @Override
        public DialogConvertListener[] newArray(int size) {
            return new DialogConvertListener[size];
        }
    };
}
