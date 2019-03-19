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

import android.webkit.WebViewClient;

/**
 * @author cenxiaozhong
 * @date 2017/12/15
 * @since 3.0.0
 */
public class MiddlewareWebClientBase extends WebViewClientDelegate {
    private MiddlewareWebClientBase mMiddleWrareWebClientBase;
    private static String TAG = MiddlewareWebClientBase.class.getSimpleName();

    MiddlewareWebClientBase(MiddlewareWebClientBase client) {
        super(client);
        this.mMiddleWrareWebClientBase = client;
    }

    protected MiddlewareWebClientBase(WebViewClient client) {
        super(client);
    }

    protected MiddlewareWebClientBase() {
        super(null);
    }

    final MiddlewareWebClientBase next() {
        return this.mMiddleWrareWebClientBase;
    }

    @Override
    final void setDelegate(WebViewClient delegate) {
        super.setDelegate(delegate);

    }

    final MiddlewareWebClientBase enq(MiddlewareWebClientBase middleWrareWebClientBase) {
        setDelegate(middleWrareWebClientBase);
        this.mMiddleWrareWebClientBase = middleWrareWebClientBase;
        return middleWrareWebClientBase;
    }

}
