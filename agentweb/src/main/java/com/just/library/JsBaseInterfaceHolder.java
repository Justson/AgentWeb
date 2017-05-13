package com.just.library;

import android.os.Build;
import android.webkit.JavascriptInterface;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by cenxiaozhong on 2017/5/13.
 */

public abstract class JsBaseInterfaceHolder implements JsInterfaceHolder{

    @Override
    public boolean checkObject(Object v) {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN_MR1)
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
}
