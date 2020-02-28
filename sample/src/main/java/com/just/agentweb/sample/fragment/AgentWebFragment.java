package com.just.agentweb.sample.fragment;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.download.library.DownloadImpl;
import com.download.library.DownloadListenerAdapter;
import com.download.library.Extra;
import com.download.library.ResourceRequest;
import com.google.gson.Gson;
import com.just.agentweb.AbsAgentWebSettings;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.DefaultDownloadImpl;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.MiddlewareWebChromeBase;
import com.just.agentweb.MiddlewareWebClientBase;
import com.just.agentweb.PermissionInterceptor;
import com.just.agentweb.WebChromeClient;
import com.just.agentweb.WebListenerManager;
import com.just.agentweb.sample.R;
import com.just.agentweb.sample.client.MiddlewareChromeClient;
import com.just.agentweb.sample.client.MiddlewareWebViewClient;
import com.just.agentweb.sample.common.CommonWebChromeClient;
import com.just.agentweb.sample.common.FragmentKeyDown;
import com.just.agentweb.sample.common.UIController;

import java.util.HashMap;

/**
 * Created by cenxiaozhong on 2017/5/15.
 * source code  https://github.com/Justson/AgentWeb
 */

public class AgentWebFragment extends Fragment implements FragmentKeyDown {

	private ImageView mBackImageView;
	private View mLineView;
	private ImageView mFinishImageView;
	private TextView mTitleTextView;
	protected AgentWeb mAgentWeb;
	public static final String URL_KEY = "url_key";
	private ImageView mMoreImageView;
	private PopupMenu mPopupMenu;
	/**
	 * 用于方便打印测试
	 */
	private Gson mGson = new Gson();
	public static final String TAG = AgentWebFragment.class.getSimpleName();
	private MiddlewareWebClientBase mMiddleWareWebClient;
	private MiddlewareWebChromeBase mMiddleWareWebChrome;

