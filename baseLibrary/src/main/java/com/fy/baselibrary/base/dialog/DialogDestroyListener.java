package com.fy.baselibrary.base.dialog;

import android.os.Parcel;
import android.os.Parcelable;

import com.fy.baselibrary.base.ViewHolder;

/**
 * 关闭 dialog 回调
 * Created by fangs on 2018/3/22.
 */
public abstract class DialogDestroyListener implements Parcelable {

    protected abstract void destroyView(ViewHolder holder, CommonDialog dialog);


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public DialogDestroyListener() {
    }

    protected DialogDestroyListener(Parcel in) {
    }

    public static final Creator<DialogDestroyListener> CREATOR = new Creator<DialogDestroyListener>() {
        @Override
        public DialogDestroyListener createFromParcel(Parcel source) {
            return new DialogDestroyListener(source) {
                @Override
                protected void destroyView(ViewHolder holder, CommonDialog dialog) {

                }
            };
        }

        @Override
        public DialogDestroyListener[] newArray(int size) {
            return new DialogDestroyListener[size];
        }
    };
}
