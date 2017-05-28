package com.just.library;

import android.graphics.Bitmap;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/5/28.
 */

public class WebViewClientCallbackManager {


    private PageLifeCycleCallback mPageLifeCycleCallback;

    public PageLifeCycleCallback getPageLifeCycleCallback() {
        return mPageLifeCycleCallback;
    }

    public void setPageLifeCycleCallback(PageLifeCycleCallback pageLifeCycleCallback) {
        mPageLifeCycleCallback = pageLifeCycleCallback;
    }

    public interface PageLifeCycleCallback {

        void onPageStarted(WebView view, String url, Bitmap favicon);
        void onPageFinished(WebView view, String url);

    }
}
