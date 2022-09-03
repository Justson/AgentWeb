
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

import static android.provider.MediaStore.EXTRA_OUTPUT;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.List;


/**
 * @author cenxiaozhong
 * @since 2.0.0
 */
public final class AgentActionFragment extends Fragment {

    public static final String KEY_URI = "KEY_URI";
    public static final String KEY_FROM_INTENTION = "KEY_FROM_INTENTION";
    private static final String TAG = AgentActionFragment.class.getSimpleName();
    private Action mAction;
    public static final int REQUEST_CODE = 0x254;
    public static final String FRAGMENT_TAG = "AgentWebActionFragment";

    public static void start(Activity activity, Action action) {
        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        AgentActionFragment fragment = (AgentActionFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new AgentActionFragment();
            fragmentManager.beginTransaction().add(fragment, FRAGMENT_TAG).commitAllowingStateLoss();
        }
        fragment.mAction = action;
        if (fragment.isViewCreated) {
            fragment.runAction();
        }
    }


    public AgentActionFragment() {
    }

    private void resetAction() {
//        mAction = null;
    }

    private boolean isViewCreated = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            LogUtils.i(TAG, "savedInstanceState:" + savedInstanceState);
            return;
        }
        isViewCreated = true;
        runAction();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void runAction() {
        if (mAction == null) {
            resetAction();
            return;
        }
        if (mAction.getAction() == Action.ACTION_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermission(mAction);
            } else {
                resetAction();
            }
        } else if (mAction.getAction() == Action.ACTION_CAMERA) {
            captureCamera();
        } else if (mAction.getAction() == Action.ACTION_VIDEO) {
            recordVideo();
        } else {
            choose();
        }
    }



    private void choose() {
        try {
            if (mAction.getChooserListener() == null) {
                return;
            }
            Intent mIntent = mAction.getIntent();
            if (mIntent == null) {
                resetAction();
                return;
            }
            this.startActivityForResult(mIntent, REQUEST_CODE);
        } catch (Throwable throwable) {
            LogUtils.i(TAG, "找不到文件选择器");
            chooserActionCallback(-1, null);
            if (LogUtils.isDebug()) {
                throwable.printStackTrace();
            }
        }
    }

    private void chooserActionCallback(int resultCode, Intent data) {
        if (mAction.getChooserListener() != null) {
            mAction.getChooserListener().onChoiceResult(REQUEST_CODE, resultCode, data);
        }
        resetAction();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAction == null) {
            return;
        }
        if (requestCode == REQUEST_CODE) {
            if (mAction.getUri() != null) {
                chooserActionCallback(resultCode, new Intent().putExtra(KEY_URI, mAction.getUri()));
            } else {
                chooserActionCallback(resultCode, data);
            }
        }
        resetAction();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(Action action) {
        List<String> permissions = action.getPermissions();
        if (AgentWebUtils.isEmptyCollection(permissions)) {
            resetAction();
            return;
        }
        if (mAction.getRationaleListener() != null) {
            boolean rationale = false;
            for (String permission : permissions) {
                rationale = shouldShowRequestPermissionRationale(permission);
                if (rationale) {
                    break;
                }
            }
            mAction.getRationaleListener().onRationaleResult(rationale, new Bundle());
            resetAction();
            return;
        }
        if (mAction.getPermissionListener() != null) {
            requestPermissions(permissions.toArray(new String[]{}), 1);
        }
    }


    private void captureCamera() {
        try {
            if (mAction.getChooserListener() == null) {
                resetAction();
                return;
            }
            File mFile = AgentWebUtils.createImageFile(this.getActivity());
            if (mFile == null) {
                mAction.getChooserListener().onChoiceResult(REQUEST_CODE, Activity.RESULT_CANCELED, null);
            }
            Intent intent = AgentWebUtils.getIntentCaptureCompat(getActivity(), mFile);
            // 指定开启系统相机的Action
            mAction.setUri((Uri) intent.getParcelableExtra(EXTRA_OUTPUT));
            this.startActivityForResult(intent, REQUEST_CODE);
        } catch (Throwable ignore) {
            LogUtils.e(TAG, "找不到系统相机");
            if (mAction.getChooserListener() != null) {
                mAction.getChooserListener().onChoiceResult(REQUEST_CODE, Activity.RESULT_CANCELED, null);
            }
            resetAction();
            if (LogUtils.isDebug()) {
                ignore.printStackTrace();
            }
        }
    }

    private void recordVideo() {
        try {
            if (mAction.getChooserListener() == null) {
                resetAction();
                return;
            }
            File mFile = AgentWebUtils.createVideoFile(this.getActivity());
            if (mFile == null) {
                mAction.getChooserListener().onChoiceResult(REQUEST_CODE, Activity.RESULT_CANCELED, null);
                resetAction();
                return;
            }
            Intent intent = AgentWebUtils.getIntentVideoCompat(getActivity(), mFile);
            // 指定开启系统相机的Action
            mAction.setUri((Uri) intent.getParcelableExtra(EXTRA_OUTPUT));
            this.startActivityForResult(intent, REQUEST_CODE);
        } catch (Throwable ignore) {
            LogUtils.e(TAG, "找不到系统相机");
            if (mAction.getChooserListener() != null) {
                mAction.getChooserListener().onChoiceResult(REQUEST_CODE, Activity.RESULT_CANCELED, null);
            }
            resetAction();
            if (LogUtils.isDebug()) {
                ignore.printStackTrace();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mAction.getPermissionListener() != null) {
            Bundle mBundle = new Bundle();
            mBundle.putInt(KEY_FROM_INTENTION, mAction.getFromIntention());
            mAction.getPermissionListener().onRequestPermissionsResult(permissions, grantResults, mBundle);
        }
        resetAction();
    }

    public interface RationaleListener {
        void onRationaleResult(boolean showRationale, Bundle extras);
    }

    public interface PermissionListener {
        void onRequestPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults, Bundle extras);
    }

    public interface ChooserListener {
        void onChoiceResult(int requestCode, int resultCode, Intent data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
