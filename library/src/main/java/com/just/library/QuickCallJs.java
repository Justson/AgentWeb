package com.just.library;

import android.webkit.ValueCallback;

/**
 * Created by cenxiaozhong on 2017/5/29.
 */

public interface QuickCallJs {
    void quickCallJs(String method, ValueCallback<String> callback, String... params);
    void quickCallJs(String method,String... params);
    void quickCallJs(String method);
}
