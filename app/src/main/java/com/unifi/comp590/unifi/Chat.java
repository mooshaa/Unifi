package com.unifi.comp590.unifi;

public class Chat {
    private String mUserName;
    private String mLastMessage;

    public String getmLastMessage() {
        return mLastMessage;
    }

    public void setmLastMessage(String mLastMessage) {
        this.mLastMessage = mLastMessage;
    }

    public Chat(String mUserName, String mLastMessage) {

        this.mUserName = mUserName;
        this.mLastMessage = mLastMessage;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }
}
