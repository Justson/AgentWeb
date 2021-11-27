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
    private FileCompressListener mFileCompressor;

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


    public void registerFileCompressListener(FileCompressListener valueCallback) {
        this.mFileCompressor = valueCallback;
    }

    public void unregisterFileCompressListener(FileCompressListener valueCallback) {
        this.mFileCompressor = null;
    }

    void fileCompress(Uri[] uri, ValueCallback<Uri[]> callback) {
        if (mFileCompressor == null) {
            callback.onReceiveValue(uri);
        } else {
            mFileCompressor.compressFile(uri, callback);
        }
    }

    public interface FileCompressListener {
        void compressFile(Uri[] uri, ValueCallback<Uri[]> callback);
    }


}

