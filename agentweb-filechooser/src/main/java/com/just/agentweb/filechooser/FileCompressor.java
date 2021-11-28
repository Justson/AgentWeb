package com.just.agentweb.filechooser;

import android.net.Uri;
import android.webkit.ValueCallback;

import java.io.Serializable;

/**
 * @author cenxiaozhong
 * @date 2021/11/26
 * @since 1.0.0
 */
public class FileCompressor implements Serializable {

    private static FileCompressor sInstance = null;
    private FileCompressEngine mFileCompressEngine;

    FileCompressor() {
    }

    public static final FileCompressor getInstance() {
        if (sInstance == null) {
            synchronized (FileCompressor.class) {
                if (sInstance == null) {
                    sInstance = new FileCompressor();
                }
            }
        }
        return sInstance;
    }


    public void registerFileCompressEngine(FileCompressEngine valueCallback) {
        this.mFileCompressEngine = valueCallback;
    }

    public void unregisterFileCompressEngine(FileCompressEngine valueCallback) {
        this.mFileCompressEngine = null;
    }

    void fileCompress(String type, Uri[] uri, ValueCallback<Uri[]> callback) {
        if (mFileCompressEngine == null) {
            callback.onReceiveValue(uri);
        } else {
            mFileCompressEngine.compressFile(type, uri, callback);
        }
    }

    public interface FileCompressEngine {
        void compressFile(String type, Uri[] uri, ValueCallback<Uri[]> callback);
    }


}

