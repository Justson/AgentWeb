package com.just.library;

import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/12/6.
 * 该类统一控制了与用户交互的一些界面
 */
public class AgentWebUIControllerImpl extends AgentWebUIController  {


    public static AgentWebUIController build() {
        return new AgentWebUIControllerImpl();
    }

    @Override
    public void onJsAlert(WebView view, String url, String message) {
        getDelegate().onJsAlert(view, url, message);
    }

    @Override
    public void onJsConfirm(WebView view, String url, String message) {
        getDelegate().onJsConfirm(view, url, message);
    }

    @Override
    public void onJsPrompt(WebView view, String url, String message, String defaultValue) {
        getDelegate().onJsPrompt(view, url, message, defaultValue);
    }


}
