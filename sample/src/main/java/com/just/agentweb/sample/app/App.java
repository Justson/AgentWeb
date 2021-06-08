package com.just.agentweb.sample.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.webkit.WebView;

import com.just.agentweb.sample.service.WebService;
import com.just.agentweb.sample.utils.ProcessUtils;
import com.queue.library.GlobalQueue;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by cenxiaozhong on 2017/5/23.
 * source code  https://github.com/Justson/AgentWeb
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 说明， WebView 初处初始化耗时 250ms 左右。
         * 提前初始化WebView ，好处可以提升页面初始化速度，减少白屏时间，
         * 坏处，拖慢了App 冷启动速度，如果 WebView 配合 VasSonic 使用，
         * 建议不要在此处提前初始化 WebView 。
         */
//        WebView mWebView=new WebView(new MutableContextWrapper(this));

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        //implementation 'com.github.Justson:dispatch-queue:v1.0.5'
        GlobalQueue.getMainQueue().postRunnableInIdleRunning(new Runnable() {
            @Override
            public void run() {
                try {
                    startService(new Intent(App.this, WebService.class));
                } catch (Throwable throwable) {

                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 安卓9.0后不允许多进程使用同一个数据目录
            String processName = ProcessUtils.getCurrentProcessName(base);
            if (!base.getApplicationContext().getPackageName().equals(processName)) {
                try {
                    WebView.setDataDirectorySuffix(processName);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }

}
