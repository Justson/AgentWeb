package com.just.agentweb;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Created by cenxiaozhong on 2018/2/4.
 */

public interface AgentWebDownloader<T extends AgentWebDownloader.Extra> extends DownloadingService {

    void download(T t);


    /**
     *
     */
    abstract class Extra implements Serializable {


        protected boolean isForceDownload = false;
        protected boolean enableIndicator = true;
        @DrawableRes
        protected int icon = -1;
        protected boolean isParallelDownload = true;
        protected boolean isOpenBreakPointDownload = true;
        protected String url;
        protected String userAgent;
        protected String contentDisposition;
        protected String mimetype;
        protected long contentLength;
        // 超时时长
        protected long downloadTimeOut = 2 * 60 * 1000 * 60;
        // 连接超时
        protected long connectTimeOut = 10 * 1000;


        protected Extra() {

        }

        public String getUrl() {
            return url;
        }

        protected Extra setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getUserAgent() {
            return userAgent;
        }

        protected Extra setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public String getContentDisposition() {
            return contentDisposition;
        }

        protected Extra setContentDisposition(String contentDisposition) {
            this.contentDisposition = contentDisposition;
            return this;
        }

        public String getMimetype() {
            return mimetype;
        }

        protected Extra setMimetype(String mimetype) {
            this.mimetype = mimetype;
            return this;
        }

        public long getContentLength() {
            return contentLength;
        }

        protected Extra setContentLength(long contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public boolean isForceDownload() {
            return isForceDownload;
        }

        public boolean isEnableIndicator() {
            return enableIndicator;
        }


        public long getDownloadTimeOut() {
            return downloadTimeOut;
        }

        public void setDownloadTimeOut(long downloadTimeOut) {
            this.downloadTimeOut = downloadTimeOut;
        }

        public long getConnectTimeOut() {
            return connectTimeOut;
        }

        public void setConnectTimeOut(long connectTimeOut) {
            this.connectTimeOut = connectTimeOut;
        }

        public int getIcon() {
            return icon;
        }

        public boolean isParallelDownload() {
            return isParallelDownload;
        }

        public boolean isOpenBreakPointDownload() {
            return isOpenBreakPointDownload;
        }

        public Extra setOpenBreakPointDownload(boolean openBreakPointDownload) {
            isOpenBreakPointDownload = openBreakPointDownload;
            return this;
        }

        public Extra setForceDownload(boolean force) {
            isForceDownload = force;
            return this;
        }

        public Extra setEnableIndicator(boolean enableIndicator) {
            this.enableIndicator = enableIndicator;
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


    }

    abstract class ExtraService extends Extra {

        public abstract void performReDownload();
    }


}
