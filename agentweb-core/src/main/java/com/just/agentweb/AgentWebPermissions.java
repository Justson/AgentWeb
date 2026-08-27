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

import android.Manifest;
import android.os.Build;


/**
 * @author cenxiaozhong
 * @since 1.0.0
 */
public class AgentWebPermissions {
	public static String[] CAMERA;
	public static String[] LOCATION;
	public static String[] MEDIA;
	public static final String ACTION_CAMERA = "Camera";
	public static final String ACTION_LOCATION = "Location";
	public static final String ACTION_MEDIA = "Media";

	static {
		CAMERA = new String[]{
				Manifest.permission.CAMERA};

		LOCATION = new String[]{
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION};

		if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
			// Issue #1077: Android 14 起用户可以只授予「选定的照片」，此时系统只授予
			// READ_MEDIA_VISUAL_USER_SELECTED，READ_MEDIA_IMAGES / VIDEO 仍为 denied。
			// 必须把它一并申请，系统才会给出「选择照片」这一档选项；
			// 校验时须用 AgentWebUtils#hasAnyPermission（任一满足即可），
			// 因为完整授权与部分授权授予的是不同的权限，不存在同时满足的情况。
			MEDIA = new String[]{
					Manifest.permission.READ_MEDIA_IMAGES,
					Manifest.permission.READ_MEDIA_VIDEO,
					Manifest.permission.READ_MEDIA_AUDIO,
					Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
			};
		} else if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
			MEDIA = new String[]{
					Manifest.permission.READ_MEDIA_VIDEO,
					Manifest.permission.READ_MEDIA_AUDIO,
					Manifest.permission.READ_MEDIA_IMAGES,
			};
		} else {
			MEDIA = new String[]{
					Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE
			};
		}
	}

	private static void emptyMediaPermission() {
		MEDIA = new String[]{};
	}

	private static void emptyCameraPermission() {
		CAMERA = new String[]{};
	}

	public static void dontAskUnnecessaryPermissions() {
		if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
			emptyMediaPermission();
			emptyCameraPermission();
		}
	}
}
