

package com.just.library;

import android.util.Log;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class JsCallback {
    private static final String CALLBACK_JS_FORMAT = "javascript:%s.callback(%d, %d %s);";
    private int mIndex;
    private boolean mCouldGoOn;
    private WeakReference<WebView> mWebViewRef;
    private int mIsPermanent;
    private String mInjectedName;

    public JsCallback (WebView view, String injectedName, int index) {
        mCouldGoOn = true;
        mWebViewRef = new WeakReference<WebView>(view);
        mInjectedName = injectedName;
        mIndex = index;
    }

    /**
     * 向网页执行js回调；
     * @param args
     * @throws JsCallbackException
     */
    public void apply (Object... args) throws JsCallbackException {
        if (mWebViewRef.get() == null) {
            throw new JsCallbackException("the WebView related to the JsCallback has been recycled");
        }
        if (!mCouldGoOn) {
            throw new JsCallbackException("the JsCallback isn't permanent,cannot be called more than once");
        }
        StringBuilder sb = new StringBuilder();
        for (Object arg : args){
            sb.append(",");
            boolean isStrArg = arg instanceof String;
            // 有的接口将Json对象转换成了String返回，这里不能加双引号，否则网页会认为是String而不是JavaScript对象；
            boolean isObjArg = isJavaScriptObject(arg);
            if (isStrArg && !isObjArg) {
                sb.append("\"");
            }
            sb.append(String.valueOf(arg));
            if (isStrArg && !isObjArg) {
                sb.append("\"");
            }
        }
        String execJs = String.format(CALLBACK_JS_FORMAT, mInjectedName, mIndex, mIsPermanent, sb.toString());
        if (LogUtils.isDebug()) {
            Log.d("JsCallBack", execJs);
        }
        mWebViewRef.get().loadUrl(execJs);
        mCouldGoOn = mIsPermanent > 0;
    }

    /**
     * 是否是JSON(JavaScript Object Notation)对象；
     * @param obj
     * @return
     */
    private boolean isJavaScriptObject(Object obj) {
        if (obj instanceof JSONObject || obj instanceof JSONArray) {
            return true;
        } else {
            String json = obj.toString();
            try {
                new JSONObject(json);
            } catch (JSONException e) {
                try {
                    new JSONArray(json);
                } catch (JSONException e1) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 一般传入到Java方法的js function是一次性使用的，即在Java层jsCallback.apply(...)之后不能再发起回调了；
     * 如果需要传入的function能够在当前页面生命周期内多次使用，请在第一次apply前setPermanent(true)；
     * @param value
     */
    public void setPermanent (boolean value) {
        mIsPermanent = value ? 1 : 0;
    }

    public static class JsCallbackException extends Exception {
        public JsCallbackException (String msg) {
            super(msg);
        }
    }
}
