package com.just.agentweb;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by cenxiaozhong.
 * source code  https://github.com/Justson/AgentWeb
 */

public class DefaultWebClient extends MiddlewareWebClientBase {

    private WeakReference<Activity> mWeakReference = null;
    private static final int CONSTANTS_ABNORMAL_BIG = 7;
    private WebViewClient mWebViewClient;
    private boolean webClientHelper = true;
    private static final String ANDROID_WEBVIEWCLIENT_PATH = "android.webkit.WebViewClient";
    public static final String INTENT_SCHEME = "intent://";
    public static final String WEBCHAT_PAY_SCHEME = "weixin://wap/pay?";
    public static final String ALIPAYS_SCHEME = "alipays://";
    public static final String HTTP_SCHEME = "http://";
    public static final String HTTPS_SCHEME = "https://";
    private static final boolean hasAlipayLib;
    private static final String TAG = DefaultWebClient.class.getSimpleName();
    public static final int DERECT_OPEN_OTHER_APP = 1001;
    public static final int ASK_USER_OPEN_OTHER_APP = DERECT_OPEN_OTHER_APP >> 2;
    public static final int DISALLOW_OPEN_OTHER_APP = DERECT_OPEN_OTHER_APP >> 4;
    private int schemeHandleType = ASK_USER_OPEN_OTHER_APP;
    private boolean isInterceptUnkownScheme = true;
    private WeakReference<AgentWebUIController> mAgentWebUIController = null;
    private WebView mWebView;
    private DefaultMsgConfig.WebViewClientMsgCfg mMsgCfg = null;
    private Handler.Callback mCallback = null;
    private Method onMainFrameErrorMethod = null;
    private Object mPayTask; //alipay
    public static final String SCHEME_SMS = "sms:";
    private Set<String> mErrorUrlsSet = new HashSet<>();
    private Set<String> mWaittingFinishSet = new HashSet<>();

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

