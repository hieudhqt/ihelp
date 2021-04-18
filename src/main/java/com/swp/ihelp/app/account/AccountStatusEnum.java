package com.swp.ihelp.app.account;

public enum AccountStatusEnum {

    ACTIVE(1, "active"),
    SUSPENDED(2, "suspended");

    private int id;
    private String name;

    AccountStatusEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
