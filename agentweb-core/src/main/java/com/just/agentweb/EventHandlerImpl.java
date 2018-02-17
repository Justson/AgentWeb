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

import android.view.KeyEvent;
import android.webkit.WebView;

/**
 * IEventHandler 对事件的处理，主要是针对
 * 视屏状态进行了处理 ， 如果当前状态为 视频状态
 * 则先退出视频。
 *
 * @author cenxiaozhong
 * @date 2017/6/3
 * @since 2.0.0
 */
public class EventHandlerImpl implements IEventHandler {
	private WebView mWebView;
	private EventInterceptor mEventInterceptor;

	public static final EventHandlerImpl getInstantce(WebView view, EventInterceptor eventInterceptor) {
		return new EventHandlerImpl(view, eventInterceptor);
	}

	public EventHandlerImpl(WebView webView, EventInterceptor eventInterceptor) {
		this.mWebView = webView;
		this.mEventInterceptor = eventInterceptor;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return back();
		}
		return false;
	}

	@Override
	public boolean back() {
		if (this.mEventInterceptor != null && this.mEventInterceptor.event()) {
			return true;
		}
		if (mWebView != null && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return false;
	}

}
