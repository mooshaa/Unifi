package com.unifi.comp590.unifi;

public class Message {
    private String message, type;
    private long time;
    private boolean seen;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    private String from;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Message(String message, String type, long time, boolean seen, String from) {

        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
    }

    public Message() {

    }
}
