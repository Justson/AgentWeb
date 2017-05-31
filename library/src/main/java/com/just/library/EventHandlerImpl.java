package com.just.library;

import android.view.KeyEvent;
import android.webkit.WebView;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> <br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class EventHandlerImpl implements IEventHandler {
    private WebView mWebView;

    public static final EventHandlerImpl getInstantce(WebView view) {
        return new EventHandlerImpl(view);
    }

    public EventHandlerImpl(WebView webView) {
        this.mWebView = webView;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public boolean back() {
        if(mWebView!=null&&mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return false;
    }
}
