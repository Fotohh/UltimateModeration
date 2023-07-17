package me.xaxis.ultimatemoderation.configmanagement;

public enum Permissions {

    STAFF_CHAT("ump.staff_chat"),
    MUTE_BYPASS("ump.mute_bypass"),
    SPY("ump.spy"),
    VANISH_BYPASS("ump.vanish.bypass"),
    ;

    private final String permission;

    Permissions(String permission){
        this.permission = permission;
    }

    public String s(){
        return permission;
    }

}
