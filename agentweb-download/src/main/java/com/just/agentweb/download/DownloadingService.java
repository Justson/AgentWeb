package com.just.agentweb.download;

/**
 * Created by cenxiaozhong on 2018/2/4.
 */

public interface DownloadingService {

    boolean isShutdown();

    AgentWebDownloader.ExtraService shutdownNow();

}
