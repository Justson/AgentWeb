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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by cenxiaozhong on 2017/7/1.
 */
/**
 * @author cenxiaozhong
 * @date 2017/7/1
 * @update 4.0.0
 * @since 1.0.0
 */
public interface IWebLayout<T extends WebView,V extends ViewGroup> {

    /**
     *
     * @return WebView 的父控件
     */
    @NonNull V getLayout();

    /**
     *
     * @return 返回 WebView  或 WebView 的子View ，返回null AgentWeb 内部会创建适当 WebView
     */
    @Nullable T getWebView();
}
