package com.just.library;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

import java.lang.ref.WeakReference;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> <br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 * source CODE  https://github.com/Justson/AgentWeb
 */

public class DefaultWebClient extends WrapperWebViewClient {

    private WebViewClientCallbackManager mWebViewClientCallbackManager;
    private WeakReference<Activity> mWeakReference = null;
    private static final int CONSTANTS_ABNORMAL_BIG = 7;
    private WebViewClient mWebViewClient;
    private boolean webClientHelper = false;
    private static final String WEBVIEWCLIENTPATH = "android.webkit.WebViewClient";
    public static final String INTENT_SCHEME = "intent://";
    public static final String WEBCHAT_PAY_SCHEME = "weixin://wap/pay?";
    private static final boolean hasAlipayLib;

    static {
        boolean tag = true;
        try {
            Class.forName("com.alipay.sdk.app.PayTask");
        } catch (Throwable ignore) {
            tag = false;
        }
        hasAlipayLib = tag;

        LogUtils.i("Info", "static  hasAlipayLib:" + hasAlipayLib);
    }

    DefaultWebClient(@NonNull Activity activity, WebViewClient client, WebViewClientCallbackManager manager, boolean webClientHelper, PermissionInterceptor permissionInterceptor, WebView webView) {
        super(client);
        this.mWebViewClient = client;
        mWeakReference = new WeakReference<Activity>(activity);
        this.mWebViewClientCallbackManager = manager;
        this.webClientHelper = webClientHelper;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        LogUtils.i("Info", " DefaultWebClient shouldOverrideUrlLoading");
        if (webClientHelper && handleNormalLinked(request.getUrl() + "")) {
            return true;
        }
        int tag = -1;

        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", WEBVIEWCLIENTPATH + ".shouldOverrideUrlLoading", WebView.class, WebResourceRequest.class) && (((tag = 1) > 0) && super.shouldOverrideUrlLoading(view, request))) {
            return true;
        }

        if (webClientHelper && request.getUrl().toString().startsWith(INTENT_SCHEME)) { //
            handleIntentUrl(request.getUrl() + "");
            return true;
        }

        //06-13 10:21:22.238 21630-21630/com.just.library.agentweb I/Info: url:weixin://wap/pay?appid%3Dwxb08de3dfbafe2a05%26noncestr%3Da3e707b7ba724555a623cdcb487c9752%26package%3DWAP%26prepayid%3Dwx20170613102122864958995b0782239150%26sign%3D54C503BE0D29F0013D71E9A5FB634E6D%26timestamp%3D1497320482

        if (webClientHelper && request.getUrl().toString().startsWith(WEBCHAT_PAY_SCHEME)) {
            startActivity(request.getUrl().toString());
            return true;
        }
        if (webClientHelper && hasAlipayLib && isAlipay(view, request.getUrl() + ""))
            return true;

        if (tag > 0)
            return false;

        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtils.i("Info", "shouldOverrideUrlLoading --->  url:" + url);
        if (webClientHelper && handleNormalLinked(url)) {
            return true;
        }