	public static AgentWebFragment getInstance(Bundle bundle) {

		AgentWebFragment mAgentWebFragment = new AgentWebFragment();
		if (bundle != null) {
			mAgentWebFragment.setArguments(bundle);
		}

		return mAgentWebFragment;

	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_agentweb, container, false);
	}


	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


		mAgentWeb = AgentWeb.with(this)//
				.setAgentWebParent((LinearLayout) view, -1, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//传入AgentWeb的父控件。
				.useDefaultIndicator(-1, 3)//设置进度条颜色与高度，-1为默认值，高度为2，单位为dp。
				.setAgentWebWebSettings(getSettings())//设置 IAgentWebSettings。
				.setWebViewClient(mWebViewClient)//WebViewClient ， 与 WebView 使用一致 ，但是请勿获取WebView调用setWebViewClient(xx)方法了,会覆盖AgentWeb DefaultWebClient,同时相应的中间件也会失效。
				.setWebChromeClient(new CommonWebChromeClient()) //WebChromeClient
				.setPermissionInterceptor(mPermissionInterceptor) //权限拦截 2.0.0 加入。
				.setSecurityType(AgentWeb.SecurityType.STRICT_CHECK) //严格模式 Android 4.2.2 以下会放弃注入对象 ，使用AgentWebView没影响。
				.setAgentWebUIController(new UIController(getActivity())) //自定义UI  AgentWeb3.0.0 加入。
				.setMainFrameErrorView(R.layout.agentweb_error_page, -1) //参数1是错误显示的布局，参数2点击刷新控件ID -1表示点击整个布局都刷新， AgentWeb 3.0.0 加入。
				.useMiddlewareWebChrome(getMiddlewareWebChrome()) //设置WebChromeClient中间件，支持多个WebChromeClient，AgentWeb 3.0.0 加入。
				.additionalHttpHeader(getUrl(), "cookie", "41bc7ddf04a26b91803f6b11817a5a1c")
				.useMiddlewareWebClient(getMiddlewareWebClient()) //设置WebViewClient中间件，支持多个WebViewClient， AgentWeb 3.0.0 加入。
				.setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他页面时，弹窗质询用户前往其他应用 AgentWeb 3.0.0 加入。
				.interceptUnkownUrl() //拦截找不到相关页面的Url AgentWeb 3.0.0 加入。
				.createAgentWeb()//创建AgentWeb。
				.ready()//设置 WebSettings。
				.go(getUrl()); //WebView载入该url地址的页面并显示。


		AgentWebConfig.debug();

		initView(view);


		// AgentWeb 没有把WebView的功能全面覆盖 ，所以某些设置 AgentWeb 没有提供 ， 请从WebView方面入手设置。
		mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
		//mAgentWeb.getWebCreator().getWebView()  获取WebView .

//		mAgentWeb.getWebCreator().getWebView().setOnLongClickListener();

	}


	protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {

		/**
		 * PermissionInterceptor 能达到 url1 允许授权， url2 拒绝授权的效果。
		 * @param url
		 * @param permissions
		 * @param action
		 * @return true 该Url对应页面请求权限进行拦截 ，false 表示不拦截。
		 */
		@Override
		public boolean intercept(String url, String[] permissions, String action) {
			Log.i(TAG, "mUrl:" + url + "  permission:" + mGson.toJson(permissions) + " action:" + action);
			return false;
		}
	};


	/**
	 * @return IAgentWebSettings
	 */
	public IAgentWebSettings getSettings() {
		return new AbsAgentWebSettings() {
			private AgentWeb mAgentWeb;

			@Override
			protected void bindAgentWebSupport(AgentWeb agentWeb) {
				this.mAgentWeb = agentWeb;
			}

			/**
			 * AgentWeb 4.0.0 内部删除了 DownloadListener 监听 ，以及相关API ，将 Download 部分完全抽离出来独立一个库，
			 * 如果你需要使用 AgentWeb Download 部分 ， 请依赖上 compile 'com.download.library:Downloader:4.1.1' ，
			 * 如果你需要监听下载结果，请自定义 AgentWebSetting ， New 出 DefaultDownloadImpl
			 * 实现进度或者结果监听，例如下面这个例子，如果你不需要监听进度，或者下载结果，下面 setDownloader 的例子可以忽略。
			 * @param webView
			 * @param downloadListener
			 * @return WebListenerManager
			 */
			@Override
			public WebListenerManager setDownloader(WebView webView, android.webkit.DownloadListener downloadListener) {
				return super.setDownloader(webView,
						new DefaultDownloadImpl(getActivity(),
								webView,
								this.mAgentWeb.getPermissionInterceptor()) {

							@Override
							protected ResourceRequest createResourceRequest(String url) {
								return DownloadImpl.getInstance()
										.with(getActivity().getApplicationContext())
										.url(url)
										.quickProgress()
										.addHeader("", "")
										.setEnableIndicator(true)
										.autoOpenIgnoreMD5()
										.setRetry(5)
										.setBlockMaxTime(100000L);
							}

							@Override
							protected void taskEnqueue(ResourceRequest resourceRequest) {
								resourceRequest.enqueue(new DownloadListenerAdapter() {
									@Override
									public void onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, Extra extra) {
										super.onStart(url, userAgent, contentDisposition, mimetype, contentLength, extra);
									}

									@MainThread
									@Override
									public void onProgress(String url, long downloaded, long length, long usedTime) {
										super.onProgress(url, downloaded, length, usedTime);
									}

									@Override
									public boolean onResult(Throwable throwable, Uri path, String url, Extra extra) {
										return super.onResult(throwable, path, url, extra);
									}
								});
							}
						});
			}
		};
	}

	/**
	 * 页面空白，请检查scheme是否加上， scheme://host:port/path?query&query 。
	 *
	 * @return mUrl
	 */
	public String getUrl() {
		String target = "";

		if (TextUtils.isEmpty(target = this.getArguments().getString(URL_KEY))) {
			target = "http://www.jd.com/";
		}

//		return "http://ggzy.sqzwfw.gov.cn/WebBuilderDS/WebbuilderMIS/attach/downloadZtbAttach.jspx?attachGuid=af982055-3d76-4b00-b5ab-36dee1f90b11&appUrlFlag=sqztb&siteGuid=7eb5f7f1-9041-43ad-8e13-8fcb82ea831a";
		return target;
	}

	protected com.just.agentweb.WebChromeClient mWebChromeClient = new WebChromeClient() {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			//  super.onProgressChanged(view, newProgress);
			Log.i(TAG, "onProgressChanged:" + newProgress + "  view:" + view);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			if (mTitleTextView != null && !TextUtils.isEmpty(title)) {
				if (title.length() > 10) {
					title = title.substring(0, 10).concat("...");
				}
			}
			mTitleTextView.setText(title);
		}
	};
	/**
	 * 注意，重写WebViewClient的方法,super.xxx()请务必正确调用， 如果没有调用super.xxx(),则无法执行DefaultWebClient的方法
	 * 可能会影响到AgentWeb自带提供的功能,尽可能调用super.xxx()来完成洋葱模型
	 */
	protected com.just.agentweb.WebViewClient mWebViewClient = new com.just.agentweb.WebViewClient() {

		private HashMap<String, Long> timer = new HashMap<>();

		@Override
		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
			super.onReceivedError(view, request, error);
		}

		@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
			return super.shouldOverrideUrlLoading(view, request);
		}

		@Nullable
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
			return super.shouldInterceptRequest(view, request);
		}

		//
		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, String url) {

			Log.i(TAG, "view:" + new Gson().toJson(view.getHitTestResult()));
			Log.i(TAG, "mWebViewClient shouldOverrideUrlLoading:" + url);
			//优酷想唤起自己应用播放该视频 ， 下面拦截地址返回 true  则会在应用内 H5 播放 ，禁止优酷唤起播放该视频， 如果返回 false ， DefaultWebClient  会根据intent 协议处理 该地址 ， 首先匹配该应用存不存在 ，如果存在 ， 唤起该应用播放 ， 如果不存在 ， 则跳到应用市场下载该应用 .
			if (url.startsWith("intent://") && url.contains("com.youku.phone")) {
				return true;
			}
			/*else if (isAlipay(view, mUrl))   //1.2.5开始不用调用该方法了 ，只要引入支付宝sdk即可 ， DefaultWebClient 默认会处理相应url调起支付宝
			    return true;*/
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.i(TAG, "mUrl:" + url + " onPageStarted  target:" + getUrl());
			timer.put(url, System.currentTimeMillis());
			if (url.equals(getUrl())) {
				pageNavigator(View.GONE);
			} else {
				pageNavigator(View.VISIBLE);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);

			if (timer.get(url) != null) {
				long overTime = System.currentTimeMillis();
				Long startTime = timer.get(url);
				Log.i(TAG, "  page mUrl:" + url + "  used time:" + (overTime - startTime));
			}

		}
		/*错误页回调该方法 ， 如果重写了该方法， 上面传入了布局将不会显示 ， 交由开发者实现，注意参数对齐。*/
	   /* public void onMainFrameError(AbsAgentWebUIController agentWebUIController, WebView view, int errorCode, String description, String failingUrl) {

            Log.i(TAG, "AgentWebFragment onMainFrameError");
            agentWebUIController.onMainFrameError(view,errorCode,description,failingUrl);

        }*/

		@Override
		public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
			super.onReceivedHttpError(view, request, errorResponse);

