
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

package com.just.agentweb.sample.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.coolindicator.sdk.CoolIndicator;
import com.just.agentweb.AgentWebUtils;
import com.just.agentweb.BaseIndicatorView;

/**
 * @author cenxiaozhong
 * @date 2018/2/23
 * @since 1.0.0
 */
public class CoolIndicatorLayout extends BaseIndicatorView {


	private static final String TAG = CoolIndicatorLayout.class.getSimpleName();
	private CoolIndicator mCoolIndicator = null;


	public CoolIndicatorLayout(Context context) {
		this(context, null);
	}

	public CoolIndicatorLayout(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public CoolIndicatorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mCoolIndicator = CoolIndicator.create((Activity) context);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mCoolIndicator.setProgressDrawable(context.getResources().getDrawable(com.coolindicator.sdk.R.drawable.default_drawable_indicator, context.getTheme()));
		} else {
			mCoolIndicator.setProgressDrawable(context.getResources().getDrawable(com.coolindicator.sdk.R.drawable.default_drawable_indicator));
		}

		this.addView(mCoolIndicator, offerLayoutParams());

	}

	@Override
	public void show() {
		this.setVisibility(View.VISIBLE);
		mCoolIndicator.start();
	}

	@Override
	public void setProgress(int newProgress) {
	}

	@Override
	public void hide() {
		mCoolIndicator.complete();
	}

	@Override
	public LayoutParams offerLayoutParams() {
		return new FrameLayout.LayoutParams(-1, AgentWebUtils.dp2px(getContext(), 3));
	}
}
