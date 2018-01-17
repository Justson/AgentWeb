package com.just.agentweb;

import android.os.Build;
import android.support.annotation.RawRes;
import android.support.annotation.RequiresApi;
import android.webkit.ValueCallback;

/**
 * Created by cenxiaozhong on 2017/5/29.
 */

public interface QuickCallJS {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    void quickCallJS(String method, ValueCallback<String> callback, String... params);

    void quickCallJS(String method, String... params);

    void quickCallJS(String method);

    void quickCallRawJS(@RawRes int res);

}
