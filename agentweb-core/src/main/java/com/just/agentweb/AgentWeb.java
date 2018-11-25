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
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * @author cenxiaozhong
 * @update 4.0.0
 * @since 1.0.0
 */
public final class AgentWeb {
	/**
	 * AgentWeb 's TAG
	 */
	private static final String TAG = AgentWeb.class.getSimpleName();
	/**
	 * Activity
	 */
	private Activity mActivity;
	/**
	 * 承载 WebParentLayout 的 ViewGroup
	 */
	private ViewGroup mViewGroup;
	/**
	 * 负责创建布局 WebView ，WebParentLayout  Indicator等。
	 */
	private WebCreator mWebCreator;
	/**
	 * 管理 WebSettings
	 */
	private IAgentWebSettings mAgentWebSettings;
	/**
	 * AgentWeb
	 */
	private AgentWeb mAgentWeb = null;
	/**
	 * IndicatorController 控制Indicator
	 */
	private IndicatorController mIndicatorController;
	/**
	 * WebChromeClient
	 */
	private WebChromeClient mWebChromeClient;
	/**
	 * WebViewClient
	 */
	private WebViewClient mWebViewClient;
	/**
	 * is show indicator
	 */
	private boolean mEnableIndicator;
	/**
	 * IEventHandler 处理WebView相关返回事件
	 */
	private IEventHandler mIEventHandler;
	/**
	 * WebView 注入对象
	 */
	private ArrayMap<String, Object> mJavaObjects = new ArrayMap<>();
	/**
	 * flag
	 */
	private int TAG_TARGET = 0;
	/**
	 * WebListenerManager
	 */
	private WebListenerManager mWebListenerManager;
	/**
	 * 安全 Controller
	 */
	private WebSecurityController<WebSecurityCheckLogic> mWebSecurityController = null;
	/**
	 * WebSecurityCheckLogic
	 */
	private WebSecurityCheckLogic mWebSecurityCheckLogic = null;
	/**
	 * WebChromeClient
	 */
	private WebChromeClient mTargetChromeClient;
	/**
	 * flag security 's mode
	 */
	private SecurityType mSecurityType = SecurityType.DEFAULT_CHECK;
	/**
	 * Activity
	 */
	private static final int ACTIVITY_TAG = 0;
	/**
	 * Fragment
	 */
	private static final int FRAGMENT_TAG = 1;
	/**
	 * AgentWeb 默认注入对像
	 */
	private AgentWebJsInterfaceCompat mAgentWebJsInterfaceCompat = null;
	/**
	 * JsAccessEntrace 提供快速JS方法调用
	 */
	private JsAccessEntrace mJsAccessEntrace = null;
	/**
	 * URL Loader ， 提供了 WebView#loadUrl(url) reload() stopLoading（） postUrl()等方法
	 */
	private IUrlLoader mIUrlLoader = null;
	/**
	 * WebView 生命周期 ， 跟随生命周期释放CPU
	 */
	private WebLifeCycle mWebLifeCycle;
	/**
	 * Video 视屏播放管理类
	 */
	private IVideo mIVideo = null;
	/**
	 * WebViewClient 辅助控制开关
	 */
	private boolean mWebClientHelper = true;
	/**
	 * PermissionInterceptor 权限拦截
	 */
	private PermissionInterceptor mPermissionInterceptor;
	/**
	 * 是否拦截未知的Url， @link{DefaultWebClient}
	 */
	private boolean mIsInterceptUnkownUrl = false;
	/**
	 *
	 */
	private int mUrlHandleWays = -1;
	/**
	 * MiddlewareWebClientBase WebViewClient 中间件
	 */
	private MiddlewareWebClientBase mMiddleWrareWebClientBaseHeader;
	/**
	 * MiddlewareWebChromeBase WebChromeClient 中间件
	 */
	private MiddlewareWebChromeBase mMiddlewareWebChromeBaseHeader;
	/**
	 * 事件拦截
	 */
	private EventInterceptor mEventInterceptor;

	/**
	 * 注入对象管理类
	 */
	private JsInterfaceHolder mJsInterfaceHolder = null;


