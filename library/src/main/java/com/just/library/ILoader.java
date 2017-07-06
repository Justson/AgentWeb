package com.just.library;

/**
 * Created by cenxiaozhong on 2017/6/3.
 */

public interface ILoader {


    void loadUrl(String url);

    void reload();

    void loadData(String data, String mimeType, String encoding);

    void stopLoading();

    void loadDataWithBaseURL(String baseUrl, String data,
                             String mimeType, String encoding, String historyUrl);


    void postUrl(String url ,byte[] params);

    HttpHeaders getHttpHeaders();
}
