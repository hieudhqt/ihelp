package com.swp.ihelp.app.account;

public enum AccountRoleEnum {

    USER("user", "ROLE_USER"),
    MANAGER("manager", "ROLE_MANAGER"),
    ADMIN("admin", "ROLE_ADMIN");

    private String id, name;

    AccountRoleEnum(String id, String name) {
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
