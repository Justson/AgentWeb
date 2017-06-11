package com.just.library;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;

/**
 * <b>@项目名：</b> agentweb<br>
 * <b>@包名：</b>com.just.library<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> <br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 *     source code  https://github.com/Justson/AgentWeb
 */

public class DefaultWebClient extends WrapperWebViewClient {

    private WebViewClientCallbackManager mWebViewClientCallbackManager;
    private WeakReference<Activity>mWeakReference=null;
    private static final int CONSTANTS_ABNORMAL_BIG=7;
    private WebViewClient mWebViewClient;
    private boolean webClientHelper=false;
    private static final String WEBVIEWCLIENTPATH="android.webkit.WebViewClient";
    DefaultWebClient(@NonNull Activity activity, WebViewClient client, WebViewClientCallbackManager manager,boolean webClientHelper) {
        super(client);
        this.mWebViewClient=client;
        mWeakReference=new WeakReference<Activity>(activity);
        this.mWebViewClientCallbackManager=manager;
        this.webClientHelper=webClientHelper;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        LogUtils.i("Info", " DefaultWebClient shouldOverrideUrlLoading");
        if(webClientHelper&&handleNormalLinked(request.getUrl()+"")){
            return true;
        }
        int tag=-1;

        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", WEBVIEWCLIENTPATH + ".shouldOverrideUrlLoading", WebView.class, WebResourceRequest.class)&&(((tag=1)>0)&&super.shouldOverrideUrlLoading(view,request))) {
            return true;
        }

        if(webClientHelper&&tag>0&&request.getUrl().toString().startsWith("intent://")){ //
            handleIntentUrl(request.getUrl()+"");
            return true;
        }

        if(tag>0)
            return false;

        return super.shouldOverrideUrlLoading(view,request);
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        LogUtils.i("Info","shouldOverrideUrlLoading --->  url:"+url);
        if(webClientHelper&&handleNormalLinked(url)){
            return true;
        }

        int tag=-1;

        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "shouldOverrideUrlLoading", WEBVIEWCLIENTPATH + ".shouldOverrideUrlLoading", WebView.class, String.class)&&(((tag=1)>0)&&super.shouldOverrideUrlLoading(view,url))) {
            return true;
        }

        if(webClientHelper&&tag>0&&url.startsWith("intent://")){ //拦截
            handleIntentUrl(url);
            return true;
        }

        if(tag>0)
            return false;


        return super.shouldOverrideUrlLoading(view, url);
    }


    private void handleIntentUrl(String intentUrl){
        try {

            Intent intent=null;
            if(TextUtils.isEmpty(intentUrl)||!intentUrl.startsWith("intent://"))
                return ;

            Activity mActivity=null;
            if((mActivity=mWeakReference.get())==null)
                return;
            PackageManager packageManager =mActivity.getPackageManager();
            intent = new Intent().parseUri(intentUrl, Intent.URI_INTENT_SCHEME);
            ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            LogUtils.i("Info","resolveInfo:"+info+"   package:"+intent.getPackage());
            if (info != null) {  //跳到该应用
                mActivity.startActivity(intent);
                return;
            }
            intent=new Intent().setData(Uri.parse("market://details?id=" + intent.getPackage()));
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
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    private boolean handleNormalLinked(String url){
        if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                Activity mActivity=null;
                if((mActivity=mWeakReference.get())==null)
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
        if(AgentWebConfig.WEBVIEW_TYPE==AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE&&mWebViewClientCallbackManager.getPageLifeCycleCallback()!=null){
            mWebViewClientCallbackManager.getPageLifeCycleCallback().onPageStarted(view,url,favicon);
        }
        super.onPageStarted(view, url, favicon);

    }



    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        LogUtils.i("Info", "onReceivedError");
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        LogUtils.i("Info", "onReceivedError");

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if(AgentWebConfig.WEBVIEW_TYPE==AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE&&mWebViewClientCallbackManager.getPageLifeCycleCallback()!=null){
            mWebViewClientCallbackManager.getPageLifeCycleCallback().onPageFinished(view,url);
        }
        super.onPageFinished(view, url);

        LogUtils.i("Info", "onPageFinished");
    }



    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        LogUtils.i("Info", "shouldOverrideKeyEvent");
        return super.shouldOverrideKeyEvent(view, event);
    }


    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {


        if (AgentWebUtils.isOverriedMethod(mWebViewClient, "onScaleChanged", WEBVIEWCLIENTPATH + ".onScaleChanged", WebView.class, float.class,float.class)) {
            super.onScaleChanged(view, oldScale, newScale);
            return;
        }

        LogUtils.i("Info","onScaleChanged:"+oldScale+"   n:"+newScale);
        if (newScale - oldScale > CONSTANTS_ABNORMAL_BIG) {
            view.setInitialScale((int) (oldScale / newScale * 100));
        }

    }
}
