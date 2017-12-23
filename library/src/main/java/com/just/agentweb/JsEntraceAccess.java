package com.just.agentweb;

import android.webkit.ValueCallback;

/**
 * Created by cenxiaozhong on 2017/5/14.
 * source code  https://github.com/Justson/AgentWeb
 */

public interface JsEntraceAccess extends QuickCallJs{


    void callJs(String js, ValueCallback<String> callback);

    void callJs(String js);




}
