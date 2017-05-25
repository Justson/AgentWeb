package com.just.library;

import android.webkit.ValueCallback;

/**
 * Created by cenxiaozhong on 2017/5/14.
 */

public interface JsEntraceAccess {


    void callJs(String str, ValueCallback<String> callback);

    void loadJs(String str);
}
