package com.just.agentweb;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

/**
 * Created by cenxiaozhong on 2017/6/21.
 * source code  https://github.com/Justson/AgentWeb
 */

public interface DownloadListener {


    /**
     * @param url                下载链接
     * @param userAgent          userAgent
     * @param contentDisposition contentDisposition
     * @param mimetype           资源的媒体类型
     * @param contentLength      文件长度
     * @param extra              下载配置 ， 用户可以通过 Extra 修改下载icon ， 关闭进度条 ， 或者是否强制下载。
     * @return true 表示用户处理了该下载事件 ， false 交给 AgentWeb 下载
     */
    boolean start(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra);


    /**
     * @param path 文件的绝对路径
     * @param url  下载的地址
     * @param throwable    如果异常，返回给用户异常
     * @return true 表示用户处理了下载完成后续的事件 ，false 默认交给AgentWeb 处理
     */
    boolean result(String path, String url, Throwable throwable);

    class DownloadListenerAdapter implements DownloadListener {

        @Override
        public boolean start(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
            return false;
        }

        @Override
        public boolean result(String path, String url, Throwable e) {
            return false;
        }
    }

    abstract class Extra {
        private boolean isForce;
        private boolean enableIndicator;
        private DefaultMsgConfig.DownloadMsgConfig mDownloadMsgConfig;
        private int icon = -1;
        private boolean isParallelDownload = false;

        public Extra setForceDownload(boolean force) {
            isForce = force;
            return this;
        }

        public Extra setEnableIndicator(boolean enableIndicator) {
            this.enableIndicator = enableIndicator;
            return this;
        }


        public Extra setDownloadMsgConfig(@NonNull  DefaultMsgConfig.DownloadMsgConfig downloadMsgConfig) {
            if (downloadMsgConfig != null) {
                mDownloadMsgConfig = downloadMsgConfig;
            }
            return this;
        }


        public Extra setIcon(@DrawableRes int icon) {
            this.icon = icon;
            return this;
        }

        public Extra setParallelDownload(boolean parallelDownload) {
            isParallelDownload = parallelDownload;
            return this;
        }


        public void build() {

        }

    }


}
