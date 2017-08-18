package com.just.library;

/**
 * Created by cenxiaozhong on 2017/8/17.
 */

public interface PermissionInterceptor {

    boolean intercept(String url, String[] permissions, String action);

}
