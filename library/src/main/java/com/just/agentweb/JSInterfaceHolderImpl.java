package com.just.agentweb;

import android.support.v4.util.ArrayMap;
import android.webkit.WebView;

import java.util.Map;
import java.util.Set;

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source code  https://github.com/Justson/AgentWeb
 */

public class JSInterfaceHolderImpl extends JSBaseInterfaceHolder {

    private static final String TAG=JSInterfaceHolderImpl.class.getSimpleName();
    static JSInterfaceHolderImpl getJsInterfaceHolder(WebView webView, AgentWeb.SecurityType securityType) {

        return new JSInterfaceHolderImpl(webView,securityType);
    }

    private WebView mWebView;
    private AgentWeb.SecurityType mSecurityType;
    JSInterfaceHolderImpl(WebView webView, AgentWeb.SecurityType securityType) {
        super(securityType);
        this.mWebView = webView;
        this.mSecurityType=securityType;
    }

    @Override
    public JSInterfaceHolder addJavaObjects(ArrayMap<String, Object> maps) {



        if(!checkSecurity()){
            LogUtils.i(TAG,"The injected object is not safe, give up injection");
            return this;
        }
        LogUtils.i(TAG,"inject set:"+maps.size());

        Set<Map.Entry<String, Object>> sets = maps.entrySet();
        for (Map.Entry<String, Object> mEntry : sets) {

            Object v = mEntry.getValue();
            boolean t = checkObject(v);
            if (!t)
                throw new JSInterfaceObjectException("this object has not offer method javascript to call ,please check addJavascriptInterface annotation was be added");

            else
                addJavaObjectDirect(mEntry.getKey(), v);
        }

        return this;
    }

    @Override
    public JSInterfaceHolder addJavaObject(String k, Object v) {

        if(!checkSecurity()){
            return this;
        }
        boolean t = checkObject(v);
        if (!t)
            throw new JSInterfaceObjectException("this object has not offer method javascript to call , please check addJavascriptInterface annotation was be added");

        else
            addJavaObjectDirect(k, v);
        return this;
    }

    private JSInterfaceHolder addJavaObjectDirect(String k, Object v) {
        LogUtils.i(TAG, "k:" + k + "  v:" + v);
        this.mWebView.addJavascriptInterface(v, k);
        return this;
    }


}
