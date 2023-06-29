package me.xaxis.ultimatemoderation.configmanagement;

public enum Permissions {

    STAFF_CHAT("um.staff_chat");

    private final String permission;

    Permissions(String permission){
        this.permission = permission;
    }

    public String s(){
        return permission;
    }

}
