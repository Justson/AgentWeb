package com.just.agentweb;

import android.support.v4.util.ArrayMap;

/**
 * Created by cenxiaozhong on 2017/5/13.
 * source code  https://github.com/Justson/AgentWeb
 */

public interface JSInterfaceHolder {

    JSInterfaceHolder addJavaObjects(ArrayMap<String, Object> maps);

    JSInterfaceHolder addJavaObject(String k, Object v);

    boolean checkObject(Object v);

}
