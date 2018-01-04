package com.just.agentweb;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cenxiaozhong on 2018/1/3.
 */

public class Action implements Parcelable {

    public transient static final int ACTION_PERMISSION = 1;
    public transient static final int ACTION_FILE = 2;
    public transient static final int ACTION_CAMERA = 3;
    private String[] permissions = new String[]{};
    private int action;
    private int fromIntention;


    public String[] getPermissions() {
        return permissions;
    }

    public int getAction() {
        return action;
    }

    public Action() {

    }


    protected Action(Parcel in) {
        permissions = in.createStringArray();
        action = in.readInt();
        fromIntention = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(permissions);
        dest.writeInt(action);
        dest.writeInt(fromIntention);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Action> CREATOR = new Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel in) {
            return new Action(in);
        }

        @Override
        public Action[] newArray(int size) {
            return new Action[size];
        }
    };

    public int getFromIntention() {
        return fromIntention;
    }

    public static Action createPermissionsAction(String[] permissions) {
        Action mAction = new Action();
        mAction.setAction(Action.ACTION_PERMISSION);
        mAction.setPermissions(permissions);
        return mAction;
    }

    public Action setFromIntention(int fromIntention) {
        this.fromIntention = fromIntention;
        return this;
    }


    public void setAction(int action) {
        this.action = action;
    }

    public void setPermissions(String[] permissions) {
        permissions = permissions;
    }
}
