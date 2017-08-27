package com.just.library;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by cenxiaozhong on 2017/5/28.
 */

public class WrapperWebViewClient extends WebViewClient {


    private WebViewClient mWebViewClient;
    private static final String TAG=WrapperWebViewClient.class.getSimpleName();
    WrapperWebViewClient (WebViewClient client){
        this.mWebViewClient=client;
    }

    @Deprecated
    public boolean shouldOverrideUrlLoading(WebView view, String url) {


        if(mWebViewClient!=null){
           return mWebViewClient.shouldOverrideUrlLoading(view,url);
        }

        return false;
    }


    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        LogUtils.i(TAG,"loading request");
        if(mWebViewClient!=null){
            return mWebViewClient.shouldOverrideUrlLoading(view,request);
        }
        return super.shouldOverrideUrlLoading(view, request);
    }


    public void onPageStarted(WebView view, String url, Bitmap favicon) {

        if(mWebViewClient!=null){
             mWebViewClient.onPageStarted(view,url,favicon);
            return;
        }
        super.onPageStarted(view,url,favicon);
    }


    public void onPageFinished(WebView view, String url) {
        if(mWebViewClient!=null){
            mWebViewClient.onPageFinished(view,url);
            return;
        }
        super.onPageFinished(view,url);
    }


    public void onLoadResource(WebView view, String url) {
        if(mWebViewClient!=null){
            mWebViewClient.onLoadResource(view,url);
            return;
        }
        super.onLoadResource(view,url);
    }


    public void onPageCommitVisible(WebView view, String url) {
        if(mWebViewClient!=null){
            mWebViewClient.onPageCommitVisible(view,url);return;
        }
        super.onPageCommitVisible(view,url);
    }


    @Deprecated
    public WebResourceResponse shouldInterceptRequest(WebView view,
                                                      String url) {
        if(mWebViewClient!=null){
            return mWebViewClient.shouldInterceptRequest(view,url);
        }
        return  super.shouldInterceptRequest(view,url);
    }


    public WebResourceResponse shouldInterceptRequest(WebView view,
                                                      WebResourceRequest request) {

        if(mWebViewClient!=null){
            return mWebViewClient.shouldInterceptRequest(view,request);
        }
        return super.shouldInterceptRequest(view, request);
    }


    @Deprecated
    public void onTooManyRedirects(WebView view, Message cancelMsg,
                                   Message continueMsg) {
        if(mWebViewClient!=null){
             mWebViewClient.onTooManyRedirects(view,cancelMsg,continueMsg);
            return;
        }
       super.onTooManyRedirects(view,cancelMsg,continueMsg);
    }



    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {

        if(mWebViewClient!=null){
            mWebViewClient.onReceivedError(view,errorCode,description,failingUrl);
            return;
        }
        super.onReceivedError(view,errorCode,description,failingUrl);
    }


    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

        if(mWebViewClient!=null){
            mWebViewClient.onReceivedError(view,request,error);
            return;
        }

        super.onReceivedError(view,request,error);
    }

    public void onReceivedHttpError(
            WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {

        if(mWebViewClient!=null){
            mWebViewClient.onReceivedHttpError(view,request,errorResponse);
            return;
        }
        super.onReceivedHttpError(view,request,errorResponse);


    }


    public void onFormResubmission(WebView view, Message dontResend,
                                   Message resend) {

        if(mWebViewClient!=null){
            mWebViewClient.onFormResubmission(view,dontResend,resend);
            return;
        }
        super.onFormResubmission(view,dontResend,resend);
    }



    public void doUpdateVisitedHistory(WebView view, String url,
                                       boolean isReload) {

        if(mWebViewClient!=null){
            mWebViewClient.doUpdateVisitedHistory(view,url,isReload);
            return;
        }
        super.doUpdateVisitedHistory(view,url,isReload);
    }


    public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                   SslError error) {
        if(mWebViewClient!=null){
            mWebViewClient.onReceivedSslError(view,handler,error);
            return;
        }
        super.onReceivedSslError(view,handler,error);
    }


    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if(mWebViewClient!=null){
            mWebViewClient.onReceivedClientCertRequest(view,request);
            return;
        }
        super.onReceivedClientCertRequest(view,request);
    }


    public void onReceivedHttpAuthRequest(WebView view,
                                          HttpAuthHandler handler, String host, String realm) {
        if(mWebViewClient!=null){
            mWebViewClient.onReceivedHttpAuthRequest(view,handler,host,realm);
            return;
        }
        super.onReceivedHttpAuthRequest(view,handler,host,realm);
    }


    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        if(mWebViewClient!=null){
           return mWebViewClient.shouldOverrideKeyEvent(view,event);

        }

        return super.shouldOverrideKeyEvent(view,event);
    }


    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {

        if(mWebViewClient!=null){
            mWebViewClient.onUnhandledKeyEvent(view,event);
            return;
        }
        super.onUnhandledKeyEvent(view,event);
    }





    public void onScaleChanged(WebView view, float oldScale, float newScale) {

        if(mWebViewClient!=null){
            mWebViewClient.onScaleChanged(view,oldScale,newScale);
            return;
        }
        super.onScaleChanged(view,oldScale,newScale);
    }


    public void onReceivedLoginRequest(WebView view, String realm,
                                       String account, String args) {

        if(mWebViewClient!=null){
            mWebViewClient.onReceivedLoginRequest(view,realm,account,args);
            return;
        }
        super.onReceivedLoginRequest(view,realm,account,args);
    }
}
