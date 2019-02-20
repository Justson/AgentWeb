package com.just.agentweb;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cenxiaozhong
 * @date 2019/2/19
 * @since 1.0.0
 */
public class TargetMethods {
	private ConcurrentHashMap<String, Method> mTargetMethos = new ConcurrentHashMap<>();

	boolean exist(Object currentObject, String methodName, String method, Class... clazzs) {
		return false;
	}

	private Method getMethod(Object currentObject, String methodName, String method, Class... clazzs) {
		String key = currentObject.getClass().getName().concat(methodName);
		return null;
	}
}