	private AgentWeb(AgentBuilder agentBuilder) {
		TAG_TARGET = agentBuilder.mTag;
		this.mActivity = agentBuilder.mActivity;
		this.mViewGroup = agentBuilder.mViewGroup;
		this.mIEventHandler = agentBuilder.mIEventHandler;
		this.mEnableIndicator = agentBuilder.mEnableIndicator;
		mWebCreator = agentBuilder.mWebCreator == null ? configWebCreator(agentBuilder.mBaseIndicatorView, agentBuilder.mIndex, agentBuilder.mLayoutParams, agentBuilder.mIndicatorColor, agentBuilder.mHeight, agentBuilder.mWebView, agentBuilder.mWebLayout) : agentBuilder.mWebCreator;
		mIndicatorController = agentBuilder.mIndicatorController;
		this.mWebChromeClient = agentBuilder.mWebChromeClient;
		this.mWebViewClient = agentBuilder.mWebViewClient;
		mAgentWeb = this;
		this.mAgentWebSettings = agentBuilder.mAgentWebSettings;

		if (agentBuilder.mJavaObject != null && !agentBuilder.mJavaObject.isEmpty()) {
			this.mJavaObjects.putAll((Map<? extends String, ?>) agentBuilder.mJavaObject);
			LogUtils.i(TAG, "mJavaObject size:" + this.mJavaObjects.size());

		}
		this.mPermissionInterceptor = agentBuilder.mPermissionInterceptor == null ? null : new PermissionInterceptorWrapper(agentBuilder.mPermissionInterceptor);
		this.mSecurityType = agentBuilder.mSecurityType;
		this.mIUrlLoader = new UrlLoaderImpl(mWebCreator.create().getWebView(), agentBuilder.mHttpHeaders);
		if (this.mWebCreator.getWebParentLayout() instanceof WebParentLayout) {
			WebParentLayout mWebParentLayout = (WebParentLayout) this.mWebCreator.getWebParentLayout();
			mWebParentLayout.bindController(agentBuilder.mAgentWebUIController == null ? AgentWebUIControllerImplBase.build() : agentBuilder.mAgentWebUIController);
			mWebParentLayout.setErrorLayoutRes(agentBuilder.mErrorLayout, agentBuilder.mReloadId);
			mWebParentLayout.setErrorView(agentBuilder.mErrorView);
		}
		this.mWebLifeCycle = new DefaultWebLifeCycleImpl(mWebCreator.getWebView());
		mWebSecurityController = new WebSecurityControllerImpl(mWebCreator.getWebView(), this.mAgentWeb.mJavaObjects, this.mSecurityType);
		this.mWebClientHelper = agentBuilder.mWebClientHelper;
		this.mIsInterceptUnkownUrl = agentBuilder.mIsInterceptUnkownUrl;
		if (agentBuilder.mOpenOtherPage != null) {
			this.mUrlHandleWays = agentBuilder.mOpenOtherPage.code;
		}
		this.mMiddleWrareWebClientBaseHeader = agentBuilder.mMiddlewareWebClientBaseHeader;
		this.mMiddlewareWebChromeBaseHeader = agentBuilder.mChromeMiddleWareHeader;
		init();
	}


	/**
	 * @return PermissionInterceptor 权限控制者
	 */
	public PermissionInterceptor getPermissionInterceptor() {
		return this.mPermissionInterceptor;
	}


	public WebLifeCycle getWebLifeCycle() {
		return this.mWebLifeCycle;
	}


	public JsAccessEntrace getJsAccessEntrace() {

		JsAccessEntrace mJsAccessEntrace = this.mJsAccessEntrace;
		if (mJsAccessEntrace == null) {
			this.mJsAccessEntrace = mJsAccessEntrace = JsAccessEntraceImpl.getInstance(mWebCreator.getWebView());
		}
		return mJsAccessEntrace;
	}


	public AgentWeb clearWebCache() {

		if (this.getWebCreator().getWebView() != null) {
			AgentWebUtils.clearWebViewAllCache(mActivity, this.getWebCreator().getWebView());
		} else {
			AgentWebUtils.clearWebViewAllCache(mActivity);
		}
		return this;
	}


	public static AgentBuilder with(@NonNull Activity activity) {
		if (activity == null) {
			throw new NullPointerException("activity can not be null .");
		}
		return new AgentBuilder(activity);
	}

	public static AgentBuilder with(@NonNull Fragment fragment) {


		Activity mActivity = null;
		if ((mActivity = fragment.getActivity()) == null) {
			throw new NullPointerException("activity can not be null .");
		}
		return new AgentBuilder(mActivity, fragment);
	}

