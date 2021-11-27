package com.just.agentweb.sample.utils;

/**
 * @author cenxiaozhong
 * @date 2021/11/27
 * @since 1.0.0
 */
public class FileUtils {

    public static String getExtensionByFilePath(String filePath){
        String fe = "";
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            fe = filePath.substring(i+1);
        }
        System.out.println("File extension is : "+fe);
        return fe;
    }
}

