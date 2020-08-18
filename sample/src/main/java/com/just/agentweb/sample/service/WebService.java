package com.just.agentweb.sample.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author xiaozhongcen
 * @date 20-8-18
 * @since 1.0.0
 * 提前初始化进程减少白屏
 */
public class WebService extends Service {

    private static final String TAG = WebService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "init process");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
