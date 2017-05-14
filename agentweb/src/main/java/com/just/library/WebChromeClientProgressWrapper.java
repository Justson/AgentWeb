package com.just.library;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import java.lang.reflect.Method;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class WebChromeClientProgressWrapper extends ChromeClientProgress {
    private WebChromeClient mWebChromeClient;

    public WebChromeClientProgressWrapper(IndicatorController indicatorController, WebChromeClient webChromeClient) {
        this(indicatorController);
        this.mWebChromeClient = webChromeClient;
    }

    public WebChromeClientProgressWrapper(IndicatorController indicatorController) {
        super(indicatorController);

    }


    public void onReceivedTitle(WebView view, String title) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onReceivedTitle(view, title);
    }

    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onReceivedIcon(view, icon);

    }

    public void onReceivedTouchIconUrl(WebView view, String url,
                                       boolean precomposed) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onReceivedTouchIconUrl(view, url, precomposed);
    }


    public void onShowCustomView(View view, CustomViewCallback callback) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onShowCustomView(view, callback);
    }

    ;

    public void onShowCustomView(View view, int requestedOrientation,
                                 CustomViewCallback callback) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onShowCustomView(view, requestedOrientation, callback);
    }

    ;

    public void onHideCustomView() {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onHideCustomView();
    }

    public boolean onCreateWindow(WebView view, boolean isDialog,
                                  boolean isUserGesture, Message resultMsg) {
        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        return false;
    }

    public void onRequestFocus(WebView view) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onRequestFocus(view);
    }

    public void onCloseWindow(WebView window) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onCloseWindow(window);
    }

    public boolean onJsAlert(WebView view, String url, String message,
                             JsResult result) {
        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.onJsAlert(view, url, message, result);
        return false;
    }

    public boolean onJsConfirm(WebView view, String url, String message,
                               JsResult result) {
        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.onJsConfirm(view, url, message, result);
        return false;
    }

    public boolean onJsPrompt(WebView view, String url, String message,
                              String defaultValue, JsPromptResult result) {
        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.onJsPrompt(view, url, message, defaultValue, result);
        return false;
    }

    public boolean onJsBeforeUnload(WebView view, String url, String message,
                                    JsResult result) {
        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.onJsBeforeUnload(view, url, message, result);
        return false;
    }

    @Deprecated
    public void onExceededDatabaseQuota(String url, String databaseIdentifier,
                                        long quota, long estimatedDatabaseSize, long totalQuota,
                                        WebStorage.QuotaUpdater quotaUpdater) {
        // This default implementation passes the current quota back to WebCore.
        // WebCore will interpret this that new quota was declined.
        //注掉
//        quotaUpdater.updateQuota(quota);
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);

    }

    @Deprecated
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota,
                                         WebStorage.QuotaUpdater quotaUpdater) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
    }

    public void onGeolocationPermissionsShowPrompt(String origin,
                                                   GeolocationPermissions.Callback callback) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onGeolocationPermissionsShowPrompt(origin, callback);

    }

    /**
     * Notify the host application that a request for Geolocation permissions,
     * made with a previous call to
     * {@link #onGeolocationPermissionsShowPrompt(String, GeolocationPermissions.Callback) onGeolocationPermissionsShowPrompt()}
     * has been canceled. Any related UI should therefore be hidden.
     */
    public void onGeolocationPermissionsHidePrompt() {

        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onGeolocationPermissionsHidePrompt();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onPermissionRequest(PermissionRequest request) {
//        request.deny();
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onPermissionRequest(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onPermissionRequestCanceled(PermissionRequest request) {

        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onPermissionRequestCanceled(request);
    }

    public boolean onJsTimeout() {
        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.onJsTimeout();
        return true;
    }

    @Deprecated
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.onConsoleMessage(message, lineNumber, sourceID);
    }

    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        /*onConsoleMessage(consoleMessage.message(), consoleMessage.lineNumber(),
                consoleMessage.sourceId());*/

        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.onConsoleMessage(consoleMessage);
        return false;
    }

    public Bitmap getDefaultVideoPoster() {
        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.getDefaultVideoPoster();
        return null;
    }

    public View getVideoLoadingProgressView() {
        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.getVideoLoadingProgressView();
        return null;
    }

    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (this.mWebChromeClient != null)
            this.mWebChromeClient.getVisitedHistory(callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        return false;
    }

    // Android  >= 4.1
    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        /*believe me , i never want to do this */
        commonRefect(this.mWebChromeClient, "openFileChooser", new Object[]{uploadFile, acceptType, capture}, uploadFile.getClass(), acceptType.getClass(), capture.getClass());
    }

    //  Android < 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
        Log.i("Infoss", "openFileChooser");
        commonRefect(this.mWebChromeClient, "openFileChooser", new Object[]{valueCallback}, valueCallback.getClass());
    }

    //  Android  >= 3.0
    public void openFileChooser(ValueCallback valueCallback, String acceptType) {
        Log.i("Infoss", "openFileChooser.1");
        commonRefect(this.mWebChromeClient, "openFileChooser", new Object[]{valueCallback, acceptType}, valueCallback.getClass(), acceptType.getClass());
    }


    private void commonRefect(WebChromeClient o, String mothed, Object[] os, Class... clazzs) {
        try {
            if (o == null)
                return;
            Class<?> clazz = o.getClass();
            Method mMethod = clazz.getMethod(mothed, clazzs);
            mMethod.invoke(o, os);
        } catch (Exception igore) {
//            igore.printStackTrace();
        }

    }


    /*public void setupAutoFill(Message msg) {
        if (this.mWebChromeClient != null)
            return this.mWebChromeClient.setupAutoFill();
    }*/

}
