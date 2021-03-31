package com.swp.ihelp.google.firebase.fcm;

public enum PushNotificationParameter {

    SOUND("default"),
    COLOR("#5EAB46");

    private String value;

    PushNotificationParameter(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
