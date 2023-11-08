package com.avensys.rts.accountservice.enums;

/**
 * Author: Koh He Xiang
 * This enum is used to specify the permissions
 */
public enum Permission {
    ACCOUNT_READ("Accounts:Read"),
    ACCOUNT_WRITE("Accounts:Write"),
    ACCOUNT_DELETE("Accounts:Delete"),
    ACCOUNT_EDIT("Accounts:Edit"),

    ACCOUNT_NOACCESS("Accounts:NoAccess");



    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String toString() {
        return this.permission;
    }
}
