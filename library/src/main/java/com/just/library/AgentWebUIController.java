package com.just.library;

import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/12/6.
 * 该类统一控制了与用户交互的一些UI
 */
public abstract class AgentWebUIController {


    protected void onJsAlert(WebView view, String url, String message) {

    }


    protected void onJsConfirm(WebView view, String url, String message) {

    }


    public void onJsPrompt(WebView view, String url, String message, String defaultValue) {

    }
}
