package com.avensys.rts.accountservice.enums;

/**
 * Author: Koh He Xiang
 * This enum is used to specify the permissions
 */
public enum Permission {
    ACCOUNT_READ("Account:Read"),
    ACCOUNT_WRITE("Account:Write"),
    ACCOUNT_DELETE("Account:Delete"),
    ACCOUNT_EDIT("Account:Edit"),

    ACCOUNT_NOACCESS("Account:NoAccess");



    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String toString() {
        return this.permission;
    }
}
