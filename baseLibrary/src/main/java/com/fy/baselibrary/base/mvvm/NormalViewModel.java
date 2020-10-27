package com.fy.baselibrary.base.mvvm;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * description 不需要用ViewModel的,请用此类代替
 * Created by fangs on 2020/10/12 17:24.
 */
public class NormalViewModel extends BaseViewModel {

    public NormalViewModel(@NonNull Application application) {
        super(application);
    }
}