//			Log.i(TAG, "onReceivedHttpError:" + 3 + "  request:" + mGson.toJson(request) + "  errorResponse:" + mGson.toJson(errorResponse));
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
			super.onReceivedSslError(view, handler, error);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);

//			Log.i(TAG, "onReceivedError:" + errorCode + "  description:" + description + "  errorResponse:" + failingUrl);
		}
	};


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/**
		 * 2.0.0开始 废弃该api ，没有api代替 ,使用 ActionActivity 绕过该方法 ,降低使用门槛,4.0.0 删除该API。
		 */
//        mAgentWeb.uploadFileResult(requestCode, resultCode, data);
	}

	protected void initView(View view) {
		mBackImageView = (ImageView) view.findViewById(R.id.iv_back);
		mLineView = view.findViewById(R.id.view_line);
		mFinishImageView = (ImageView) view.findViewById(R.id.iv_finish);
		mTitleTextView = (TextView) view.findViewById(R.id.toolbar_title);
		mBackImageView.setOnClickListener(mOnClickListener);
		mFinishImageView.setOnClickListener(mOnClickListener);
		mMoreImageView = (ImageView) view.findViewById(R.id.iv_more);
		mMoreImageView.setOnClickListener(mOnClickListener);
		pageNavigator(View.GONE);
	}


	private void pageNavigator(int tag) {

		mBackImageView.setVisibility(tag);
		mLineView.setVisibility(tag);
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {


			switch (v.getId()) {
				case R.id.iv_back:
					// true表示AgentWeb处理了该事件
					if (!mAgentWeb.back()) {
						AgentWebFragment.this.getActivity().finish();
					}
					break;
				case R.id.iv_finish:
					AgentWebFragment.this.getActivity().finish();
					break;
				case R.id.iv_more:
					showPoPup(v);
					break;
				default:
					break;

			}
		}

	};

	/**
	 * 打开浏览器
	 *
	 * @param targetUrl 外部浏览器打开的地址
	 */
	private void openBrowser(String targetUrl) {
		if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
			Toast.makeText(this.getContext(), targetUrl + " 该链接无法使用浏览器打开。", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri mUri = Uri.parse(targetUrl);
		intent.setData(mUri);
		startActivity(intent);
	}


	/**
	 * 显示更多菜单
	 *
	 * @param view 菜单依附在该View下面
	 */
	private void showPoPup(View view) {
		if (mPopupMenu == null) {
			mPopupMenu = new PopupMenu(this.getActivity(), view);
			mPopupMenu.inflate(R.menu.toolbar_menu);
			mPopupMenu.setOnMenuItemClickListener(mOnMenuItemClickListener);
		}
		mPopupMenu.show();
	}

	/**
	 * 菜单事件
	 */
	private PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {

			switch (item.getItemId()) {

				case R.id.refresh:
					if (mAgentWeb != null) {
						mAgentWeb.getUrlLoader().reload(); // 刷新
					}
					return true;

				case R.id.copy:
					if (mAgentWeb != null) {
						toCopy(AgentWebFragment.this.getContext(), mAgentWeb.getWebCreator().getWebView().getUrl());
					}
					return true;
				case R.id.default_browser:
					if (mAgentWeb != null) {
						openBrowser(mAgentWeb.getWebCreator().getWebView().getUrl());
					}
					return true;
				case R.id.default_clean:
					toCleanWebCache();
					return true;
				case R.id.error_website:
					loadErrorWebSite();
					// test DownloadingService
//			        LogUtils.i(TAG, " :" + mDownloadingService + "  " + (mDownloadingService == null ? "" : mDownloadingService.isShutdown()) + "  :" + mExtraService);
//                    if (mDownloadingService != null && !mDownloadingService.isShutdown()) {
//                        mExtraService = mDownloadingService.shutdownNow();
//                        LogUtils.i(TAG, "mExtraService::" + mExtraService);
//                        return true;
//                    }
//                    if (mExtraService != null) {
//                        mExtraService.performReDownload();
//                    }

					return true;
				default:
					return false;
			}

		}
	};

	/**
	 * 测试错误页的显示
	 */
	private void loadErrorWebSite() {
		if (mAgentWeb != null) {
			mAgentWeb.getUrlLoader().loadUrl("http://www.unkownwebsiteblog.me");
		}
	}

	/**
	 * 清除 WebView 缓存
	 */
	private void toCleanWebCache() {

		if (this.mAgentWeb != null) {

			//清理所有跟WebView相关的缓存 ，数据库， 历史记录 等。
			this.mAgentWeb.clearWebCache();
			Toast.makeText(getActivity(), "已清理缓存", Toast.LENGTH_SHORT).show();
			//清空所有 AgentWeb 硬盘缓存，包括 WebView 的缓存 , AgentWeb 下载的图片 ，视频 ，apk 等文件。
//            AgentWebConfig.clearDiskCache(this.getContext());
		}

	}


	/**
	 * 复制字符串
	 *
	 * @param context
	 * @param text
	 */
	private void toCopy(Context context, String text) {

		ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));

	}


	@Override
	public void onResume() {
		mAgentWeb.getWebLifeCycle().onResume();//恢复
		super.onResume();
	}

	@Override
	public void onPause() {

		mAgentWeb.getWebLifeCycle().onPause(); //暂停应用内所有WebView ， 调用mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); 恢复。
		super.onPause();
	}

	@Override
	public boolean onFragmentKeyDown(int keyCode, KeyEvent event) {
		return mAgentWeb.handleKeyEvent(keyCode, event);
	}

	@Override
	public void onDestroyView() {
		mAgentWeb.getWebLifeCycle().onDestroy();
		super.onDestroyView();
	}

	/**
	 * MiddlewareWebClientBase 是 AgentWeb 3.0.0 提供一个强大的功能，
	 * 如果用户需要使用 AgentWeb 提供的功能， 不想重写 WebClientView方
	 * 法覆盖AgentWeb提供的功能，那么 MiddlewareWebClientBase 是一个
	 * 不错的选择 。
	 *
	 * @return
	 */
	protected MiddlewareWebClientBase getMiddlewareWebClient() {
		return this.mMiddleWareWebClient = new MiddlewareWebViewClient() {
			/**
			 *
			 * @param view
			 * @param url
			 * @return
			 */
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.e(TAG, "MiddlewareWebClientBase#shouldOverrideUrlLoading url:" + url);
				/*if (url.startsWith("agentweb")) { // 拦截 url，不执行 DefaultWebClient#shouldOverrideUrlLoading
					Log.i(TAG, "agentweb scheme ~");
					return true;
				}*/

				if (super.shouldOverrideUrlLoading(view, url)) { // 执行 DefaultWebClient#shouldOverrideUrlLoading
					return true;
				}
				// do you work
				return false;
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				Log.e(TAG, "MiddlewareWebClientBase#shouldOverrideUrlLoading request url:" + request.getUrl().toString());
				return super.shouldOverrideUrlLoading(view, request);
			}
		};
	}

	protected MiddlewareWebChromeBase getMiddlewareWebChrome() {
		return this.mMiddleWareWebChrome = new MiddlewareChromeClient() {
		};
	}
}
