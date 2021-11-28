package com.just.agentweb;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.HashSet;
import java.util.Set;

/**
 * @author cenxiaozhong
 * @date 2021/11/24
 * @since 1.0.0
 */
public class AgentWebCompat {


    /**
     * 来之 https://github.com/Justson/AgentWeb/issues/934 建议
     * https://juejin.cn/post/6950091477192015902
     * fix Using WebView from more than one process
     * @param context
     */
    public static void setDataDirectorySuffix(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        try {

            Set<String> pathSet = new HashSet<>();
            String suffix = "";
            String dataPath = context.getDataDir().getAbsolutePath();
            String webViewDir = "/app_webview";
            String huaweiWebViewDir = "/app_hws_webview";
            String lockFile = "/webview_data.lock";
            String processName = ProcessUtils.getCurrentProcessName(context);
            if (!TextUtils.equals(context.getPackageName(), processName)) {//判断不等于默认进程名称
                suffix = TextUtils.isEmpty(processName) ? context.getPackageName() : processName;
                WebView.setDataDirectorySuffix(suffix);
                suffix = "_" + suffix;
                pathSet.add(dataPath + webViewDir + suffix + lockFile);
                if (RomUtils.isHuawei()) {
                    pathSet.add(dataPath + huaweiWebViewDir + suffix + lockFile);
                }
            }else{
                //主进程
                suffix = "_" + processName;
                pathSet.add(dataPath + webViewDir + lockFile);//默认未添加进程名后缀
                pathSet.add(dataPath + webViewDir + suffix + lockFile);//系统自动添加了进程名后缀
                if (RomUtils.isHuawei()) {//部分华为手机更改了webview目录名
                    pathSet.add(dataPath + huaweiWebViewDir + lockFile);
                    pathSet.add(dataPath + huaweiWebViewDir + suffix + lockFile);
                }
            }
            for (String path : pathSet) {
                File file = new File(path);
                if (file.exists()) {
                    tryLockOrRecreateFile(file);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private static void tryLockOrRecreateFile(File file) {
        try {
            FileLock tryLock = new RandomAccessFile(file, "rw").getChannel().tryLock();
            if (tryLock != null) {
                tryLock.close();
            } else {
                createFile(file, file.delete());
            }
        } catch (Exception e) {
            e.printStackTrace();
            boolean deleted = false;
            if (file.exists()) {
                deleted = file.delete();
            }
            createFile(file, deleted);
        }
    }

    private static void createFile(File file, boolean deleted){
        try {
            if (deleted && !file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

