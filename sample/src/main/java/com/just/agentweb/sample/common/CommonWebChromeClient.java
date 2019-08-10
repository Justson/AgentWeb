package com.just.agentweb.sample.common;

import android.util.Log;
import android.webkit.WebView;

import com.just.agentweb.WebChromeClient;

/**
 * @author cenxiaozhong
 * @date 2019/2/19
 * @since 1.0.0
 */
public class CommonWebChromeClient extends WebChromeClient {
	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		  super.onProgressChanged(view, newProgress);
		Log.i("CommonWebChromeClient", "onProgressChanged:" + newProgress + "  view:" + view);
	}
}
