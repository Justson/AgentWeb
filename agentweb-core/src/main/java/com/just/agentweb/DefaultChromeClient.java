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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import static com.just.agentweb.ActionActivity.KEY_FROM_INTENTION;


/**
 * @author cenxiaozhong
 * @since 1.0.0
 */
public class DefaultChromeClient extends MiddlewareWebChromeBase {

	/**
	 * Activity 的虚引用
	 */
	private WeakReference<Activity> mActivityWeakReference = null;
	/**
	 * DefaultChromeClient 's TAG
	 */
	private String TAG = DefaultChromeClient.class.getSimpleName();
	/**
	 * Android WebChromeClient path ，用于反射，用户是否重写来该方法
	 */
	public static final String ANDROID_WEBCHROMECLIENT_PATH = "android.webkit.WebChromeClient";
	/**
	 * WebChromeClient
	 */
	private WebChromeClient mWebChromeClient;
	/**
	 * 是否被包装过
	 */
	private boolean mIsWrapper = false;
	/**
	 * Video 处理类
	 */
	private IVideo mIVideo;
	/**
	 * PermissionInterceptor 权限拦截器
	 */
	private PermissionInterceptor mPermissionInterceptor;
	/**
	 * 当前 WebView
	 */
	private WebView mWebView;
	/**
	 * Web端触发的定位 mOrigin
	 */
	private String mOrigin = null;
	/**
	 * Web 端触发的定位 Callback 回调成功，或者失败
	 */
	private GeolocationPermissions.Callback mCallback = null;
	/**
	 * 标志位
	 */
	public static final int FROM_CODE_INTENTION = 0x18;
	/**
	 * 标识当前是获取定位权限
	 */
	public static final int FROM_CODE_INTENTION_LOCATION = FROM_CODE_INTENTION << 2;
	/**
	 * AbsAgentWebUIController
	 */
	private WeakReference<AbsAgentWebUIController> mAgentWebUIController = null;
	/**
	 * IndicatorController 进度条控制器
	 */
	private IndicatorController mIndicatorController;
	/**
	 * 文件选择器
	 */
	private Object mFileChooser;

