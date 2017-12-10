package com.just.library;

import android.app.Activity;
import android.os.Handler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/12/6.
 * 该类统一控制了与用户交互的一些界面
 */
public class AgentWebUIControllerImpl extends AgentWebUIController {


    public static AgentWebUIController build() {
        return new AgentWebUIControllerImpl();
    }

    @Override
    public void onJsAlert(WebView view, String url, String message) {
        getDelegate().onJsAlert(view, url, message);
    }

    @Override
    public void onJsConfirm(WebView view, String url, String message, JsResult jsResult) {
        getDelegate().onJsConfirm(view, url, message, jsResult);
    }

    @Override
    public void showChooser(WebView view, String url, String[] ways, Handler.Callback callback) {
        getDelegate().showChooser(view, url, ways, callback);
    }

    @Override
    public void onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {
        getDelegate().onJsPrompt(view, url, message, defaultValue, jsPromptResult);
    }

    @Override
    protected void bindSupportWebParent(WebParentLayout webParentLayout, Activity activity) {

    }


}
