package com.just.library;

import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/5/26.
 */

public abstract class BaseJsEntraceAccess implements JsEntraceAccess {

    private WebView mWebView;
    public static final String TAG=BaseJsEntraceAccess.class.getSimpleName();
    BaseJsEntraceAccess(WebView webView){
        this.mWebView=webView;
    }

    @Override
    public void callJs(String js, final ValueCallback<String> callback) {

        LogUtils.i(TAG,"method callJs:"+js);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.evaluateJs(js, callback);
        } else {
            this.loadJs(js);
        }


    }
    @Override
    public void callJs(String js) {
        this.callJs(js,  null);
    }


    private void loadJs(String js) {

        mWebView.loadUrl(js);

    }
    private void evaluateJs(String js, final ValueCallback<String>callback){

        mWebView.evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (callback != null)
                    callback.onReceiveValue(value);
            }
        });
    }


    @Override
    public void quickCallJs(String method, ValueCallback<String> callback,String... params) {

        StringBuilder sb=new StringBuilder();
        sb.append("javascript:"+method);
        if(params==null||params.length==0){
            sb.append("()");
        }else{
            sb.append("(").append(concat(params)).append(")");
        }


        callJs(sb.toString(),callback);

    }

    private String concat(String...params){

        StringBuilder mStringBuilder=new StringBuilder();

        for(int i=0;i<params.length;i++){
            String param=params[i];
            if(!AgentWebUtils.isJson(param)){

                mStringBuilder.append("\"").append(param).append("\"");
            }else{
                mStringBuilder.append(param);
            }

            if(i!=params.length-1){
                mStringBuilder.append(" , ");
            }

        }

        return mStringBuilder.toString();
    }


    @Override
    public void quickCallJs(String method, String... params) {

        this.quickCallJs(method,null,params);
    }

    @Override
    public void quickCallJs(String method) {
        this.quickCallJs(method,(String[])null);
    }
}
