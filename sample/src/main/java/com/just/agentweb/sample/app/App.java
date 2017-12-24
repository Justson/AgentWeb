package com.just.agentweb.sample.app;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by cenxiaozhong on 2017/5/23.
 * source code  https://github.com/Justson/AgentWeb
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //提前初始化WebView
//        WebView mWebView=new WebView(new MutableContextWrapper(this));

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

    }
}
