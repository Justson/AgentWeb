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
import java.util.Map;

/**
 * @author cenxiaozhong
 * @date 2019/2/8
 * @since 1.0.0
 */
public class Extra implements Serializable, Cloneable {

    /**
     * 强制下肢
     */
    protected boolean mIsForceDownload = false;
    /**
     * 显示系统通知
     */
    protected boolean mEnableIndicator = true;
    /**
     * 通知icon
     */
    @DrawableRes
    protected int mIcon = R.drawable.ic_file_download_black_24dp;
    /**
     * 并行下载
     */
    protected boolean mIsParallelDownload = true;
    /**
     * 断点续传，分块传输该字段无效
     */
    protected boolean mIsBreakPointDownload = true;
    /**
     * 当前下载链接
     */
    protected String mUrl;
    /**
     * mContentDisposition ，提取文件名 ，如果ContentDisposition不指定文件名，则从url中提取文件名
     */
    protected String mContentDisposition;
    /**
     * 文件大小
     */
    protected long mContentLength;
    /**
     * 文件类型
     */
    protected String mMimetype;
    /**
     * UA
     */
    protected String mUserAgent;
    /**
     * Header
     */
    protected Map<String, String> mHeaders;
    /**
     * 下载文件完成，是否自动打开该文件
     */
    protected boolean mAutoOpen = false;
    /**
     * 超时时长默认为两小时
     */
    protected long downloadTimeOut = Long.MAX_VALUE;
    /**
     * 连接超时， 默认10s
     */
    protected int connectTimeOut = 10 * 1000;
    /**
     * 以8KB位单位，默认60s ，如果60s内无法从网络流中读满8KB数据，则抛出异常 。
     */
    protected int blockMaxTime = 10 * 60 * 1000;

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Extra setHeaders(Map<String, String> headers) {
        mHeaders = headers;
        return this;
    }

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
        return mUrl;
    }

    protected Extra setUrl(String url) {
        this.mUrl = url;
        return this;
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    protected Extra setUserAgent(String userAgent) {
        this.mUserAgent = userAgent;
        return this;
    }

    public String getContentDisposition() {
        return mContentDisposition;
    }

    protected Extra setContentDisposition(String contentDisposition) {
        this.mContentDisposition = contentDisposition;
        return this;
    }

    public String getMimetype() {
        return mMimetype;
    }

    protected Extra setMimetype(String mimetype) {
        this.mMimetype = mimetype;
        return this;
    }

    public long getContentLength() {
        return mContentLength;
    }

    protected Extra setContentLength(long contentLength) {
        this.mContentLength = contentLength;
        return this;
    }

    public boolean isForceDownload() {
        return mIsForceDownload;
    }

    public boolean isEnableIndicator() {
        return mEnableIndicator;
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
        return mIcon;
    }

    public boolean isParallelDownload() {
        return mIsParallelDownload;
    }

    public boolean isBreakPointDownload() {
        return mIsBreakPointDownload;
    }

    public Extra setBreakPointDownload(boolean breakPointDownload) {
        mIsBreakPointDownload = breakPointDownload;
        return this;
    }

    public Extra setForceDownload(boolean force) {
        mIsForceDownload = force;
        return this;
    }

    public Extra setEnableIndicator(boolean enableIndicator) {
        this.mEnableIndicator = enableIndicator;
        return this;
    }


    public Extra setIcon(@DrawableRes int icon) {
        this.mIcon = icon;
        return this;
    }

    public Extra setParallelDownload(boolean parallelDownload) {
        mIsParallelDownload = parallelDownload;
        return this;
    }

    public Extra addHeader(String key, String value) {
        if (this.mHeaders == null) {
            this.mHeaders = new android.support.v4.util.ArrayMap<>();
        }
        this.mHeaders.put(key, value);
        return this;
    }

    public Extra setAutoOpen(boolean autoOpen) {
        mAutoOpen = autoOpen;
        return this;
    }

    public boolean isAutoOpen() {
        return mAutoOpen;
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new Extra();
    }
}
