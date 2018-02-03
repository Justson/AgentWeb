package com.just.agentweb;

/**
 * Created by cenxiaozhong on 2018/2/4.
 */

public interface DownloadingService {
    boolean isShutdown();

    void shutdownNow();
}
