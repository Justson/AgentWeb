package com.just.library;

import android.app.Activity;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/12/8.
 */

public class DefaultUIController extends AgentWebUIController {

    @Override
    public void onJsAlert(WebView view, String url, String message) {

    }

    @Override
    public void onJsConfirm(WebView view, String url, String message) {

    }

    @Override
    public void onJsPrompt(WebView view, String url, String message, String defaultValue) {

    }

    @Override
    void bindSupportWebParent(WebParentLayout webParentLayout, Activity activity) {

    }
}
