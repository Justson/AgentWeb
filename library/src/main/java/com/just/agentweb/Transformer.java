package com.just.agentweb;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by cenxiaozhong on 2017/12/15.
 */

public interface Transformer<T,R> {


    @NonNull R apply(@Nullable T t);
}
