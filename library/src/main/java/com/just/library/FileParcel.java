package com.just.library;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cenxiaozhong on 2017/5/24.
 */

public class FileParcel implements Parcelable {

    int id;
    private String contentPath;
    private String fileBase64;

    protected FileParcel(Parcel in) {
        id = in.readInt();
        contentPath = in.readString();
        fileBase64 = in.readString();
    }

    public FileParcel(int id, String contentPath, String fileBase64) {
        this.id = id;
        this.contentPath = contentPath;
        this.fileBase64 = fileBase64;

    }

    public static final Creator<FileParcel> CREATOR = new Creator<FileParcel>() {
        @Override
        public FileParcel createFromParcel(Parcel in) {
            return new FileParcel(in);
        }

        @Override
        public FileParcel[] newArray(int size) {
            return new FileParcel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getFileBase64() {
        return fileBase64;
    }

    public void setFileBase64(String fileBase64) {
        this.fileBase64 = fileBase64;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(contentPath);
        dest.writeString(fileBase64);
    }

    @Override
    public String toString() {
        return "FileParcel{" +
                "id=" + id +
                ", contentPath='" + contentPath + '\'' +
                ", fileBase64='" + fileBase64 + '\'' +
                '}';
    }
}
