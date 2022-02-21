package com.lazilette.presencode.model;

public class Absen {
    String message;
    String key;

    public Absen() {
    }

    public Absen(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
