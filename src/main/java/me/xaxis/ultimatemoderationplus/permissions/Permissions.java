package me.xaxis.ultimatemoderationplus.permissions;

public enum Permissions {

    NOTE_COMMAND_DELETE("ultimatemoderationplus.admin.note-command.delete"),
    NOTE_COMMAND_EDIT("ultimatemoderationplus.admin.note-command.edit"),
    NOTE_COMMAND_VIEW("ultimatemoderationplus.admin.note-command.view"),
    WARN_COMMAND_DELETE("ultimatemoderationplus.admin.warn-command.delete"),
    WARN_COMMAND_EDIT("ultimatemoderationplus.admin.warn-command.edit"),
    WARN_COMMAND_VIEW("ultimatemoderationplus.admin.warn-command.view"),
    KICK_COMMAND("ultimatemoderationplus.admin.kick-command"),
    MUTE_COMMAND("ultimatemoderationplus.admin.mute-command"),
    UNMUTE_COMMAND("ultimatemoderationplus.admin.unmute-command"),
    BAN_COMMAND("ultimatemoderationplus.admin.ban-command"),
    UNBAN_COMMAND("ultimatemoderationplus.admin.unban-command"),
    TEMPBAN_COMMAND("ultimatemoderationplus.admin.tempban-command"),
    TEMPMUTE_COMMAND("ultimatemoderationplus.admin.tempmute-command")
    ;
    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
