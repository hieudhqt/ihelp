package com.swp.ihelp.google.firebase.fcm;

public enum NotificationParameter {

    SOUND("default"),
    COLOR("#5EAB46");

    private String value;

    NotificationParameter(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
