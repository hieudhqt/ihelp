package com.swp.ihelp.app.account;

public enum AccountStatusEnum {

    ACTIVE("1", "active"),
    SUSPENDED("2", "suspended");

    private String id;
    private String name;

    AccountStatusEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
