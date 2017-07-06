package com.just.library;

import android.support.v4.util.ArrayMap;

import java.util.Map;

/**
 * Created by cenxiaozhong on 2017/7/5.
 */

public  class HttpHeaders {


    public static HttpHeaders create(){
       return new HttpHeaders();
    }

    private Map<String,String>headers=null;

    HttpHeaders(){
        headers= new ArrayMap<>();
    }

    public Map<String,String> getHeaders(){
        return headers;
    }

    public void additionalHttpHeader(String k,String v){
        headers.put(k,v);
    }
    public void removeHttpHeader(String k){
        headers.remove(k);
    }

    public boolean isEmptyHeaders(){
        return headers==null||headers.isEmpty();
    }


    @Override
    public String toString() {
        return "HttpHeaders{" +
                "headers=" + headers +
                '}';
    }
}
