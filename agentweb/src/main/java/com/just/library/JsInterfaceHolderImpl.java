package com.just.library;

import android.support.v4.util.ArrayMap;
import android.webkit.WebView;

import java.util.Map;
import java.util.Set;

/**
 * Created by cenxiaozhong on 2017/5/13.
 */

public class JsInterfaceHolderImpl extends JsBaseInterfaceHolder {

    static JsInterfaceHolderImpl getJsInterfaceHolder(WebView webView){

        return new JsInterfaceHolderImpl(webView);
    }
    private WebView mWebView;
    JsInterfaceHolderImpl(WebView webView){
        this.mWebView=webView;
    }
    @Override
    public JsInterfaceHolder addJavaObjects(ArrayMap<String, Object> maps) {

        Set<Map.Entry<String,Object>> sets= maps.entrySet();
        for(Map.Entry<String,Object> mEntry:sets){


           Object v= mEntry.getValue();
           boolean t=checkObject(v);
            if(!t)
                throw new JsInterfaceObjectException("this object has not offer method javascript to call");

            else
                addJavaObjectDirect(mEntry.getKey(),v);
        }

        return null;
    }

    @Override
    public JsInterfaceHolder addJavaObject(String k, Object v) {
        boolean t=checkObject(v);
        if(!t)
            throw new JsInterfaceObjectException("this object has not javascriptInterface");

        else
            addJavaObjectDirect(k,v);
        return this;
    }
    private JsInterfaceHolder addJavaObjectDirect(String k,Object v){
            this.mWebView.addJavascriptInterface(v,k);
        return this;
    }


}
