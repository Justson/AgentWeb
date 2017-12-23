package com.just.agentweb;

import android.content.Intent;

/**
 * Created by cenxiaozhong on 2017/5/22.
 * source code  https://github.com/Justson/AgentWeb
 */

public interface IFileUploadChooser {



    void openFileChooser();

    void fetchFilePathFromIntent(int requestCode, int resultCode, Intent data);
}
