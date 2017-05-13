package com.just.library;

import android.support.v4.util.ArrayMap;

/**
 * Created by cenxiaozhong on 2017/5/13.
 */

public interface JsInterfaceHolder {

    JsInterfaceHolder addJavaObjects(ArrayMap<String,Object> maps);

    JsInterfaceHolder addJavaObject(String k,Object v);

    boolean checkObject(Object v) ;

}
