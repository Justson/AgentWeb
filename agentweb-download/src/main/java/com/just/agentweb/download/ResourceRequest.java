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

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;

/**
 * @author cenxiaozhong
 * @date 2019/2/9
 * @since 1.0.0
 */
public class ResourceRequest<T extends DownloadTask> {
    private DownloadTask mDownloadTask;

    public static ResourceRequest with(Context context) {
        ResourceRequest resourceRequest = new ResourceRequest();
        resourceRequest.mDownloadTask = Rumtime.getInstance().getDefaultDownloadTask();
        resourceRequest.mDownloadTask.setContext(context);
        return resourceRequest;
    }

    public ResourceRequest url(@NonNull String url) {
        mDownloadTask.setUrl(url);
        return this;
    }

    public ResourceRequest target(@Nullable File target) {
        mDownloadTask.setFile(target);
        return this;
    }

    protected ResourceRequest setContentLength(long contentLength) {
        mDownloadTask.mContentLength = contentLength;
        return this;
    }


    public ResourceRequest setDownloadTimeOut(long downloadTimeOut) {
        mDownloadTask.downloadTimeOut = downloadTimeOut;
        return this;
    }

    public ResourceRequest setConnectTimeOut(int connectTimeOut) {
        mDownloadTask.connectTimeOut = connectTimeOut;
        return this;
    }

    public ResourceRequest setOpenBreakPointDownload(boolean openBreakPointDownload) {
        mDownloadTask.mIsBreakPointDownload = openBreakPointDownload;
        return this;
    }

    public ResourceRequest setForceDownload(boolean force) {
        mDownloadTask.mIsForceDownload = force;
        return this;
    }

    public ResourceRequest setEnableIndicator(boolean enableIndicator) {
        mDownloadTask.mEnableIndicator = enableIndicator;
        return this;
    }


    public ResourceRequest setIcon(@DrawableRes int icon) {
        mDownloadTask.mIcon = icon;
        return this;
    }

    public ResourceRequest setParallelDownload(boolean parallelDownload) {
        mDownloadTask.mIsParallelDownload = parallelDownload;
        return this;
    }

    public ResourceRequest addHeader(String key, String value) {
        if (mDownloadTask.mHeaders == null) {
            mDownloadTask.mHeaders = new android.support.v4.util.ArrayMap<>();
        }
        mDownloadTask.mHeaders.put(key, value);
        return this;
    }

    public ResourceRequest setAutoOpen(boolean autoOpen) {
        mDownloadTask.mAutoOpen = autoOpen;
        return this;
    }

    public File get() {
        safe();
        return DownloadImpl.getInstance().call(mDownloadTask);
    }

    private void safe() {
        if (null == mDownloadTask.getContext()) {
            throw new NullPointerException("context can't be null .");
        }
        if (TextUtils.isEmpty(mDownloadTask.getUrl())) {
            throw new NullPointerException("url can't be empty .");
        }
    }

    public void enqueue() {
        safe();
        DownloadImpl.getInstance().enqueue(mDownloadTask);
    }

    public void enqueue(DownloadListener downloadListener) {
        safe();
        mDownloadTask.setDownloadListener(downloadListener);
        DownloadImpl.getInstance().enqueue(mDownloadTask);
    }

}