        int tag = -1;

        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", WEBVIEWCLIENTPATH + ".shouldOverrideUrlLoading", WebView.class, String.class) && (((tag = 1) > 0) && super.shouldOverrideUrlLoading(view, url))) {
            return true;
        }

        if (webClientHelper && url.startsWith(INTENT_SCHEME)) { //拦截
            handleIntentUrl(url);
            return true;
        }

        if (webClientHelper && url.startsWith(WEBCHAT_PAY_SCHEME)) {
            startActivity(url);
            return true;
        }
        if (webClientHelper && hasAlipayLib && isAlipay(view, url))
            return true;

        if (tag > 0)
            return false;


        return super.shouldOverrideUrlLoading(view, url);
    }


    private void handleIntentUrl(String intentUrl) {
        try {

            Intent intent = null;
            if (TextUtils.isEmpty(intentUrl) || !intentUrl.startsWith(INTENT_SCHEME))
                return;

            Activity mActivity = null;
            if ((mActivity = mWeakReference.get()) == null)
                return;
            PackageManager packageManager = mActivity.getPackageManager();
            intent = new Intent().parseUri(intentUrl, Intent.URI_INTENT_SCHEME);
            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            LogUtils.i("Info", "resolveInfo:" + info + "   package:" + intent.getPackage());
            if (info != null) {  //跳到该应用
                mActivity.startActivity(intent);
                return;
            }
            /*intent=new Intent().setData(Uri.parse("market://details?id=" + intent.getPackage()));
            info=packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            LogUtils.i("Info","resolveInfo:"+info);
            if (info != null) {  //跳到应用市场
                mActivity.startActivity(intent);
                return;
            }

            intent=new Intent().setData(Uri.parse("https://play.google.com/store/apps/details?id=" + intent.getPackage()));
            info=packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            LogUtils.i("Info","resolveInfo:"+info);
            if (info != null) {  //跳到浏览器
                mActivity.startActivity(intent);
                return;
            }*/
        } catch (Throwable e) {
            if (LogUtils.isDebug())
                e.printStackTrace();
        }


    }

    private boolean isAlipay(final WebView view, String url) {

        Activity mActivity = null;
        if ((mActivity = mWeakReference.get()) == null)
            return false;
        final PayTask task = new PayTask(mActivity);
        final String ex = task.fetchOrderInfoFromH5PayUrl(url);
        if (!TextUtils.isEmpty(ex)) {
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                public void run() {
                    final H5PayResultModel result = task.h5Pay(ex, true);
                    if (!TextUtils.isEmpty(result.getReturnUrl())) {
                        AgentWebUtils.runInUiThread(new Runnable() {

                            @Override
                            public void run() {
                                view.loadUrl(result.getReturnUrl());
                            }
                        });
                    }
                }
            });

            return true;
        }
        return false;
    }


    private boolean handleNormalLinked(String url) {
        if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                Activity mActivity = null;
                if ((mActivity = mWeakReference.get()) == null)
                    return false;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mActivity.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        LogUtils.i("Info", "onPageStarted");
        if (AgentWebConfig.WEBVIEW_TYPE == AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE && mWebViewClientCallbackManager.getPageLifeCycleCallback() != null) {
            mWebViewClientCallbackManager.getPageLifeCycleCallback().onPageStarted(view, url, favicon);
        }
        super.onPageStarted(view, url, favicon);

    }


    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        LogUtils.i("Info", "onReceivedError：" + description + "  CODE:" + errorCode);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        LogUtils.i("Info", "onReceivedError:" + error.toString());

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (AgentWebConfig.WEBVIEW_TYPE == AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE && mWebViewClientCallbackManager.getPageLifeCycleCallback() != null) {
            mWebViewClientCallbackManager.getPageLifeCycleCallback().onPageFinished(view, url);
        }
        super.onPageFinished(view, url);

        LogUtils.i("Info", "onPageFinished");
    }


    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        LogUtils.i("Info", "shouldOverrideKeyEvent");
        return super.shouldOverrideKeyEvent(view, event);
    }


    private void startActivity(String url) {


        try {

            if (mWeakReference.get() == null)
                return;

            LogUtils.i("Info", "start wechat pay Activity");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            mWeakReference.get().startActivity(intent);

        } catch (Exception e) {
            if(LogUtils.isDebug()){
                LogUtils.i("Info", "支付异常");
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {


        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "onScaleChanged", WEBVIEWCLIENTPATH + ".onScaleChanged", WebView.class, float.class, float.class)) {
            super.onScaleChanged(view, oldScale, newScale);
            return;
        }

        LogUtils.i("Info", "onScaleChanged:" + oldScale + "   n:" + newScale);
        if (newScale - oldScale > CONSTANTS_ABNORMAL_BIG) {
            view.setInitialScale((int) (oldScale / newScale * 100));
        }

    }
}
