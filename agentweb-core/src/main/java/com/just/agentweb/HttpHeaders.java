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

import android.net.Uri;
import androidx.collection.ArrayMap;
import android.text.TextUtils;

import java.util.Map;


/**
 * @author cenxiaozhong
 * @date 2017/7/5
 * @since 2.0.0
 */
public class HttpHeaders {
    public static HttpHeaders create() {
        return new HttpHeaders();
    }

    private final Map<String, Map<String, String>> mHeaders;

    HttpHeaders() {
        mHeaders = new ArrayMap<String, Map<String, String>>();
    }

    public Map<String, String> getHeaders(String url) {
        String subUrl = subBaseUrl(url);
        if (mHeaders.get(subUrl) == null) {
            Map<String, String> headers = new ArrayMap<>();
            mHeaders.put(subUrl, headers);
            return headers;
        }
        return mHeaders.get(subUrl);
    }

    public void additionalHttpHeader(String url, String k, String v) {
        if (null == url) {
            return;
        }
        url = subBaseUrl(url);
        Map<String, Map<String, String>> mHeaders = getHeaders();
        Map<String, String> headersMap = mHeaders.get(subBaseUrl(url));
        if (null == headersMap) {
            headersMap = new ArrayMap<>();
        }
        headersMap.put(k, v);
        mHeaders.put(url, headersMap);
    }


    public void additionalHttpHeaders(String url, Map<String, String> headers) {
        if (null == url) {
            return;
        }
        String subUrl = subBaseUrl(url);
        Map<String, Map<String, String>> mHeaders = getHeaders();
        Map<String, String> headersMap = headers;
        if (null == headersMap) {
            headersMap = new ArrayMap<>();
        }
        mHeaders.put(subUrl, headersMap);
    }

    public void removeHttpHeader(String url, String k) {
        if (null == url) {
            return;
        }
        String subUrl = subBaseUrl(url);
        Map<String, Map<String, String>> mHeaders = getHeaders();
        Map<String, String> headersMap = mHeaders.get(subUrl);
        if (null != headersMap) {
            headersMap.remove(k);
        }
    }

    public boolean isEmptyHeaders(String url) {
        url = subBaseUrl(url);
        Map<String, String> heads = getHeaders(url);
        return heads == null || heads.isEmpty();
    }

    public Map<String, Map<String, String>> getHeaders() {
        return this.mHeaders;
    }

    private String subBaseUrl(String originUrl) {
        if (TextUtils.isEmpty(originUrl)) {
            return originUrl;
        }
        Uri originUri = Uri.parse(originUrl);
        return originUri.getScheme() + "://" + originUri.getAuthority();
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "mHeaders=" + mHeaders +
                '}';
    }
}
