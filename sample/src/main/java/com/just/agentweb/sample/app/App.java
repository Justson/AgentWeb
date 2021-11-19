package com.just.agentweb.sample.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import com.just.agentweb.sample.service.WebService;
import com.just.agentweb.sample.utils.ProcessUtils;
import com.just.agentweb.sample.utils.RomUtils;
import com.queue.library.GlobalQueue;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cenxiaozhong on 2017/5/23.
 * source code  https://github.com/Justson/AgentWeb
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 说明， WebView 初处初始化耗时 250ms 左右。
         * 提前初始化WebView ，好处可以提升页面初始化速度，减少白屏时间，
         * 坏处，拖慢了App 冷启动速度，如果 WebView 配合 VasSonic 使用，
         * 建议不要在此处提前初始化 WebView 。
         */
//        WebView mWebView=new WebView(new MutableContextWrapper(this));

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        //implementation 'com.github.Justson:dispatch-queue:v1.0.5'
        GlobalQueue.getMainQueue().postRunnableInIdleRunning(new Runnable() {
            @Override
            public void run() {
                try {
                    startService(new Intent(App.this, WebService.class));
                } catch (Throwable throwable) {

                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        handleWebviewDir(base);
    }


    /**
     * 来之 https://github.com/Justson/AgentWeb/issues/934 建议
     * fix Using WebView from more than one process
     * @param context
     */
    private static void handleWebviewDir(Context context) {
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
