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

package com.just.agentweb;

import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;
/**
 * @author cenxiaozhong
 * @since 1.0.0
 */
public class UrlLoaderImpl implements IUrlLoader {


    private Handler mHandler = null;
    private WebView mWebView;
    private HttpHeaders mHttpHeaders;

    UrlLoaderImpl(WebView webView, HttpHeaders httpHeaders) {
        this.mWebView = webView;
        if (this.mWebView == null) {
            new NullPointerException("webview cannot be null .");
        }

        this.mHttpHeaders = httpHeaders;
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void safeLoadUrl(final String url) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                loadUrl(url);
            }
        });
    }

    private void safeReload() {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                reload();
            }
        });
    }

    @Override
    public void loadUrl(String url) {


        if (!AgentWebUtils.isUIThread()) {
            safeLoadUrl(url);
            return;
        }

        if (mHttpHeaders == null || mHttpHeaders.isEmptyHeaders()) {
            this.mWebView.loadUrl(url);
        } else {
            this.mWebView.loadUrl(url, mHttpHeaders.getHeaders());
        }
    }

    @Override
    public void reload() {
        if (!AgentWebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    reload();
                }
            });
            return;
        }
        this.mWebView.reload();


    }

    @Override
    public void loadData(final String data, final String mimeType, final String encoding) {

        if (!AgentWebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadData(data, mimeType, encoding);
                }
            });
            return;
        }
        this.mWebView.loadData(data, mimeType, encoding);

    }

    @Override
    public void stopLoading() {

        if (!AgentWebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    stopLoading();
                }
            });
            return;
        }
        this.mWebView.stopLoading();

    }

    @Override
    public void loadDataWithBaseURL(final String baseUrl, final String data, final String mimeType, final String encoding, final String historyUrl) {

        if (!AgentWebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
                }
            });
            return;
        }
        this.mWebView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);

    }

    @Override
    public void postUrl(final String url, final byte[] postData) {

        if (!AgentWebUtils.isUIThread()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    postUrl(url, postData);
                }
            });
            return;
        }

        this.mWebView.postUrl(url, postData);
    }

    @Override
    public HttpHeaders getHttpHeaders() {
        return this.mHttpHeaders == null ? this.mHttpHeaders = HttpHeaders.create() : this.mHttpHeaders;
    }
}
