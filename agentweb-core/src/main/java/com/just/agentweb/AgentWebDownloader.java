package com.just.agentweb;

/**
 * Created by cenxiaozhong on 2018/2/4.
 */

public interface AgentWebDownloader<T> extends DownloadingService {

    void download(T t);


}