    @Deprecated
    DefaultWebClient(@NonNull Activity activity, WebViewClient client,  boolean webClientHelper, PermissionInterceptor permissionInterceptor, WebView webView) {
        super(client);
        this.mWebView = webView;
        this.mWebViewClient = client;
        mWeakReference = new WeakReference<Activity>(activity);
        this.webClientHelper = webClientHelper;
        mAgentWebUIController = new WeakReference<AgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(webView));
    }

    DefaultWebClient(Builder builder) {
        super(builder.client);
        this.mWebView = builder.webView;
        this.mWebViewClient = builder.client;
        mWeakReference = new WeakReference<Activity>(builder.activity);
        this.webClientHelper = builder.webClientHelper;
        mAgentWebUIController = new WeakReference<AgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(builder.webView));
        isInterceptUnkownScheme = builder.isInterceptUnkownScheme;

        LogUtils.i(TAG, "schemeHandleType:" + schemeHandleType);
        if (builder.schemeHandleType <= 0) {
            schemeHandleType = ASK_USER_OPEN_OTHER_APP;
        } else {
            schemeHandleType = builder.schemeHandleType;
        }
        this.mMsgCfg = builder.mCfg;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        LogUtils.i(TAG, " DefaultWebClient shouldOverrideUrlLoading:" + request.getUrl());
        int tag = -1;

        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", ANDROID_WEBVIEWCLIENT_PATH + ".shouldOverrideUrlLoading", WebView.class, WebResourceRequest.class) && (((tag = 1) > 0) && super.shouldOverrideUrlLoading(view, request))) {
            return true;
        }

        String url = request.getUrl().toString();

        if (url.startsWith(HTTP_SCHEME) || url.startsWith(HTTPS_SCHEME)) {
            return (webClientHelper && hasAlipayLib && isAlipay(view, url));
        }

        if (!webClientHelper) {
            return false;
        }
        if (handleLinked(url)) {
            return true;
        }

        LogUtils.i(TAG, "helper:" + webClientHelper + "  isInterceptUnkownScheme:" + isInterceptUnkownScheme);

        if (url.startsWith(INTENT_SCHEME)) { // intent
            LogUtils.i(TAG, "" + INTENT_SCHEME + "   intercept:" + true);
            handleIntentUrl(url);
            return true;
        }

        if (url.startsWith(WEBCHAT_PAY_SCHEME)) {//微信支付
            startActivity(url);
            return true;
        }

        if (url.startsWith(ALIPAYS_SCHEME) && openOtherPage(url)) {
            LogUtils.i(TAG, "alipays scheme open alipay ~~ ");
            return true;
        }

        if (queryActivies(url) > 0 && handleOtherScheme(url)) {
            LogUtils.i(TAG, "intercept OtherPageScheme");
            return true;
        }
        if (isInterceptUnkownScheme) {
            LogUtils.i(TAG, "intercept InterceptUnkownScheme :" + request.getUrl());
            return true;
        }

        if (tag > 0)
            return false;

        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return super.shouldInterceptRequest(view, url);
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    private boolean handleOtherScheme(String url) {

        LogUtils.i(TAG, "schemeHandleType:" + schemeHandleType + "   :" + mAgentWebUIController.get() + " url:" + url);
        switch (schemeHandleType) {

            case DERECT_OPEN_OTHER_APP: //直接打开其他App
                openOtherPage(url);
                return true;
            case ASK_USER_OPEN_OTHER_APP:  //咨询用户是否打开其他App
                if (mAgentWebUIController.get() != null) {
                    mAgentWebUIController.get()
                            .onAskOpenPage(this.mWebView,
                                    mWebView.getUrl(),
                                    String.format(mMsgCfg.getLeaveApp(), AgentWebUtils.getApplicationName(mWebView.getContext())),
                                    mMsgCfg.getConfirm(),
                                    mMsgCfg.getTitle(), getCallback(url));
                }
                return true;
            default://默认不打开
                return false;
        }
    }


    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        LogUtils.i(TAG, "shouldInterceptRequest:" + request.getUrl().toString());
        return super.shouldInterceptRequest(view, request);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtils.i(TAG, "shouldOverrideUrlLoading --->  url:" + url);


        int tag = -1;

        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", ANDROID_WEBVIEWCLIENT_PATH + ".shouldOverrideUrlLoading", WebView.class, String.class) && (((tag = 1) > 0) && super.shouldOverrideUrlLoading(view, url))) {
            return true;
        }

        if (url.startsWith(HTTP_SCHEME) || url.startsWith(HTTPS_SCHEME)) {
            return (webClientHelper && hasAlipayLib && isAlipay(view, url));
        }

        if (!webClientHelper) {
            return false;
        }
        if (handleLinked(url)) { //电话 ， 邮箱 ， 短信
            return true;
        }
        if (url.startsWith(INTENT_SCHEME)) { //Intent scheme
            handleIntentUrl(url);
            return true;
        }

        if (url.startsWith(WEBCHAT_PAY_SCHEME)) { //微信支付
            startActivity(url);
            return true;
        }
        if (url.startsWith(ALIPAYS_SCHEME) && openOtherPage(url)) {//支付宝
            return true;
        }

        if (queryActivies(url) > 0 && handleOtherScheme(url)) { //打开Scheme 相对应的页面
            LogUtils.i(TAG, "intercept OtherAppScheme");
            return true;
        }
        if (isInterceptUnkownScheme) { // 手机里面没有页面能匹配到该链接 ， 也就是无法处理的scheme返回True，拦截下来。
            LogUtils.i(TAG, "intercept InterceptUnkownScheme : " + url);
            return true;
        }

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

            if (openOtherPage(intentUrl)) {
                return;
            }
        } catch (Throwable e) {
            if (LogUtils.isDebug())
                e.printStackTrace();
        }


    }

    private boolean openOtherPage(String intentUrl) {
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
        } catch (Throwable ignore) {
            if (LogUtils.isDebug()) {
                ignore.printStackTrace();
            }
        }

        return false;
    }

    private boolean isAlipay(final WebView view, String url) {

        try {

            Activity mActivity = null;
            if ((mActivity = mWeakReference.get()) == null)
                return false;
            /**
             * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
             */
            if (mPayTask == null) {
                Class clazz = Class.forName("com.alipay.sdk.app.PayTask");
                Constructor<?> mConstructor = clazz.getConstructor(Activity.class);
                mPayTask = mConstructor.newInstance(mActivity);
            }
            final PayTask task = (PayTask) mPayTask;
            boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                @Override
                public void onPayResult(final H5PayResultModel result) {
                    final String url = result.getReturnUrl();
                    if (!TextUtils.isEmpty(url)) {
                        AgentWebUtils.runInUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.loadUrl(url);
                            }
                        });
                    }
                }
            });
            LogUtils.i(TAG, "alipay-isIntercepted:" + isIntercepted + "  url:" + url);
            return isIntercepted;
        } catch (Throwable ignore) {
            if (AgentWebConfig.DEBUG) {
                ignore.printStackTrace();
            }
        }
        return false;
    }


    private boolean handleLinked(String url) {
        if (url.startsWith(WebView.SCHEME_TEL)
                || url.startsWith(SCHEME_SMS)
                || url.startsWith(WebView.SCHEME_MAILTO)
                || url.startsWith(WebView.SCHEME_GEO)) {
            try {
                Activity mActivity = null;
                if ((mActivity = mWeakReference.get()) == null)
                    return false;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mActivity.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
                if (AgentWebConfig.DEBUG) {
                    ignored.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        LogUtils.i(TAG, "onPageStarted");

        if (!mWaittingFinishSet.contains(url)) {
            mWaittingFinishSet.add(url);
        }
        super.onPageStarted(view, url, favicon);

    }


    //MainFrame Error
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "onReceivedError", ANDROID_WEBVIEWCLIENT_PATH + ".onReceivedError", WebView.class, int.class, String.class, String.class)) {
            super.onReceivedError(view, errorCode, description, failingUrl);
//            return;
        }
        LogUtils.i(TAG, "onReceivedError：" + description + "  CODE:" + errorCode);
        onMainFrameError(view, errorCode, description, failingUrl);
    }


    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "onReceivedError", ANDROID_WEBVIEWCLIENT_PATH + ".onReceivedError", WebView.class, WebResourceRequest.class, WebResourceError.class)) {
            super.onReceivedError(view, request, error);
//            return;
        }
        if (request.isForMainFrame()) {
            onMainFrameError(view,
                    error.getErrorCode(), error.getDescription().toString(),
                    request.getUrl().toString());
        }
        LogUtils.i(TAG, "onReceivedError:" + error.toString());
    }

    //
    private void onMainFrameError(WebView view, int errorCode, String description, String failingUrl) {
        LogUtils.i(TAG, "onMainFrameError:" + failingUrl + "  mWebViewClient:" + mWebViewClient);
        mErrorUrlsSet.add(failingUrl);
        if (this.mWebViewClient != null && webClientHelper) {  //下面逻辑判断开发者是否重写了 onMainFrameError 方法 ， 优先交给开发者处理
            Method mMethod = this.onMainFrameErrorMethod;
            if (mMethod != null || (this.onMainFrameErrorMethod = mMethod = AgentWebUtils.isExistMethod(mWebViewClient, "onMainFrameError", AgentWebUIController.class, WebView.class, int.class, String.class, String.class)) != null) {
                try {
                    mMethod.invoke(this.mWebViewClient, mAgentWebUIController.get(), view, errorCode, description, failingUrl);
                } catch (Throwable ignore) {
                    if (LogUtils.isDebug()) {
                        ignore.printStackTrace();
                    }
                }
                return;
            }
        }
        if (mAgentWebUIController.get() != null) {
            mAgentWebUIController.get().onMainFrameError(view, errorCode, description, failingUrl);
        }
    }


    @Override
    public void onPageFinished(WebView view, String url) {

        LogUtils.i(TAG, "onPageFinished:" + mErrorUrlsSet + "  contains:" + mErrorUrlsSet.contains(url));
        if (!mErrorUrlsSet.contains(url) && mWaittingFinishSet.contains(url)) {
            if (mAgentWebUIController.get() != null) {
                LogUtils.i(TAG, "onPageFinished onShowMainFrame");
                mAgentWebUIController.get().onShowMainFrame();
            }
        }
        if (mWaittingFinishSet.contains(url)) {
            mWaittingFinishSet.remove(url);
        }
        if (!mErrorUrlsSet.isEmpty()) {
            mErrorUrlsSet.clear();
        }
        super.onPageFinished(view, url);

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


        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "onScaleChanged", ANDROID_WEBVIEWCLIENT_PATH + ".onScaleChanged", WebView.class, float.class, float.class)) {
            super.onScaleChanged(view, oldScale, newScale);
            return;
        }

        LogUtils.i(TAG, "onScaleChanged:" + oldScale + "   n:" + newScale);
        if (newScale - oldScale > CONSTANTS_ABNORMAL_BIG) {
            view.setInitialScale((int) (oldScale / newScale * 100));
        }

    }


    private Handler.Callback getCallback(final String url) {
        if (this.mCallback != null) {
            return this.mCallback;
        }
        return this.mCallback = new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        openOtherPage(url);
                        break;
                }
                return true;
            }
        };
    }


    public static Builder createBuilder() {
        return new Builder();
    }

    public static class Builder {

        private Activity activity;
        private WebViewClient client;
        private boolean webClientHelper;
        private PermissionInterceptor permissionInterceptor;
        private WebView webView;
        private boolean isInterceptUnkownScheme;
        private int schemeHandleType;
        private DefaultMsgConfig.WebViewClientMsgCfg mCfg;

        public Builder setCfg(DefaultMsgConfig.WebViewClientMsgCfg cfg) {
            mCfg = cfg;
            return this;
        }

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setClient(WebViewClient client) {
            this.client = client;
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

        public Builder setInterceptUnkownScheme(boolean interceptUnkownScheme) {
            this.isInterceptUnkownScheme = interceptUnkownScheme;
            return this;
        }

        public Builder setSchemeHandleType(int schemeHandleType) {
            this.schemeHandleType = schemeHandleType;
            return this;
        }

        public DefaultWebClient build() {
            return new DefaultWebClient(this);
        }
    }

    public static enum OpenOtherPageWays {
        DERECT(DefaultWebClient.DERECT_OPEN_OTHER_APP), ASK(DefaultWebClient.ASK_USER_OPEN_OTHER_APP), DISALLOW(DefaultWebClient.DISALLOW_OPEN_OTHER_APP);
        int code;

        OpenOtherPageWays(int code) {
            this.code = code;
        }
    }
}
