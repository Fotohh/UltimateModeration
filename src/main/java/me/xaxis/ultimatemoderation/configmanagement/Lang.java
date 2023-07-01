package me.xaxis.ultimatemoderation.configmanagement;

public enum Lang {

    NO_PERMISSION("messages.player.error.no_permission", "&4You do not have permission to execute this command!"),
    STAFF_CHAT_UNTOGGLED("messages.player.staff_chat_untoggled","&4Staff chat has been untoggled!"),
    STAFF_CHAT_TOGGLED("messages.player.staff_chat_toggled", "&aStaff chat has been toggled!"),
    STAFF_CHAT_PREFIX("messages.prefix.staff_chat_prefix", "&7<&6Staff Chat&7>&b "),
    VANISH_LEAVE_MESSAGE("messages.player.vanish_leave_message", "&e%s has left the game!"),
    VANISH_JOIN_MESSAGE("messages.player.vanish_join_message", "&e%s has joined the game!"),
    ;

    public String getPath(){
        return path;
    }
    public String getDefaultValue(){
        return defaultValue;
    }

    private final String path;
    private final String defaultValue;

    Lang(String path, String value){
        this.path = path;
        this.defaultValue = value;
    }

}
