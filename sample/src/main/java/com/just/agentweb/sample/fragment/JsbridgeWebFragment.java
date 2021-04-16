package com.just.agentweb.sample.fragment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebViewClient;

import androidx.annotation.Nullable;

/**
 * Created by cenxiaozhong on 2017/7/1.
 * source code  https://github.com/Justson/AgentWeb
 */

public class JsbridgeWebFragment extends AgentWebFragment {

	public static JsbridgeWebFragment getInstance(Bundle bundle) {

		JsbridgeWebFragment mJsbridgeWebFragment = new JsbridgeWebFragment();
		if (mJsbridgeWebFragment != null) {
			mJsbridgeWebFragment.setArguments(bundle);
		}

		return mJsbridgeWebFragment;
	}

	private BridgeWebView mBridgeWebView;

	@Override
	public String getUrl() {
		return super.getUrl();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

		mBridgeWebView = new BridgeWebView(getActivity());
		mAgentWeb = AgentWeb.with(this)
				.setAgentWebParent((ViewGroup) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
				.useDefaultIndicator(-1, 2)
				.setAgentWebWebSettings(getSettings())
				.setWebChromeClient(mWebChromeClient)
				.setWebViewClient(getWebViewClient())
				.setWebView(mBridgeWebView)
				.setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
//                .setDownloadListener(mDownloadListener) 4.0.0 删除该API
				.createAgentWeb()//
				.ready()//
				.go(getUrl());


		initView(view);


		mBridgeWebView.registerHandler("submitFromWeb", new BridgeHandler() {

			@Override
			public void handler(String data, CallBackFunction function) {
				function.onCallBack("submitFromWeb exe, response data 中文 from Java");
			}

		});

		User user = new User();
		Location location = new Location();
		location.address = "SDU";
		user.location = location;
		user.name = "Agentweb --> Jsbridge";
		mBridgeWebView.callHandler("functionInJs", new Gson().toJson(user), new CallBackFunction() {
			@Override
			public void onCallBack(String data) {
				Log.i(TAG, "data:" + data);
			}
		});

		mBridgeWebView.send("hello");
	}

	private WebViewClient getWebViewClient() {
		return new WebViewClient() {
			BridgeWebViewClient mBridgeWebViewClient = new BridgeWebViewClient(mBridgeWebView);

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (mBridgeWebViewClient.shouldOverrideUrlLoading(view, url)) {
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					if (mBridgeWebViewClient.shouldOverrideUrlLoading(view, request.getUrl().toString())) {
						return true;
					}
				}
				return super.shouldOverrideUrlLoading(view, request);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mBridgeWebViewClient.onPageFinished(view, url);
			}

		};
	}

	static class Location {
		String address;
	}

	static class User {
		String name;
		Location location;
		String testStr;
	}

}
