package com.unifi.comp590.unifi;

public class Users {
    public String user_name;
    public String user_image;
    public String user_status;
    public String user_thumbnail;

    public Users() {
    }

    public Users(String user_name, String user_image, String user_status, String user_thumbnail) {
        this.user_name = user_name;
        this.user_image = user_image;
        this.user_status = user_status;
        this.user_thumbnail = user_thumbnail;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_thumbnail() {
        return user_thumbnail;
    }

    public void setUser_thumbnail(String user_thumbnail) {
        this.user_thumbnail = user_thumbnail;
    }
}
