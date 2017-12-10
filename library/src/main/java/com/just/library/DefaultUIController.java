package com.just.library;

import android.app.Activity;
import android.os.Handler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/12/8.
 */

public class DefaultUIController extends AgentWebUIController {

    @Override
    public void onJsAlert(WebView view, String url, String message) {

    }

    @Override
    public void onJsConfirm(WebView view, String url, String message, JsResult jsResult) {

    }

    @Override
    public void showChooser(WebView view, String url, String[] ways, Handler.Callback callback) {

    }

    @Override
    public void onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {

    }

    @Override
    protected void bindSupportWebParent(WebParentLayout webParentLayout, Activity activity) {

    }
}
