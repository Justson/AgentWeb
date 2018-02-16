package com.just.agentweb;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * Created by cenxiaozhong on 2017/12/8.
 */

public class WebParentLayout extends FrameLayout implements Provider<AgentWebUIController> {
	private AgentWebUIController mAgentWebUIController = null;
	private static final String TAG = WebParentLayout.class.getSimpleName();
	@LayoutRes
	private int mErrorLayoutRes;
	@IdRes
	private int mClickId = -1;
	private View mErrorView;
	private WebView mWebView;
	private FrameLayout mErrorLayout = null;

	WebParentLayout(@NonNull Context context) {
		this(context, null);
		LogUtils.i(TAG, "WebParentLayout");
	}

	WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, -1);
	}

	WebParentLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if (!(context instanceof Activity)) {
			throw new IllegalArgumentException("WebParentLayout context must be activity or activity sub class .");
		}
		this.mErrorLayoutRes = R.layout.agentweb_error_page;
		LogUtils.i(TAG, "mErrorLayoutRes:" + mErrorLayoutRes);
	}

	void bindController(AgentWebUIController agentWebUIController) {
		LogUtils.i(TAG, "bindController:" + agentWebUIController);
		this.mAgentWebUIController = agentWebUIController;
		this.mAgentWebUIController.bindWebParent(this, (Activity) getContext());
	}

	void showPageMainFrameError() {

		View container = this.mErrorLayout;
		if (container != null) {
			container.setVisibility(View.VISIBLE);
		} else {
			createErrorLayout();
			container = this.mErrorLayout;
		}
		View clickView = null;
		if (mClickId != -1 && (clickView = container.findViewById(mClickId)) != null) {
			clickView.setClickable(true);
		} else {
			container.setClickable(true);
		}
	}

	private void createErrorLayout() {

		final FrameLayout mFrameLayout = new FrameLayout(getContext());
		mFrameLayout.setBackgroundColor(Color.WHITE);
		mFrameLayout.setId(R.id.mainframe_error_container_id);
		if (this.mErrorView == null) {
			LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());
			LogUtils.i(TAG, "mErrorLayoutRes:" + mErrorLayoutRes);
			mLayoutInflater.inflate(mErrorLayoutRes, mFrameLayout, true);
		} else {
			mFrameLayout.addView(mErrorView);
		}

		ViewStub mViewStub = (ViewStub) this.findViewById(R.id.mainframe_error_viewsub_id);
		final int index = this.indexOfChild(mViewStub);
		this.removeViewInLayout(mViewStub);
		final ViewGroup.LayoutParams layoutParams = getLayoutParams();
		if (layoutParams != null) {
			this.addView(this.mErrorLayout = mFrameLayout, index, layoutParams);
		} else {
			this.addView(this.mErrorLayout = mFrameLayout, index);
		}

		mFrameLayout.setVisibility(View.VISIBLE);
		if (mClickId != -1) {
			final View clickView = mFrameLayout.findViewById(mClickId);
			if (clickView != null) {
				clickView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (getWebView() != null) {
							clickView.setClickable(false);
							getWebView().reload();
						}
					}
				});
				return;
			} else {

				if (LogUtils.isDebug()) {
					LogUtils.i(TAG, "ClickView is null , cannot bind accurate view to refresh or reload .");
				}
			}

		}

		mFrameLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getWebView() != null) {
					mFrameLayout.setClickable(false);
					getWebView().reload();
				}

			}
		});
	}

	void hideErrorLayout() {
		View mView = null;
		if ((mView = this.findViewById(R.id.mainframe_error_container_id)) != null) {
			mView.setVisibility(View.GONE);
		}
	}

	void setErrorView(@NonNull View errorView) {
		this.mErrorView = errorView;
	}

	void setErrorLayoutRes(@LayoutRes int resLayout, @IdRes int id) {
		this.mClickId = id;
		if (this.mClickId <= 0) {
			this.mClickId = -1;
		}
		this.mErrorLayoutRes = resLayout;
		if (this.mErrorLayoutRes <= 0) {
			this.mErrorLayoutRes = R.layout.agentweb_error_page;
		}
	}

	@Override
	public AgentWebUIController provide() {
		return this.mAgentWebUIController;
	}


	void bindWebView(WebView view) {
		if (this.mWebView == null) {
			this.mWebView = view;
		}
	}

	WebView getWebView() {
		return this.mWebView;
	}


}
