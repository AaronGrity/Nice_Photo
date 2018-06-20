package com.wlw135.nice_photo;

import android.app.Application;

/**
 * Created by 10716 on 2018/6/20.
 */

public class app extends Application {
    private static app context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static app getContext() {
        return context;
    }
}
