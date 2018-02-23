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


/**
 * @author cenxiaozhong
 * @since 1.0.0
 */
public class AgentWebPermissions {


	public static final String[] CAMERA;
	public static final String[] LOCATION;
	public static final String[] STORAGE;

	public static final String ACTION_CAMERA = "Camera";
	public static final String ACTION_LOCATION = "Location";
	public static final String ACTION_STORAGE = "Storage";

	static {


		CAMERA = new String[]{
				Manifest.permission.CAMERA};


		LOCATION = new String[]{
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION};


		STORAGE = new String[]{
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE};
	}


}
