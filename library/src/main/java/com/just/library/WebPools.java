package com.just.library;

/**
 * Created by cenxiaozhong on 2017/8/10.
 */

import android.app.Activity;
import android.content.MutableContextWrapper;
import android.webkit.WebView;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 *复用webview
 */
public class WebPools {


    private final Queue<WebView> mWebViews;

    private Object lock = new Object();
    private static WebPools mWebPools = null;

    private static final AtomicReference<WebPools> mAtomicReference = new AtomicReference<>();
    private static final String TAG=WebPools.class.getSimpleName();

    private WebPools() {
        mWebViews = new LinkedBlockingQueue<>();
    }


    public static WebPools getInstance() {

        for (; ; ) {
            if (mWebPools != null)
                return mWebPools;
            if (mAtomicReference.compareAndSet(null, new WebPools()))
                return mWebPools=mAtomicReference.get();

        }
    }


    public void recycle(WebView webView) {
        recycleInternal(webView);
    }



    public WebView acquireWebView(Activity activity) {
        return acquireWebViewInternal(activity);
    }

    private WebView acquireWebViewInternal(Activity activity) {

        WebView mWebView = mWebViews.poll();

        LogUtils.i(TAG,"acquireWebViewInternal  webview:"+mWebView);
        if (mWebView == null) {
            synchronized (lock) {
                return new WebView(new MutableContextWrapper(activity));
            }
        } else {
            MutableContextWrapper mMutableContextWrapper = (MutableContextWrapper) mWebView.getContext();
            mMutableContextWrapper.setBaseContext(activity);
            return mWebView;
        }
    }



    private void recycleInternal(WebView webView) {
        try {

            if (webView.getContext() instanceof MutableContextWrapper) {

                MutableContextWrapper mContext = (MutableContextWrapper) webView.getContext();
                mContext.setBaseContext(mContext.getApplicationContext());
                LogUtils.i(TAG,"enqueue  webview:"+webView);
                mWebViews.offer(webView);
            }
            if(webView.getContext() instanceof  Activity){
//            throw new RuntimeException("leaked");
                LogUtils.i(TAG,"Abandon this webview  ， It will cause leak if enqueue !");
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
