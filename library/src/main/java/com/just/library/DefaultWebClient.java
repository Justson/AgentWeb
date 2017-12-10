package com.just.library;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.List;

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
    private static final String TAG = DefaultWebClient.class.getSimpleName();

    public static final int DERECT_OPEN_OTHER_APP = 1001;
    public static final int ASK_USER_OPEN_OTHER_APP = DERECT_OPEN_OTHER_APP >> 2;
    public static final int DISALLOW_OPEN_OTHER_APP = DERECT_OPEN_OTHER_APP >> 4;
    public int scheme_handle_type = ASK_USER_OPEN_OTHER_APP;
    private boolean is_intercept_unkown_scheme = true;
    private WeakReference<AgentWebUIController> mAgentWebUIController = null;
    private WebView mWebView;

    static {
        boolean tag = true;
        try {
            Class.forName("com.alipay.sdk.app.PayTask");
        } catch (Throwable ignore) {
            tag = false;
        }
        hasAlipayLib = tag;

        LogUtils.i(TAG, "hasAlipayLib:" + hasAlipayLib);
    }


    DefaultWebClient(@NonNull Activity activity, WebViewClient client, WebViewClientCallbackManager manager, boolean webClientHelper, PermissionInterceptor permissionInterceptor, WebView webView) {
        super(client);
        this.mWebView = webView;
        this.mWebViewClient = client;
        mWeakReference = new WeakReference<Activity>(activity);
        this.mWebViewClientCallbackManager = manager;
        this.webClientHelper = webClientHelper;
        mAgentWebUIController = new WeakReference<AgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(webView));
    }

    DefaultWebClient(Builder builder) {
        super(builder.client);
        this.mWebView = builder.webView;
        this.mWebViewClient = builder.client;
        mWeakReference = new WeakReference<Activity>(builder.activity);
        this.mWebViewClientCallbackManager = builder.manager;
        this.webClientHelper = builder.webClientHelper;
        mAgentWebUIController = new WeakReference<AgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(builder.webView));
        is_intercept_unkown_scheme=builder.is_intercept_unkown_scheme;
        scheme_handle_type=builder.scheme_handle_type;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        LogUtils.i(TAG, " DefaultWebClient shouldOverrideUrlLoading:" + request.getUrl());
        if (webClientHelper && handleNormalLinked(request.getUrl() + "")) {
            return true;
        }
        int tag = -1;

        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", WEBVIEWCLIENTPATH + ".shouldOverrideUrlLoading", WebView.class, WebResourceRequest.class) && (((tag = 1) > 0) && super.shouldOverrideUrlLoading(view, request))) {
            return true;
        }
        if (request.getUrl().toString().startsWith("http://") || request.getUrl().toString().startsWith("https://")) {
            return false;
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

        if (webClientHelper && queryActivies(request.getUrl().toString()) > 0 && handleOtherAppScheme(request.getUrl().toString())) {
            LogUtils.i(TAG, "intercept OtherAppScheme");
            return true;
        }
        if (is_intercept_unkown_scheme) {
            LogUtils.i(TAG, "intercept is_intercept_unkown_scheme");
            return true;
        }

        if (tag > 0)
            return false;

        return super.shouldOverrideUrlLoading(view, request);
    }

    private boolean handleOtherAppScheme(String url) {

        LogUtils.i(TAG, "scheme_handle_type:" + scheme_handle_type + "   :" + mAgentWebUIController.get() + " url:" + url);
        switch (scheme_handle_type) {

            case DERECT_OPEN_OTHER_APP:
                openOtherApp(url);
                return true;
            case ASK_USER_OPEN_OTHER_APP:
                if (mAgentWebUIController.get() != null) {
                    mAgentWebUIController.get()
                            .onAskOpenOtherApp(this.mWebView,
                                    mWebView.getUrl(),
                                    "你需要离开"
                                            + getApplicationName(mWebView.getContext())
                                            + "前往其他应用吗?",
                                    "离开",
                                    "提示", getCallback(url));
                }
                return true;
            default:
                return false;
        }
    }

    public String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtils.i(TAG, "shouldOverrideUrlLoading --->  url:" + url);

        if (webClientHelper && handleNormalLinked(url)) {
            return true;
        }

        int tag = -1;

        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", WEBVIEWCLIENTPATH + ".shouldOverrideUrlLoading", WebView.class, String.class) && (((tag = 1) > 0) && super.shouldOverrideUrlLoading(view, url))) {
            return true;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return false;
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


    private int queryActivies(String url) {

        try {
            if (mWeakReference.get() == null) {
                return 0;
            }
            Intent intent = new Intent().parseUri(url, Intent.URI_INTENT_SCHEME);
            PackageManager mPackageManager = mWeakReference.get().getPackageManager();
            List<ResolveInfo> mResolveInfos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return mResolveInfos == null ? 0 : mResolveInfos.size();
        } catch (URISyntaxException ignore) {
            if (LogUtils.isDebug()) {
                ignore.printStackTrace();
            }
            return 0;
        }
    }

    private void handleIntentUrl(String intentUrl) {
        try {

            Intent intent = null;
            if (TextUtils.isEmpty(intentUrl) || !intentUrl.startsWith(INTENT_SCHEME))
                return;

            if (openOtherApp(intentUrl)) {
                return;
            }
            /*intent=new Intent().setData(Uri.parse("market://details?id=" + intent.getPackage()));
            info=packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            LogUtils.i(TAG,"resolveInfo:"+info);
            if (info != null) {  //跳到应用市场
                mActivity.startActivity(intent);
                return;
            }

            intent=new Intent().setData(Uri.parse("https://play.google.com/store/apps/details?id=" + intent.getPackage()));
            info=packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            LogUtils.i(TAG,"resolveInfo:"+info);
            if (info != null) {  //跳到浏览器
                mActivity.startActivity(intent);
                return;
            }*/
        } catch (Throwable e) {
            if (LogUtils.isDebug())
                e.printStackTrace();
        }


    }

    private boolean openOtherApp(String intentUrl) {
        try {
            Intent intent;
            Activity mActivity = null;
            if ((mActivity = mWeakReference.get()) == null)
                return true;
            PackageManager packageManager = mActivity.getPackageManager();
            intent = new Intent().parseUri(intentUrl, Intent.URI_INTENT_SCHEME);
            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            LogUtils.i(TAG, "resolveInfo:" + info + "   package:" + intent.getPackage());
            if (info != null) {  //跳到该应用
                mActivity.startActivity(intent);
                return true;
            }
        } catch (Throwable igonre) {
            if (LogUtils.isDebug()) {
                igonre.printStackTrace();
            }
        }

        return false;
    }

    private boolean isAlipay(final WebView view, String url) {

        try {
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
        } catch (Throwable ignore) {

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
        LogUtils.i(TAG, "onPageStarted");
        if (AgentWebConfig.WEBVIEW_TYPE == AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE && mWebViewClientCallbackManager.getPageLifeCycleCallback() != null) {
            mWebViewClientCallbackManager.getPageLifeCycleCallback().onPageStarted(view, url, favicon);
        }
        super.onPageStarted(view, url, favicon);

    }


    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        LogUtils.i(TAG, "onReceivedError：" + description + "  CODE:" + errorCode);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        LogUtils.i(TAG, "onReceivedError:" + error.toString());

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (AgentWebConfig.WEBVIEW_TYPE == AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE && mWebViewClientCallbackManager.getPageLifeCycleCallback() != null) {
            mWebViewClientCallbackManager.getPageLifeCycleCallback().onPageFinished(view, url);
        }
        super.onPageFinished(view, url);

        LogUtils.i(TAG, "onPageFinished");
    }


    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        LogUtils.i(TAG, "shouldOverrideKeyEvent");
        return super.shouldOverrideKeyEvent(view, event);
    }


    private void startActivity(String url) {


        try {

            if (mWeakReference.get() == null)
                return;

            LogUtils.i(TAG, "start wechat pay Activity");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            mWeakReference.get().startActivity(intent);

        } catch (Exception e) {
            if (LogUtils.isDebug()) {
                LogUtils.i(TAG, "支付异常");
                e.printStackTrace();
            }
        }


    }


    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {


        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "onScaleChanged", WEBVIEWCLIENTPATH + ".onScaleChanged", WebView.class, float.class, float.class)) {
            super.onScaleChanged(view, oldScale, newScale);
            return;
        }

        LogUtils.i(TAG, "onScaleChanged:" + oldScale + "   n:" + newScale);
        if (newScale - oldScale > CONSTANTS_ABNORMAL_BIG) {
            view.setInitialScale((int) (oldScale / newScale * 100));
        }

    }


    private Handler.Callback mCallback = null;

    public Handler.Callback getCallback(final String url) {
        if (this.mCallback != null) {
            return this.mCallback;
        }
        return this.mCallback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        openOtherApp(url);
                        break;
                }
                return true;
            }
        };
    }


    public static class Builder{

        //@NonNull Activity activity, WebViewClient client, WebViewClientCallbackManager manager, boolean webClientHelper, PermissionInterceptor permissionInterceptor, WebView webView


        private Activity activity;
        private WebViewClient client;
        private  WebViewClientCallbackManager manager;
        private boolean webClientHelper;
        private PermissionInterceptor permissionInterceptor;
        private WebView webView;
        private boolean is_intercept_unkown_scheme;
        private int scheme_handle_type;

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setClient(WebViewClient client) {
            this.client = client;
            return this;
        }

        public Builder setManager(WebViewClientCallbackManager manager) {
            this.manager = manager;
            return this;
        }

        public Builder setWebClientHelper(boolean webClientHelper) {
            this.webClientHelper = webClientHelper;
            return this;
        }

        public Builder setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
            this.permissionInterceptor = permissionInterceptor;
            return this;
        }

        public Builder setWebView(WebView webView) {
            this.webView = webView;
            return this;
        }

        public Builder setIs_intercept_unkown_scheme(boolean is_intercept_unkown_scheme) {
            this.is_intercept_unkown_scheme = is_intercept_unkown_scheme;
            return this;
        }

        public Builder setScheme_handle_type(int scheme_handle_type) {
            this.scheme_handle_type = scheme_handle_type;
            return this;
        }
    }

    static enum OpenOtherAppWays{
        DERECT(DefaultWebClient.DERECT_OPEN_OTHER_APP),ASK(DefaultWebClient.ASK_USER_OPEN_OTHER_APP),DISALLOW(DefaultWebClient.DISALLOW_OPEN_OTHER_APP);
        int code;
        OpenOtherAppWays(int code){
            this.code=code;
        }
    }
}
