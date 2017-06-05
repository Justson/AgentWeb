package com.just.library;

import android.os.Build;
import android.webkit.JavascriptInterface;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by cenxiaozhong on 2017/5/13.
 */

public abstract class JsBaseInterfaceHolder implements JsInterfaceHolder{

    private AgentWeb.SecurityType mSecurityType;
    protected JsBaseInterfaceHolder(AgentWeb.SecurityType securityType){
      this.mSecurityType =securityType;
    }
    @Override
    public boolean checkObject(Object v) {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN_MR1)
            return true;
        if(AgentWebConfig.WEBVIEW_TYPE==AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE)
            return true;
        boolean tag=false;
        Class clazz=v.getClass();

        Method[] mMethods= clazz.getMethods();

        for(Method mMethod:mMethods){

            Annotation[]mAnnotations= mMethod.getAnnotations();

            for(Annotation mAnnotation:mAnnotations){

                if(mAnnotation instanceof JavascriptInterface){
                    tag=true;
                    break;
                }

            }
            if(tag)
                break;
        }

        return tag;
    }

    protected boolean checkSecurity(){
        return mSecurityType!= AgentWeb.SecurityType.strict ?true:AgentWebConfig.WEBVIEW_TYPE==AgentWebConfig.WEBVIEW_AGENTWEB_SAFE_TYPE?true:Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN_MR1;
    }


}
