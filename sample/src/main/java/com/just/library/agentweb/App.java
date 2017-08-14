package com.just.library.agentweb;

import android.app.Application;
import android.content.MutableContextWrapper;
import android.webkit.WebView;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by cenxiaozhong on 2017/5/23.
 *  source CODE  https://github.com/Justson/AgentWeb
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WebView mWebView=new WebView(new MutableContextWrapper(this));

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

    }
}
