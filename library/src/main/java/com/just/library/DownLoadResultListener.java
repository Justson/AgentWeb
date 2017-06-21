package com.just.library;

/**
 * Created by cenxiaozhong on 2017/6/21.
 */

public interface DownLoadResultListener {


    void success(String path);

    void error(String path,String resUrl,String cause,Throwable e);

}
