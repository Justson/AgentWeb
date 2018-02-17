/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.just.agentweb.filechooser;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author cenxiaozhong
 * @date 2017/5/24
 */
public class FileParcel implements Parcelable {

    private int mId;
    private String mContentPath;
    private String mFileBase64;

    protected FileParcel(Parcel in) {
        mId = in.readInt();
        mContentPath = in.readString();
        mFileBase64 = in.readString();
    }

    public FileParcel(int id, String contentPath, String fileBase64) {
        this.mId = id;
        this.mContentPath = contentPath;
        this.mFileBase64 = fileBase64;

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
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getContentPath() {
        return mContentPath;
    }

    public void setContentPath(String contentPath) {
        this.mContentPath = contentPath;
    }

    public String getFileBase64() {
        return mFileBase64;
    }

    public void setFileBase64(String fileBase64) {
        this.mFileBase64 = fileBase64;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mContentPath);
        dest.writeString(mFileBase64);
    }

    @Override
    public String toString() {
        return "FileParcel{" +
                "mId=" + mId +
                ", mContentPath='" + mContentPath + '\'' +
                ", mFileBase64='" + mFileBase64 + '\'' +
                '}';
    }
}
