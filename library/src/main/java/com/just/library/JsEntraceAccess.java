package com.just.library;

import android.webkit.ValueCallback;

/**
 * Created by cenxiaozhong on 2017/5/14.
 */

public interface JsEntraceAccess {


    void callJs(String js, ValueCallback<String> callback);

    void callJs(String js);



    void quickCallJs(String method,ValueCallback<String> callback,String... params);

    void quickCallJs(String method,String... params);
    void quickCallJs(String method);
}
