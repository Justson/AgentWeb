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

import android.app.Activity;
import android.webkit.WebView;

import com.downloader.library.DownloadTask;
import com.just.agentweb.LogUtils;
import com.just.agentweb.PermissionInterceptor;

import java.io.Serializable;

/**
 * @author cenxiaozhong
 * @date 2019/2/8
 * @since 1.0.0
 */

public class ExtraServiceImpl extends DownloadTask implements Cloneable, Serializable {
    private static final String TAG = "ExtraServiceImpl";
    transient Activity mActivity;
    transient PermissionInterceptor mPermissionInterceptor;
    transient WebView mWebView;
    DefaultDownloadImpl mDefaultDownload;
    long mContentLength;
    boolean mIsCloneObject = false;


    @Override
    public ExtraServiceImpl setUrl(String url) {
        super.setUrl(url);
        return this;
    }

    @Override
    public ExtraServiceImpl setMimetype(String mimetype) {
        super.setMimetype(mimetype);
        return this;
    }

    @Override
    public ExtraServiceImpl setContentDisposition(String contentDisposition) {
        super.setContentDisposition(contentDisposition);
        return this;
    }

    @Override
    public ExtraServiceImpl setUserAgent(String userAgent) {
        super.setUserAgent(userAgent);
        return this;
    }

    @Override
    public long getContentLength() {
        return mContentLength;
    }

    @Override
    protected ExtraServiceImpl setContentLength(long contentLength) {
        this.mContentLength = contentLength;
        return this;
    }

    ExtraServiceImpl setActivity(Activity activity) {
        mActivity = activity;
        this.setContext(mActivity);
        return this;
    }

    ExtraServiceImpl setPermissionInterceptor(PermissionInterceptor permissionInterceptor) {
        mPermissionInterceptor = permissionInterceptor;
        return this;
    }

    ExtraServiceImpl setWebView(WebView webView) {
        this.mWebView = webView;
        return this;
    }

    @Override
    protected void destroy() {
        super.destroy();
        this.mIsCloneObject = true;
        this.mActivity = null;
        this.mPermissionInterceptor = null;
        this.mWebView = null;
    }

    @Override
    protected ExtraServiceImpl clone() {
        ExtraServiceImpl mExtraServiceImpl = (ExtraServiceImpl) super.clone();
        mExtraServiceImpl.mIsCloneObject = true;
        mExtraServiceImpl.mActivity = null;
        mExtraServiceImpl.mPermissionInterceptor = null;
        mExtraServiceImpl.mWebView = null;
        LogUtils.e(TAG, " this:" + this + "  clone:" + mExtraServiceImpl);
//		mId = Rumtime.getInstance().generateGlobalId();
        return mExtraServiceImpl;
    }


    DefaultDownloadImpl create() {
        return this.mDefaultDownload = new DefaultDownloadImpl(this);
    }

}