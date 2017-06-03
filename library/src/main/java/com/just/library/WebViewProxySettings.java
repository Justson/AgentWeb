package com.just.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Proxy;
import android.net.ProxyInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 */
public class WebViewProxySettings {
    private static final boolean DEBUG = false;
    private static final String TAG = "WebViewProxySettings";

    /**
     * 重置代理
     *
     * @param webView
     * @return
     */
    public static boolean resetProxy(WebView webView) {
        if (DEBUG) {
            printProxy(webView.getContext());
        }
        boolean result = setProxy(webView, "", -1);
        if (DEBUG) {
            Log.d(TAG, "resetProxy.result = " + result);
            printProxy(webView.getContext());
        }
        return result;
    }

    /**
     * 设置代理
     *
     * @param webView
     * @param host
     * @param port
     * @return
     */
    public static boolean setProxy(WebView webView, String host, int port) {
        boolean result;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR2) { // 3.2 (HC) and below
            result = setProxyHCAndBelow(webView, host, port);
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) { // 4.0-4.3 (JB)
            result = setProxyJB(webView, host, port);
        } else { // 4.4 (KK) and above
            result = setProxyKKAndAbove(webView.getContext().getApplicationContext(), host, port);
        }
        return result;
    }

    /**
     * 设置代理（Android 3.2及以下）
     *
     * @param webView
     * @param host
     * @param port
     * @return
     */
    private static boolean setProxyHCAndBelow(WebView webView, String host, int port) {
        Object sNetwork = invokeStaticMethod("android.webkit.Network", "getInstance", new Class[]{Context.class}, webView.getContext());
        Object mRequestQueue = getFieldValue(sNetwork, "mRequestQueue"); // android.net.http.RequestQueue
        Object httpHost = newInstance("org.apache.http.HttpHost", new Class[]{String.class, int.class}, host, port);
        setFieldValue(mRequestQueue, "mProxyHost", httpHost); // org.apache.http.HttpHost
        return true;
    }

    /**
     * 设置代理（Android 4.0 - 4.3）
     *
     * @param webView
     * @param host
     * @param port
     * @return
     */
    private static boolean setProxyJB(WebView webView, String host, int port) {
        boolean isSet = !TextUtils.isEmpty(host) && port >= 0;
        Object mWebViewCore = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) { // Android 4.0 - 4.0.3
            mWebViewCore = getFieldValue(webView, "mWebViewCore"); // android.webkit.WebViewClassic
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) { // Android 4.1 - 4.3
            Object webViewClassic = invokeStaticMethod("android.webkit.WebViewClassic", "fromWebView", new Class[]{WebView.class}, webView);
            mWebViewCore = getFieldValue(webViewClassic, "mWebViewCore"); // android.webkit.WebViewClassic
        }
        Object mBrowserFrame = getFieldValue(mWebViewCore, "mBrowserFrame"); // android.webkit.BrowserFrame
        Object sJavaBridge = getStaticFieldValue(mBrowserFrame.getClass().getName(), "sJavaBridge"); // android.webkit.JWebCoreJavaBridge
        Class classProxyProperties = getClass("android.net.ProxyProperties");
        Object proxyProperties = null;
        if (isSet) {
            proxyProperties = newInstance(classProxyProperties.getName(), new Class[]{String.class, Integer.TYPE, String.class}, host, port, null);
        }
        invokeMethod(sJavaBridge, "updateProxy", new Class[]{classProxyProperties}, proxyProperties);
        return true;
    }

    /**
     * 设置代理（Android 4.4及以上）
     *
     * @param applicationContext
     * @param host
     * @param port
     * @return
     */
    public static boolean setProxyKKAndAbove(Context applicationContext, String host, int port) {
        boolean result = false;
        boolean isSet = !TextUtils.isEmpty(host) && port >= 0;
        Object mLoadedApk = getFieldValue(applicationContext, "mLoadedApk"); // android.app.LoadedApk
        Map<Context, ?> mReceivers = (Map<Context, ?>) getFieldValue(mLoadedApk, "mReceivers"); // ArrayMap<Context, ArrayMap<BroadcastReceiver, ReceiverDispatcher>>
        for (Object receiverMap : mReceivers.values()) {
            for (Object receiver : ((Map<BroadcastReceiver, ?>) receiverMap).keySet()) {
                Class clazz = receiver.getClass();
                // https://src.chromium.org/svn/trunk/src/net/android/java/src/org/chromium/net/ProxyChangeListener.java#ProxyReceiver
                if ("org.chromium.net.ProxyChangeListener$ProxyReceiver".equals(clazz.getName())) {
                    Intent intent = new Intent(Proxy.PROXY_CHANGE_ACTION);
                    if (isSet) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            Object proxyProperties = newInstance("android.net.ProxyProperties", new Class[]{String.class, Integer.TYPE, String.class}, host, port, null);
                            intent.putExtra("proxy", (Parcelable) proxyProperties);
                        } else {
                            intent.putExtra(Proxy.EXTRA_PROXY_INFO, ProxyInfo.buildDirectProxy(host, port));
                        }
                    }
                    invokeMethod(receiver, "onReceive", new Class[]{Context.class, Intent.class}, applicationContext, intent);
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * 打印系统代理设置
     *
     * @param context
     */
    private static void printProxy(Context context) {
        StringBuilder sb = new StringBuilder();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ProxyInfo proxyInfo = (ProxyInfo) invokeMethod(cm, "getGlobalProxy", null);
        sb.append("\nconnectivityManager.getGlobalProxy.proxyInfo = " + proxyInfo);
        sb.append("\nProxy.getDefaultHost():Proxy.getDefaultPort() = " + Proxy.getDefaultHost() + ":" + Proxy.getDefaultPort());
        sb.append("\nSystem.getProperty(\"http.proxyHost\"):System.getProperty(\"http.proxyPort\") = " + System.getProperty("http.proxyHost") + ":" + System.getProperty("http.proxyPort"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            String host = (String) invokeStaticMethod("android.net.ProxyProperties", "getHost", null);
            int port = (int) invokeStaticMethod("android.net.ProxyProperties", "getPort", null);
            sb.append("\nProxyProperties.getHost():ProxyProperties.getPort() = " + host + ":" + port);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(Uri.parse("content://telephony/carriers"), null, " apn = ? and current = 1", null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    cursor.moveToFirst();
                    String apn = cursor.getString(cursor.getColumnIndex("apn"));
                    String proxy = cursor.getString(cursor.getColumnIndex("proxy"));
                    int port = cursor.getInt(cursor.getColumnIndex("port"));
                    sb.append("\nAPN:proxy:port = " + apn + ":" + proxy + ":" + port);
                }

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (DEBUG) {
            Log.d(TAG, "printProxy.proxy: " + sb);
        }
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param object    子类对象
     * @param fieldName 父类中的属性名
     * @return 父类中的属性对象
     */
    private static Field getDeclaredField(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        while (clazz != Object.class) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new RuntimeException("getDeclaredField exception, object = " + object.getClass().getName() + ", fieldName = " + fieldName);
    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     *
     * @param object    子类对象
     * @param fieldName 父类中的属性名
     * @param value     将要设置的值
     */
    private static void setFieldValue(Object object, String fieldName, Object value) {
        try {
            Field field = getDeclaredField(object, fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("setFieldValue exception, object = " + object.getClass().getName() + ", fieldName = " + fieldName, e);
        }
    }

    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     *
     * @param object    子类对象
     * @param fieldName 父类中的属性名
     * @return 父类中的属性值
     */
    private static Object getFieldValue(Object object, String fieldName) {
        try {
            Field field = getDeclaredField(object, fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException("getFieldValue exception, object = " + object.getClass().getName() + ", fieldName = " + fieldName, e);
        }
    }

    /**
     * 直接读取static类的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     *
     * @param className 类名称
     * @param fieldName 父类中的属性名
     * @return 父类中的属性值
     */
    private static Object getStaticFieldValue(String className, String fieldName) {
        try {
            Field field = getDeclaredField(getClass(className), fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("getFieldValue exception, className = " + className + ", fieldName = " + fieldName, e);
        }
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredMethod
     *
     * @param object         子类对象
     * @param methodName     父类中的方法名
     * @param parameterTypes 父类中的方法参数类型
     * @return 父类中的方法对象
     */
    private static Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Class<?> clazz = object instanceof Class ? (Class) object : object.getClass();
        while (clazz != Object.class) {
            try {
                return clazz.getDeclaredMethod(methodName, parameterTypes);
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new RuntimeException("getDeclaredMethod exception, object = " + object.getClass().getName() + ", methodName = " + methodName);
    }

    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     *
     * @param receiver       子类对象
     * @param methodName     父类中的方法名
     * @param parameterTypes 父类中的方法参数类型
     * @param parameters     父类中的方法参数
     * @return 父类中方法的执行结果
     */
    private static Object invokeMethod(Object receiver, String methodName, Class<?>[] parameterTypes, Object... parameters) {
        try {
            Method method = getDeclaredMethod(receiver, methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(receiver, parameters);
        } catch (Exception e) {
            throw new RuntimeException("invokeMethod exception, receiver = " + receiver.getClass().getName() + ", methodName = " + methodName, e);
        }
    }

    /**
     * 直接调用对象静态方法, 而忽略修饰符(private, protected, default)
     *
     * @param className      子类
     * @param methodName     父类中的方法名
     * @param parameterTypes 父类中的方法参数类型
     * @param parameters     父类中的方法参数
     * @return 父类中方法的执行结果
     */
    private static Object invokeStaticMethod(String className, String methodName, Class<?>[] parameterTypes, Object... parameters) {
        try {
            Method method = getDeclaredMethod(getClass(className), methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(null, parameters);
        } catch (Exception e) {
            throw new RuntimeException("invokeStaticMethod exception, className = " + className + ", methodName = " + methodName, e);
        }
    }

    /**
     * 获取Class
     *
     * @param className 类名
     * @return Class
     */
    private static Class getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("getClass exception, className = " + className, e);
        }
    }

    /**
     * 创建一个新实例
     *
     * @param className
     * @param parameterTypes
     * @param args
     * @return
     */
    private static Object newInstance(String className, Class<?>[] parameterTypes, Object... args) {
        try {
            Constructor constructor = getClass(className).getConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("newInstance exception, className = " + className
                    + ", parameterTypes = " + Arrays.toString(parameterTypes)
                    + ", args = " + Arrays.toString(args), e);
        }
    }
}
