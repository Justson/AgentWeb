package com.just.agentweb.download;

/**
 * Created by cenxiaozhong on 2018/2/11.
 */

public interface DownloadingListener {

    /**
     * @param downloadingService 开发者可以通过 DownloadingService#shutdownNow 终止下载
     */
    void onBindService(String url, DownloadingService downloadingService);

    /**
     * @param url        下载链接
     * @param downloaded 已经下载的长度
     * @param length     文件的总大小
     * @param usedTime   耗时,单位ms
     *                   <p>
     *                   注意该方法回调在子线程 ，线程名 AsyncTask #XX or AgentWeb # XX
     */
    void progress(String url, long downloaded, long length, long usedTime);


    /**
     *
     * @param url
     * @param downloadingService 释放 downloadingService
     */
    void onUnbindService(String url, DownloadingService downloadingService);
}
