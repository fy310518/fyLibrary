package com.fy.baselibrary.base.mvvm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

/**
 * description 定义 vm层； 创建关联，将model和view绑定起来,如此之后，我们model的更改，通过viewmodel反馈给view,从而自动刷新界面
 * Created by fangs on 2020/10/12 16:40.
 */
public class BaseViewModel extends AndroidViewModel {

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

}
