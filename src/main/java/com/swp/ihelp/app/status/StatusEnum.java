package com.swp.ihelp.app.status;

public enum StatusEnum {
    PENDING(2),
    APPROVED(3),
    COMPLETED(4),
    DISABLED(5),
    REJECTED(6),
    ONGOING(7);

    private int id;

    StatusEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