	DefaultChromeClient(Activity activity,
	                    IndicatorController indicatorController,
	                    WebChromeClient chromeClient,
	                    @Nullable IVideo iVideo,
	                    PermissionInterceptor permissionInterceptor, WebView webView) {
		super(chromeClient);
		this.mIndicatorController = indicatorController;
		mIsWrapper = chromeClient != null ? true : false;
		this.mWebChromeClient = chromeClient;
		mActivityWeakReference = new WeakReference<Activity>(activity);
		this.mIVideo = iVideo;
		this.mPermissionInterceptor = permissionInterceptor;
		this.mWebView = webView;
		mAgentWebUIController = new WeakReference<AbsAgentWebUIController>(AgentWebUtils.getAgentWebUIControllerByWebView(webView));
	}


	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);

		if (mIndicatorController != null) {
			mIndicatorController.progress(view, newProgress);
		}

	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		if (mIsWrapper) {
			super.onReceivedTitle(view, title);
		}
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String message, JsResult result) {


		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "onJsAlert", "public boolean " + ANDROID_WEBCHROMECLIENT_PATH + ".onJsAlert", WebView.class, String.class, String.class, JsResult.class)) {
			return super.onJsAlert(view, url, message, result);
		}

		if (mAgentWebUIController.get() != null) {
			mAgentWebUIController.get().onJsAlert(view, url, message);
		}

		result.confirm();

		return true;
	}


	@Override
	public void onReceivedIcon(WebView view, Bitmap icon) {
		super.onReceivedIcon(view, icon);
	}

	@Override
	public void onGeolocationPermissionsHidePrompt() {
		super.onGeolocationPermissionsHidePrompt();
	}

	//location
	@Override
	public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {

		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "onGeolocationPermissionsShowPrompt", "public void " + ANDROID_WEBCHROMECLIENT_PATH + ".onGeolocationPermissionsShowPrompt", String.class, GeolocationPermissions.Callback.class)) {
			super.onGeolocationPermissionsShowPrompt(origin, callback);
			return;
		}
		onGeolocationPermissionsShowPromptInternal(origin, callback);
	}


	private void onGeolocationPermissionsShowPromptInternal(String origin, GeolocationPermissions.Callback callback) {

		if (mPermissionInterceptor != null) {
			if (mPermissionInterceptor.intercept(this.mWebView.getUrl(), AgentWebPermissions.LOCATION, "location")) {
				callback.invoke(origin, false, false);
				return;
			}
		}

		Activity mActivity = mActivityWeakReference.get();
		if (mActivity == null) {
			callback.invoke(origin, false, false);
			return;
		}

		List<String> deniedPermissions = null;
		if ((deniedPermissions = AgentWebUtils.getDeniedPermissions(mActivity, AgentWebPermissions.LOCATION)).isEmpty()) {
			LogUtils.i(TAG, "onGeolocationPermissionsShowPromptInternal:" + true);
			callback.invoke(origin, true, false);
		} else {

			Action mAction = Action.createPermissionsAction(deniedPermissions.toArray(new String[]{}));
			mAction.setFromIntention(FROM_CODE_INTENTION_LOCATION);
			ActionActivity.setPermissionListener(mPermissionListener);
			this.mCallback = callback;
			this.mOrigin = origin;
			ActionActivity.start(mActivity, mAction);
		}


	}

	private ActionActivity.PermissionListener mPermissionListener = new ActionActivity.PermissionListener() {
		@Override
		public void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras) {


			if (extras.getInt(KEY_FROM_INTENTION) == FROM_CODE_INTENTION_LOCATION) {
				boolean hasPermission = AgentWebUtils.hasPermission(mActivityWeakReference.get(), permissions);

				if (mCallback != null) {
					if (hasPermission) {
						mCallback.invoke(mOrigin, true, false);
					} else {
						mCallback.invoke(mOrigin, false, false);
					}

					mCallback = null;
					mOrigin = null;
				}

				if (!hasPermission && null != mAgentWebUIController.get()) {
					mAgentWebUIController
							.get()
							.onPermissionsDeny(
									AgentWebPermissions.LOCATION,
									AgentWebPermissions.ACTION_LOCATION,
									"Location");
				}
			}

		}
	};

	@Override
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {


		try {
			if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "onJsPrompt", "public boolean " + ANDROID_WEBCHROMECLIENT_PATH + ".onJsPrompt", WebView.class, String.class, String.class, String.class, JsPromptResult.class)) {
				return super.onJsPrompt(view, url, message, defaultValue, result);
			}
			if (this.mAgentWebUIController.get() != null) {
				this.mAgentWebUIController.get().onJsPrompt(mWebView, url, message, defaultValue, result);
			}
		} catch (Exception e) {
			if (LogUtils.isDebug()) {
				e.printStackTrace();
			}
		}

		return true;
	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "onJsConfirm", "public boolean " + ANDROID_WEBCHROMECLIENT_PATH + ".onJsConfirm", WebView.class, String.class, String.class, JsResult.class)) {
			return super.onJsConfirm(view, url, message, result);
		}

		if (mAgentWebUIController.get() != null) {
			mAgentWebUIController.get().onJsConfirm(view, url, message, result);
		}
		return true;
	}


	@Override
	public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {


		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "onExceededDatabaseQuota", ANDROID_WEBCHROMECLIENT_PATH + ".onExceededDatabaseQuota", String.class, String.class, long.class, long.class, long.class, WebStorage.QuotaUpdater.class)) {

			super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
			return;
		}
		quotaUpdater.updateQuota(totalQuota * 2);
	}

	@Override
	public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {


		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "onReachedMaxAppCacheSize", ANDROID_WEBCHROMECLIENT_PATH + ".onReachedMaxAppCacheSize", long.class, long.class, WebStorage.QuotaUpdater.class)) {

			super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
			return;
		}
		quotaUpdater.updateQuota(requiredStorage * 2);
	}


	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
		LogUtils.i(TAG, "openFileChooser>=5.0");
		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "onShowFileChooser", ANDROID_WEBCHROMECLIENT_PATH + ".onShowFileChooser", WebView.class, ValueCallback.class, FileChooserParams.class)) {

			return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
		}

		return openFileChooserAboveL(webView, filePathCallback, fileChooserParams);
	}

	private boolean openFileChooserAboveL(WebView webView, ValueCallback<Uri[]> valueCallbacks, FileChooserParams fileChooserParams) {


		LogUtils.i(TAG, "fileChooserParams:" + fileChooserParams.getAcceptTypes() + "  getTitle:" + fileChooserParams.getTitle() + " accept:" + Arrays.toString(fileChooserParams.getAcceptTypes()) + " length:" + fileChooserParams.getAcceptTypes().length + "  :" + fileChooserParams.isCaptureEnabled() + "  " + fileChooserParams.getFilenameHint() + "  intent:" + fileChooserParams.createIntent().toString() + "   mode:" + fileChooserParams.getMode());

		Activity mActivity = this.mActivityWeakReference.get();
		if (mActivity == null || mActivity.isFinishing()) {
			return false;
		}

		return AgentWebUtils.showFileChooserCompat(mActivity,
				mWebView,
				valueCallbacks,
				fileChooserParams,
				this.mPermissionInterceptor,
				null,
				null,
				null
		);

	}

	/**
	 * Android  >= 4.1
	 *
	 * @param uploadFile ValueCallback ,  File URI callback
	 * @param acceptType
	 * @param capture
	 */
	@Override
	public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
	    /*believe me , i never want to do this */
		LogUtils.i(TAG, "openFileChooser>=4.1");
		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "openFileChooser", ANDROID_WEBCHROMECLIENT_PATH + ".openFileChooser", ValueCallback.class, String.class, String.class)) {
			super.openFileChooser(uploadFile, acceptType, capture);
			return;
		}
		createAndOpenCommonFileChooser(uploadFile, acceptType);
	}

	//  Android < 3.0
	@Override
	public void openFileChooser(ValueCallback<Uri> valueCallback) {
		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "openFileChooser", ANDROID_WEBCHROMECLIENT_PATH + ".openFileChooser", ValueCallback.class)) {
			super.openFileChooser(valueCallback);
			return;
		}
		Log.i(TAG, "openFileChooser<3.0");
		createAndOpenCommonFileChooser(valueCallback, "*/*");
	}

	//  Android  >= 3.0
	@Override
	public void openFileChooser(ValueCallback valueCallback, String acceptType) {
		Log.i(TAG, "openFileChooser>3.0");

		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "openFileChooser", ANDROID_WEBCHROMECLIENT_PATH + ".openFileChooser", ValueCallback.class, String.class)) {
			super.openFileChooser(valueCallback, acceptType);
			return;
		}
		createAndOpenCommonFileChooser(valueCallback, acceptType);
	}


	private void createAndOpenCommonFileChooser(ValueCallback valueCallback, String mimeType) {
		Activity mActivity = this.mActivityWeakReference.get();
		if (mActivity == null || mActivity.isFinishing()) {
			valueCallback.onReceiveValue(new Object());
			return;
		}


		AgentWebUtils.showFileChooserCompat(mActivity,
				mWebView,
				null,
				null,
				this.mPermissionInterceptor,
				valueCallback,
				mimeType,
				null
		);

	}


	@Override
	public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
		super.onConsoleMessage(consoleMessage);
		return true;
	}


	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "onShowCustomView", ANDROID_WEBCHROMECLIENT_PATH + ".onShowCustomView", View.class, CustomViewCallback.class)) {
			super.onShowCustomView(view, callback);
			return;
		}

		if (mIVideo != null) {
			mIVideo.onShowCustomView(view, callback);
		}

	}

	@Override
	public void onHideCustomView() {
		if (AgentWebUtils.isOverriedMethod(mWebChromeClient, "onHideCustomView", ANDROID_WEBCHROMECLIENT_PATH + ".onHideCustomView")) {
			super.onHideCustomView();
			return;
		}

		if (mIVideo != null) {
			mIVideo.onHideCustomView();
		}
	}
}
