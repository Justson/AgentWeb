/*
 * Tencent is pleased to support the open source community by making VasSonic available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 *
 */

package com.just.agentweb.sample.sonic;

import android.os.Bundle;
import android.webkit.WebView;

import com.just.agentweb.AgentWeb;
import com.tencent.sonic.sdk.SonicSessionClient;

import java.util.HashMap;

/**
 *  a implement of SonicSessionClient which need to connect webview and content data.
 */

public class SonicSessionClientImpl extends SonicSessionClient {


    private AgentWeb mAgentWeb;
    public void bindWebView(AgentWeb agentWeb) {
        this.mAgentWeb = agentWeb;
    }

    public WebView getWebView() {
        return this.mAgentWeb.getWebCreator().getWebView();
    }

    @Override
    public void loadUrl(String url, Bundle extraData) {
        this.mAgentWeb.getUrlLoader().loadUrl(url);

    }

    @Override
    public void loadDataWithBaseUrl(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        this.mAgentWeb.getUrlLoader().loadDataWithBaseURL(baseUrl,data,mimeType,encoding,historyUrl);

    }


    @Override
    public void loadDataWithBaseUrlAndHeader(String baseUrl, String data, String mimeType, String encoding, String historyUrl, HashMap<String, String> headers) {
        loadDataWithBaseUrl(baseUrl, data, mimeType, encoding, historyUrl);
    }



}
