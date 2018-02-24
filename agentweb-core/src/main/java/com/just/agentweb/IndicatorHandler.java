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

import android.webkit.WebView;


/**
 * @author cenxiaozhong
 * @since 1.0.0
 */
public class IndicatorHandler implements IndicatorController {
	private BaseIndicatorSpec mBaseIndicatorSpec;

	@Override
	public void progress(WebView v, int newProgress) {

		if (newProgress == 0) {
			reset();
		} else if (newProgress > 0 && newProgress <= 10) {
			showIndicator();
		} else if (newProgress > 10 && newProgress < 95) {
			setProgress(newProgress);
		} else {
			setProgress(newProgress);
			finish();
		}

	}

	@Override
	public BaseIndicatorSpec offerIndicator() {
		return this.mBaseIndicatorSpec;
	}

	public void reset() {

		if (mBaseIndicatorSpec != null) {
			mBaseIndicatorSpec.reset();
		}
	}

	@Override
	public void finish() {
		if (mBaseIndicatorSpec != null) {
			mBaseIndicatorSpec.hide();
		}
	}

	@Override
	public void setProgress(int n) {
		if (mBaseIndicatorSpec != null) {
			mBaseIndicatorSpec.setProgress(n);
		}
	}

	@Override
	public void showIndicator() {

		if (mBaseIndicatorSpec != null) {
			mBaseIndicatorSpec.show();
		}
	}

	static IndicatorHandler getInstance() {
		return new IndicatorHandler();
	}


	IndicatorHandler inJectIndicator(BaseIndicatorSpec baseIndicatorSpec) {
		this.mBaseIndicatorSpec = baseIndicatorSpec;
		return this;
	}
}
