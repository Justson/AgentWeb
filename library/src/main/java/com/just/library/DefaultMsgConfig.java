package com.just.library;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cenxiaozhong on 2017/7/7.
 */

public final class DefaultMsgConfig {


    private DownLoadMsgConfig mDownLoadMsgConfig = null;

    private ChromeClientMsgCfg mChromeClientMsgCfg = new ChromeClientMsgCfg();

    public ChromeClientMsgCfg getChromeClientMsgCfg() {
        return mChromeClientMsgCfg;
    }

    DefaultMsgConfig() {

        mDownLoadMsgConfig = new DownLoadMsgConfig();
    }

    public DownLoadMsgConfig getDownLoadMsgConfig() {
        return mDownLoadMsgConfig;
    }

    public static class DownLoadMsgConfig implements Parcelable {


        private String mTaskHasBeenExist = "该任务已经存在 ， 请勿重复点击下载!";

        private String mTips = "提示";

        private String mHoneycomblow = "您正在使用手机流量 ， 继续下载该文件吗?";

        private String mDownLoad = "下载";

        private String mCancel = "取消";

        private String mDownLoadFail = "下载失败!";

        private String mLoading = "当前进度:%s";

        private String mTrickter = "您有一条新通知";

        private String mFileDownLoad = "文件下载";

        private String mClickOpen = "点击打开";

        private String preLoading = "即将开始下载文件";

        public String getPreLoading() {
            return preLoading;
        }

        public void setPreLoading(String preLoading) {
            this.preLoading = preLoading;
        }

        DownLoadMsgConfig() {

        }

        protected DownLoadMsgConfig(Parcel in) {
            mTaskHasBeenExist = in.readString();
            mTips = in.readString();
            mHoneycomblow = in.readString();
            mDownLoad = in.readString();
            mCancel = in.readString();
            mDownLoadFail = in.readString();
            mLoading = in.readString();
            mTrickter = in.readString();
            mFileDownLoad = in.readString();
            mClickOpen = in.readString();
        }

        public static final Creator<DownLoadMsgConfig> CREATOR = new Creator<DownLoadMsgConfig>() {
            @Override
            public DownLoadMsgConfig createFromParcel(Parcel in) {
                return new DownLoadMsgConfig(in);
            }

            @Override
            public DownLoadMsgConfig[] newArray(int size) {
                return new DownLoadMsgConfig[size];
            }
        };

        public String getTaskHasBeenExist() {
            return mTaskHasBeenExist;
        }

        public void setTaskHasBeenExist(String taskHasBeenExist) {
            mTaskHasBeenExist = taskHasBeenExist;
        }

        public String getTips() {
            return mTips;
        }

        public void setTips(String tips) {
            mTips = tips;
        }

        public String getHoneycomblow() {
            return mHoneycomblow;
        }

        public void setHoneycomblow(String honeycomblow) {
            mHoneycomblow = honeycomblow;
        }

        public String getDownLoad() {
            return mDownLoad;
        }

        public void setDownLoad(String downLoad) {
            mDownLoad = downLoad;
        }

        public String getCancel() {
            return mCancel;
        }

        public void setCancel(String cancel) {
            mCancel = cancel;
        }

        public String getDownLoadFail() {
            return mDownLoadFail;
        }

        public void setDownLoadFail(String downLoadFail) {
            mDownLoadFail = downLoadFail;
        }

        public String getLoading() {
            return mLoading;
        }

        public void setLoading(String loading) {
            mLoading = loading;
        }

        public String getTrickter() {
            return mTrickter;
        }

        public void setTrickter(String trickter) {
            mTrickter = trickter;
        }

        public String getFileDownLoad() {
            return mFileDownLoad;
        }

        public void setFileDownLoad(String fileDownLoad) {
            mFileDownLoad = fileDownLoad;
        }

        public String getClickOpen() {
            return mClickOpen;
        }

        public void setClickOpen(String clickOpen) {
            mClickOpen = clickOpen;
        }


        @Override
        public boolean equals(Object mo) {
            if (this == mo) return true;
            if (!(mo instanceof DownLoadMsgConfig)) return false;

            DownLoadMsgConfig mthat = (DownLoadMsgConfig) mo;

            if (!getTaskHasBeenExist().equals(mthat.getTaskHasBeenExist())) return false;
            if (!getTips().equals(mthat.getTips())) return false;
            if (!getHoneycomblow().equals(mthat.getHoneycomblow())) return false;
            if (!getDownLoad().equals(mthat.getDownLoad())) return false;
            if (!getCancel().equals(mthat.getCancel())) return false;
            if (!getDownLoadFail().equals(mthat.getDownLoadFail())) return false;
            if (!getLoading().equals(mthat.getLoading())) return false;
            if (!getTrickter().equals(mthat.getTrickter())) return false;
            if (!getFileDownLoad().equals(mthat.getFileDownLoad())) return false;
            return getClickOpen().equals(mthat.getClickOpen());

        }

        @Override
        public int hashCode() {
            int mresult = getTaskHasBeenExist().hashCode();
            mresult = 31 * mresult + getTips().hashCode();
            mresult = 31 * mresult + getHoneycomblow().hashCode();
            mresult = 31 * mresult + getDownLoad().hashCode();
            mresult = 31 * mresult + getCancel().hashCode();
            mresult = 31 * mresult + getDownLoadFail().hashCode();
            mresult = 31 * mresult + getLoading().hashCode();
            mresult = 31 * mresult + getTrickter().hashCode();
            mresult = 31 * mresult + getFileDownLoad().hashCode();
            mresult = 31 * mresult + getClickOpen().hashCode();
            return mresult;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mTaskHasBeenExist);
            dest.writeString(mTips);
            dest.writeString(mHoneycomblow);
            dest.writeString(mDownLoad);
            dest.writeString(mCancel);
            dest.writeString(mDownLoadFail);
            dest.writeString(mLoading);
            dest.writeString(mTrickter);
            dest.writeString(mFileDownLoad);
            dest.writeString(mClickOpen);
        }
    }


    public static final class ChromeClientMsgCfg {

        private FileUploadMsgConfig mFileUploadMsgConfig = new FileUploadMsgConfig();

        public FileUploadMsgConfig getFileUploadMsgConfig() {
            return mFileUploadMsgConfig;
        }

        public static final class FileUploadMsgConfig implements Parcelable {

            private String[] medias = new String[]{"相机", "文件选择器"};

            FileUploadMsgConfig() {

            }

            protected FileUploadMsgConfig(Parcel in) {
                medias = in.createStringArray();
            }

            public static final Creator<FileUploadMsgConfig> CREATOR = new Creator<FileUploadMsgConfig>() {
                @Override
                public FileUploadMsgConfig createFromParcel(Parcel in) {
                    return new FileUploadMsgConfig(in);
                }

                @Override
                public FileUploadMsgConfig[] newArray(int size) {
                    return new FileUploadMsgConfig[size];
                }
            };

            public void setMedias(String[] medias) {
                this.medias = medias;
            }

            public String[] getMedias() {
                return medias;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeStringArray(medias);
            }
        }
    }

}
