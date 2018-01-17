package com.just.agentweb;

/**
 * Created by cenxiaozhong on 2017/6/21.
 * source code  https://github.com/Justson/AgentWeb
 */

public interface DownloadResultListener {

    /**
     *
     * @param path 用户文件的绝对路径
     */
    void success(String path);

    /**
     *
     * @param path 文件的绝对路径
     * @param resUrl 下载的地址
     * @param cause 错误的原因
     * @param e 如果异常，返回给用户异常
     */
    void error(String path,String resUrl,String cause,Throwable e);

}
