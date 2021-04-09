package com.swp.ihelp.app.account;

public enum AccountStatusEnum {

    ACTIVE("user", "ROLE_USER"),
    DISABLED("manager", "ROLE_MANAGER");

    private String id, name;

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
