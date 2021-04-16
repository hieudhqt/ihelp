package com.swp.ihelp.app.status;

public enum AccountStatusEnum {
    ACTIVE("1"),
    SUSPENDED("2");

    private String id;

    AccountStatusEnum(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
