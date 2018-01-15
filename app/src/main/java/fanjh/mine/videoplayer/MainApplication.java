package fanjh.mine.videoplayer;

import android.app.Application;

/**
 * Created by faker on 2018/1/12.
 */

public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });
    }
}
