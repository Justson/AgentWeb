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

import android.app.Activity;
import android.os.Handler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;


/**
 * @author cenxiaozhong
 * @date 2017/12/6
 * @since 3.0.0
 */
public class AgentWebUIControllerImplBase extends AbsAgentWebUIController {


	public static AbsAgentWebUIController build() {
		return new AgentWebUIControllerImplBase();
	}

	@Override
	public void onJsAlert(WebView view, String url, String message) {
		getDelegate().onJsAlert(view, url, message);
	}

	@Override
	public void onOpenPagePrompt(WebView view, String url, Handler.Callback callback) {
		getDelegate().onOpenPagePrompt(view, url, callback);
	}

	@Override
	public void onJsConfirm(WebView view, String url, String message, JsResult jsResult) {
		getDelegate().onJsConfirm(view, url, message, jsResult);
	}

	@Override
	public void onSelectItemsPrompt(WebView view, String url, String[] ways, Handler.Callback callback) {
		getDelegate().onSelectItemsPrompt(view, url, ways, callback);
	}

	@Override
	public void onForceDownloadAlert(String url, Handler.Callback callback) {
		getDelegate().onForceDownloadAlert(url, callback);
	}

	@Override
	public void onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {
		getDelegate().onJsPrompt(view, url, message, defaultValue, jsPromptResult);
	}

	@Override
	public void onMainFrameError(WebView view, int errorCode, String description, String failingUrl) {
		getDelegate().onMainFrameError(view, errorCode, description, failingUrl);
	}

	@Override
	public void onShowMainFrame() {
		getDelegate().onShowMainFrame();
	}

	@Override
	public void onLoading(String msg) {
		getDelegate().onLoading(msg);
	}

	@Override
	public void onCancelLoading() {
		getDelegate().onCancelLoading();
	}


	@Override
	public void onShowMessage(String message, String from) {
		getDelegate().onShowMessage(message, from);
	}

	@Override
	public void onPermissionsDeny(String[] permissions, String permissionType, String action) {
		getDelegate().onPermissionsDeny(permissions, permissionType, action);
	}

	@Override
	protected void bindSupportWebParent(WebParentLayout webParentLayout, Activity activity) {
		getDelegate().bindSupportWebParent(webParentLayout, activity);
	}


}
