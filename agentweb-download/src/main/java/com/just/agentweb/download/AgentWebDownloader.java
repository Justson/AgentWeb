/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.just.agentweb.download;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * @author cenxiaozhong
 * @date 2018/2/24
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
        /**
         * 超时时长默认为两小时
         */
        protected long downloadTimeOut = Long.MAX_VALUE;
        // 连接超时， 默认10s
        protected int connectTimeOut = 10 * 1000;
        /**
         * 以1KB位单位，默认60s ， 如果一秒钟无法从网络中读取数据满1KB，则抛出异常 。
         */
        protected int blockMaxTime = 10 * 60 * 1000;


        protected Extra() {

        }


        public int getBlockMaxTime() {
            return blockMaxTime;
        }

        public Extra setBlockMaxTime(int blockMaxTime) {
            this.blockMaxTime = blockMaxTime;
            return this;
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

        public Extra setDownloadTimeOut(long downloadTimeOut) {
            this.downloadTimeOut = downloadTimeOut;
            return this;
        }

        public int getConnectTimeOut() {
            return connectTimeOut;
        }

        public Extra setConnectTimeOut(int connectTimeOut) {
            this.connectTimeOut = connectTimeOut;
            return this;
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
