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

import com.just.agentweb.LogUtils;
import com.just.agentweb.PermissionInterceptor;

import java.io.Serializable;

/**
 * @author cenxiaozhong
 * @date 2019/2/8
 * @since 1.0.0
 */

public class ExtraServiceImpl extends IAgentWebDownloader.ExtraService implements Cloneable, Serializable {
	private static final String TAG = "ExtraServiceImpl";
	transient Activity mActivity;
	transient DownloadListener mDownloadListener;
	transient PermissionInterceptor mPermissionInterceptor;
	transient WebView mWebView;
	DefaultDownloadImpl mDefaultDownload;
	String mUrl;
	String mUserAgent;
	String mContentDisposition;
	String mMimetype;
	long mContentLength;
	boolean mIsCloneObject = false;


	@Override
	public String getUrl() {
		return mUrl;
	}

	@Override
	protected ExtraServiceImpl setUrl(String url) {
		this.mUrl = url;
		return this;
	}

	@Override
	public String getUserAgent() {
		return mUserAgent;
	}

	@Override
	protected ExtraServiceImpl setUserAgent(String userAgent) {
		this.mUserAgent = userAgent;
		return this;
	}

	@Override
	public String getContentDisposition() {
		return mContentDisposition;
	}

	@Override
	protected ExtraServiceImpl setContentDisposition(String contentDisposition) {
		this.mContentDisposition = contentDisposition;
		return this;
	}

	@Override
	public String getMimetype() {
		return mMimetype;
	}

	@Override
	protected ExtraServiceImpl setMimetype(String mimetype) {
		this.mMimetype = mimetype;
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
		return this;
	}

	ExtraServiceImpl setDownloadListener(DownloadListener downloadListeners) {
		this.mDownloadListener = downloadListeners;
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
	protected ExtraServiceImpl clone() throws CloneNotSupportedException {
		ExtraServiceImpl mExtraServiceImpl = (ExtraServiceImpl) super.clone();
		mExtraServiceImpl.mIsCloneObject = true;
		mExtraServiceImpl.mActivity = null;
		mExtraServiceImpl.mDownloadListener = null;
		mExtraServiceImpl.mPermissionInterceptor = null;
		mExtraServiceImpl.mWebView = null;
		return mExtraServiceImpl;
	}

	DefaultDownloadImpl create() {
		return this.mDefaultDownload = new DefaultDownloadImpl(this);
	}

	@Override
	public synchronized void performReDownload() {
		LogUtils.i(TAG, "performReDownload:" + mDefaultDownload);
		if (null != this.mDefaultDownload) {
			this.mDefaultDownload
					.onDownloadStartInternal(
							getUrl(),
							getUserAgent(),
							getContentDisposition(),
							getMimetype(),
							getContentLength(), this);
		}
	}
}