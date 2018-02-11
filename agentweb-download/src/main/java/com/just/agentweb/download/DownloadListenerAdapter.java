package com.just.agentweb.download;

/**
 * Created by cenxiaozhong on 2018/2/11.
 */

public class DownloadListenerAdapter implements DownloadListener, DownloadingListener {


    @Override
    public boolean start(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, AgentWebDownloader.Extra extra) {
        return false;
    }

    @Override
    public void onBindService(String url, DownloadingService downloadingService) {

    }

    @Override
    public void progress(String url, long downloaded, long length, long usedTime) {

    }

    @Override
    public void onUnbindService(String url, DownloadingService downloadingService) {

    }

    @Override
    public boolean result(String path, String url, Throwable e) {
        return false;
    }
}
