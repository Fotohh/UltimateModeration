package me.xaxis.ultimatemoderation.configmanagement;

public enum Permissions {

    ;

    private final String permission;

    Permissions(String permission){
        this.permission = permission;
    }

    public String s(){
        return permission;
    }

}