	public boolean handleKeyEvent(int keyCode, KeyEvent keyEvent) {

		if (mIEventHandler == null) {
			mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor());
		}
		return mIEventHandler.onKeyDown(keyCode, keyEvent);
	}

	public boolean back() {

		if (mIEventHandler == null) {
			mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor());
		}
		return mIEventHandler.back();
	}


	public WebCreator getWebCreator() {
		return this.mWebCreator;
	}

	public IEventHandler getIEventHandler() {
		return this.mIEventHandler == null ? (this.mIEventHandler = EventHandlerImpl.getInstantce(mWebCreator.getWebView(), getInterceptor())) : this.mIEventHandler;
	}


	public IAgentWebSettings getAgentWebSettings() {
		return this.mAgentWebSettings;
	}

	public IndicatorController getIndicatorController() {
		return this.mIndicatorController;
	}

	public JsInterfaceHolder getJsInterfaceHolder() {
		return this.mJsInterfaceHolder;
	}

	public IUrlLoader getUrlLoader() {
		return this.mIUrlLoader;
	}

	public void destroy() {
		this.mWebLifeCycle.onDestroy();
	}

	public static class PreAgentWeb {
		private AgentWeb mAgentWeb;
		private boolean isReady = false;

		PreAgentWeb(AgentWeb agentWeb) {
			this.mAgentWeb = agentWeb;
		}


		public PreAgentWeb ready() {
			if (!isReady) {
				mAgentWeb.ready();
				isReady = true;
			}
			return this;
		}

		public AgentWeb go(@Nullable String url) {
			if (!isReady) {
				ready();
			}
			return mAgentWeb.go(url);
		}


	}


	private void doSafeCheck() {

		WebSecurityCheckLogic mWebSecurityCheckLogic = this.mWebSecurityCheckLogic;
		if (mWebSecurityCheckLogic == null) {
			this.mWebSecurityCheckLogic = mWebSecurityCheckLogic = WebSecurityLogicImpl.getInstance();
		}
		mWebSecurityController.check(mWebSecurityCheckLogic);

	}

	private void doCompat() {
		mJavaObjects.put("agentWeb", mAgentWebJsInterfaceCompat = new AgentWebJsInterfaceCompat(this, mActivity));
	}

	private WebCreator configWebCreator(BaseIndicatorView progressView, int index, ViewGroup.LayoutParams lp, int indicatorColor, int height_dp, WebView webView, IWebLayout webLayout) {

		if (progressView != null && mEnableIndicator) {
			return new DefaultWebCreator(mActivity, mViewGroup, lp, index, progressView, webView, webLayout);
		} else {
			return mEnableIndicator ?
					new DefaultWebCreator(mActivity, mViewGroup, lp, index, indicatorColor, height_dp, webView, webLayout)
					: new DefaultWebCreator(mActivity, mViewGroup, lp, index, webView, webLayout);
		}
	}

	private AgentWeb go(String url) {
		this.getUrlLoader().loadUrl(url);
		IndicatorController mIndicatorController = null;
		if (!TextUtils.isEmpty(url) && (mIndicatorController = getIndicatorController()) != null && mIndicatorController.offerIndicator() != null) {
			getIndicatorController().offerIndicator().show();
		}
		return this;
	}

	private EventInterceptor getInterceptor() {

		if (this.mEventInterceptor != null) {
			return this.mEventInterceptor;
		}

		if (mIVideo instanceof VideoImpl) {
			return this.mEventInterceptor = (EventInterceptor) this.mIVideo;
		}

		return null;

	}

	private void init() {
		doCompat();
		doSafeCheck();
	}

	private IVideo getIVideo() {
		return mIVideo == null ? new VideoImpl(mActivity, mWebCreator.getWebView()) : mIVideo;
	}

	private WebViewClient getWebViewClient() {

		LogUtils.i(TAG, "getDelegate:" + this.mMiddleWrareWebClientBaseHeader);
		DefaultWebClient mDefaultWebClient = DefaultWebClient
				.createBuilder()
				.setActivity(this.mActivity)
				.setClient(this.mWebViewClient)
				.setWebClientHelper(this.mWebClientHelper)
				.setPermissionInterceptor(this.mPermissionInterceptor)
				.setWebView(this.mWebCreator.getWebView())
				.setInterceptUnkownUrl(this.mIsInterceptUnkownUrl)
				.setUrlHandleWays(this.mUrlHandleWays)
				.build();
		MiddlewareWebClientBase header = this.mMiddleWrareWebClientBaseHeader;
		if (header != null) {
			MiddlewareWebClientBase tail = header;
			int count = 1;
			MiddlewareWebClientBase tmp = header;
			while (tmp.next() != null) {
				tail = tmp = tmp.next();
				count++;
			}
			LogUtils.i(TAG, "MiddlewareWebClientBase middleware count:" + count);
			tail.setDelegate(mDefaultWebClient);
			return header;
		} else {
			return mDefaultWebClient;
		}

	}


	private AgentWeb ready() {

		AgentWebConfig.initCookiesManager(mActivity.getApplicationContext());
		IAgentWebSettings mAgentWebSettings = this.mAgentWebSettings;
		if (mAgentWebSettings == null) {
			this.mAgentWebSettings = mAgentWebSettings = AgentWebSettingsImpl.getInstance();
		}

		if (mAgentWebSettings instanceof AbsAgentWebSettings) {
			((AbsAgentWebSettings) mAgentWebSettings).bindAgentWeb(this);
		}
		if (mWebListenerManager == null && mAgentWebSettings instanceof AbsAgentWebSettings) {
			mWebListenerManager = (WebListenerManager) mAgentWebSettings;
		}
		mAgentWebSettings.toSetting(mWebCreator.getWebView());
		if (mJsInterfaceHolder == null) {
			mJsInterfaceHolder = JsInterfaceHolderImpl.getJsInterfaceHolder(mWebCreator.getWebView(), this.mSecurityType);
		}
		LogUtils.i(TAG, "mJavaObjects:" + mJavaObjects.size());
		if (mJavaObjects != null && !mJavaObjects.isEmpty()) {
			mJsInterfaceHolder.addJavaObjects(mJavaObjects);
		}

		if (mWebListenerManager != null) {
			mWebListenerManager.setDownloader(mWebCreator.getWebView(), null);
			mWebListenerManager.setWebChromeClient(mWebCreator.getWebView(), getChromeClient());
			mWebListenerManager.setWebViewClient(mWebCreator.getWebView(), getWebViewClient());
		}

		return this;
	}

	private WebChromeClient getChromeClient() {
		IndicatorController mIndicatorController =
				(this.mIndicatorController == null) ?
						IndicatorHandler.getInstance().inJectIndicator(mWebCreator.offer())
						: this.mIndicatorController;

		DefaultChromeClient mDefaultChromeClient =
				new DefaultChromeClient(this.mActivity,
						this.mIndicatorController = mIndicatorController,
						this.mWebChromeClient, this.mIVideo = getIVideo(),
						this.mPermissionInterceptor, mWebCreator.getWebView());

		LogUtils.i(TAG, "WebChromeClient:" + this.mWebChromeClient);
		MiddlewareWebChromeBase header = this.mMiddlewareWebChromeBaseHeader;
		if (header != null) {
			MiddlewareWebChromeBase tail = header;
			int count = 1;
			MiddlewareWebChromeBase tmp = header;
			for (; tmp.next() != null; ) {
				tail = tmp = tmp.next();
				count++;
			}
			LogUtils.i(TAG, "MiddlewareWebClientBase middleware count:" + count);
			tail.setDelegate(mDefaultChromeClient);
			return this.mTargetChromeClient = header;
		} else {
			return this.mTargetChromeClient = mDefaultChromeClient;
		}
	}


	public enum SecurityType {
		DEFAULT_CHECK, STRICT_CHECK;
	}


	public static final class AgentBuilder {
		private Activity mActivity;
		private Fragment mFragment;
		private ViewGroup mViewGroup;
		private boolean mIsNeedDefaultProgress;
		private int mIndex = -1;
		private BaseIndicatorView mBaseIndicatorView;
		private IndicatorController mIndicatorController = null;
		/*默认进度条是显示的*/
		private boolean mEnableIndicator = true;
		private ViewGroup.LayoutParams mLayoutParams = null;
		private WebViewClient mWebViewClient;
		private WebChromeClient mWebChromeClient;
		private int mIndicatorColor = -1;
		private IAgentWebSettings mAgentWebSettings;
		private WebCreator mWebCreator;
		private HttpHeaders mHttpHeaders = null;
		private IEventHandler mIEventHandler;
		private int mHeight = -1;
		private ArrayMap<String, Object> mJavaObject;
		private SecurityType mSecurityType = SecurityType.DEFAULT_CHECK;
		private WebView mWebView;
		private boolean mWebClientHelper = true;
		private IWebLayout mWebLayout = null;
		private PermissionInterceptor mPermissionInterceptor = null;
		private AbsAgentWebUIController mAgentWebUIController;
		private DefaultWebClient.OpenOtherPageWays mOpenOtherPage = null;
		private boolean mIsInterceptUnkownUrl = false;
		private MiddlewareWebClientBase mMiddlewareWebClientBaseHeader;
		private MiddlewareWebClientBase mMiddlewareWebClientBaseTail;
		private MiddlewareWebChromeBase mChromeMiddleWareHeader = null;
		private MiddlewareWebChromeBase mChromeMiddleWareTail = null;
		private View mErrorView;
		private int mErrorLayout;
		private int mReloadId;
		private int mTag = -1;


		public AgentBuilder(@NonNull Activity activity, @NonNull Fragment fragment) {
			mActivity = activity;
			mFragment = fragment;
			mTag = AgentWeb.FRAGMENT_TAG;
		}

		public AgentBuilder(@NonNull Activity activity) {
			mActivity = activity;
			mTag = AgentWeb.ACTIVITY_TAG;
		}


		public IndicatorBuilder setAgentWebParent(@NonNull ViewGroup v, @NonNull ViewGroup.LayoutParams lp) {
			this.mViewGroup = v;
			this.mLayoutParams = lp;
			return new IndicatorBuilder(this);
		}

		public IndicatorBuilder setAgentWebParent(@NonNull ViewGroup v, int index, @NonNull ViewGroup.LayoutParams lp) {
			this.mViewGroup = v;
			this.mLayoutParams = lp;
			this.mIndex = index;
			return new IndicatorBuilder(this);
		}


		private PreAgentWeb buildAgentWeb() {
			if (mTag == AgentWeb.FRAGMENT_TAG && this.mViewGroup == null) {
				throw new NullPointerException("ViewGroup is null,Please check your parameters .");
			}
			return new PreAgentWeb(HookManager.hookAgentWeb(new AgentWeb(this), this));
		}

		private void addJavaObject(String key, Object o) {
			if (mJavaObject == null) {
				mJavaObject = new ArrayMap<>();
			}
			mJavaObject.put(key, o);
		}

		private void addHeader(String k, String v) {

			if (mHttpHeaders == null) {
				mHttpHeaders = HttpHeaders.create();
			}
			mHttpHeaders.additionalHttpHeader(k, v);

		}
	}

	public static class IndicatorBuilder {
		private AgentBuilder mAgentBuilder = null;

		public IndicatorBuilder(AgentBuilder agentBuilder) {
			this.mAgentBuilder = agentBuilder;
		}

		public CommonBuilder useDefaultIndicator(int color) {
			this.mAgentBuilder.mEnableIndicator = true;
			this.mAgentBuilder.mIndicatorColor = color;
			return new CommonBuilder(mAgentBuilder);
		}

		public CommonBuilder useDefaultIndicator() {
			this.mAgentBuilder.mEnableIndicator = true;
			return new CommonBuilder(mAgentBuilder);
		}

		public CommonBuilder closeIndicator() {
			this.mAgentBuilder.mEnableIndicator = false;
			this.mAgentBuilder.mIndicatorColor = -1;
			this.mAgentBuilder.mHeight = -1;
			return new CommonBuilder(mAgentBuilder);
		}

		public CommonBuilder setCustomIndicator(@NonNull BaseIndicatorView v) {
			if (v != null) {
				this.mAgentBuilder.mEnableIndicator = true;
				this.mAgentBuilder.mBaseIndicatorView = v;
				this.mAgentBuilder.mIsNeedDefaultProgress = false;
			} else {
				this.mAgentBuilder.mEnableIndicator = true;
				this.mAgentBuilder.mIsNeedDefaultProgress = true;
			}

			return new CommonBuilder(mAgentBuilder);
		}

		public CommonBuilder useDefaultIndicator(@ColorInt int color, int height_dp) {
			this.mAgentBuilder.mIndicatorColor = color;
			this.mAgentBuilder.mHeight = height_dp;
			return new CommonBuilder(this.mAgentBuilder);
		}

	}


	public static class CommonBuilder {
		private AgentBuilder mAgentBuilder;

		public CommonBuilder(AgentBuilder agentBuilder) {
			this.mAgentBuilder = agentBuilder;
		}

		public CommonBuilder setEventHanadler(@Nullable IEventHandler iEventHandler) {
			mAgentBuilder.mIEventHandler = iEventHandler;
			return this;
		}

		public CommonBuilder closeWebViewClientHelper() {
			mAgentBuilder.mWebClientHelper = false;
			return this;
		}


		public CommonBuilder setWebChromeClient(@Nullable WebChromeClient webChromeClient) {
			this.mAgentBuilder.mWebChromeClient = webChromeClient;
			return this;

		}

		public CommonBuilder setWebViewClient(@Nullable WebViewClient webChromeClient) {
			this.mAgentBuilder.mWebViewClient = webChromeClient;
			return this;
		}

		public CommonBuilder useMiddlewareWebClient(@NonNull MiddlewareWebClientBase middleWrareWebClientBase) {
			if (middleWrareWebClientBase == null) {
				return this;
			}
			if (this.mAgentBuilder.mMiddlewareWebClientBaseHeader == null) {
				this.mAgentBuilder.mMiddlewareWebClientBaseHeader = this.mAgentBuilder.mMiddlewareWebClientBaseTail = middleWrareWebClientBase;
			} else {
				this.mAgentBuilder.mMiddlewareWebClientBaseTail.enq(middleWrareWebClientBase);
				this.mAgentBuilder.mMiddlewareWebClientBaseTail = middleWrareWebClientBase;
			}
			return this;
		}

		public CommonBuilder useMiddlewareWebChrome(@NonNull MiddlewareWebChromeBase middlewareWebChromeBase) {
			if (middlewareWebChromeBase == null) {
				return this;
			}
			if (this.mAgentBuilder.mChromeMiddleWareHeader == null) {
				this.mAgentBuilder.mChromeMiddleWareHeader = this.mAgentBuilder.mChromeMiddleWareTail = middlewareWebChromeBase;
			} else {
				this.mAgentBuilder.mChromeMiddleWareTail.enq(middlewareWebChromeBase);
				this.mAgentBuilder.mChromeMiddleWareTail = middlewareWebChromeBase;
			}
			return this;
		}

		public CommonBuilder setMainFrameErrorView(@NonNull View view) {
			this.mAgentBuilder.mErrorView = view;
			return this;
		}

		public CommonBuilder setMainFrameErrorView(@LayoutRes int errorLayout, @IdRes int clickViewId) {
			this.mAgentBuilder.mErrorLayout = errorLayout;
			this.mAgentBuilder.mReloadId = clickViewId;
			return this;
		}

		public CommonBuilder setAgentWebWebSettings(@Nullable IAgentWebSettings agentWebSettings) {
			this.mAgentBuilder.mAgentWebSettings = agentWebSettings;
			return this;
		}

		public PreAgentWeb createAgentWeb() {
			return this.mAgentBuilder.buildAgentWeb();
		}


		public CommonBuilder addJavascriptInterface(@NonNull String name, @NonNull Object o) {
			this.mAgentBuilder.addJavaObject(name, o);
			return this;
		}

		public CommonBuilder setSecurityType(@NonNull SecurityType type) {
			this.mAgentBuilder.mSecurityType = type;
			return this;
		}

		public CommonBuilder setWebView(@Nullable WebView webView) {
			this.mAgentBuilder.mWebView = webView;
			return this;
		}

		public CommonBuilder setWebLayout(@Nullable IWebLayout iWebLayout) {
			this.mAgentBuilder.mWebLayout = iWebLayout;
			return this;
		}

		public CommonBuilder additionalHttpHeader(String k, String v) {
			this.mAgentBuilder.addHeader(k, v);

			return this;
		}

		public CommonBuilder setPermissionInterceptor(@Nullable PermissionInterceptor permissionInterceptor) {
			this.mAgentBuilder.mPermissionInterceptor = permissionInterceptor;
			return this;
		}

		public CommonBuilder setAgentWebUIController(@Nullable AgentWebUIControllerImplBase agentWebUIController) {
			this.mAgentBuilder.mAgentWebUIController = agentWebUIController;
			return this;
		}

		public CommonBuilder setOpenOtherPageWays(@Nullable DefaultWebClient.OpenOtherPageWays openOtherPageWays) {
			this.mAgentBuilder.mOpenOtherPage = openOtherPageWays;
			return this;
		}

		public CommonBuilder interceptUnkownUrl() {
			this.mAgentBuilder.mIsInterceptUnkownUrl = true;
			return this;
		}

	}

	private static final class PermissionInterceptorWrapper implements PermissionInterceptor {

		private WeakReference<PermissionInterceptor> mWeakReference;

		private PermissionInterceptorWrapper(PermissionInterceptor permissionInterceptor) {
			this.mWeakReference = new WeakReference<PermissionInterceptor>(permissionInterceptor);
		}

		@Override
		public boolean intercept(String url, String[] permissions, String a) {
			if (this.mWeakReference.get() == null) {
				return false;
			}
			return mWeakReference.get().intercept(url, permissions, a);
		}
	}


}
