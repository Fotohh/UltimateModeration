package me.xaxis.ultimatemoderationplus.permissions;

public enum Permissions {

    NOTE_COMMAND_DELETE("ultimatemoderationplus.admin.note-command.delete"),
    NOTE_COMMAND_EDIT("ultimatemoderationplus.admin.note-command.edit"),
    NOTE_COMMAND_VIEW("ultimatemoderationplus.admin.note-command.view"),
    WARN_COMMAND_DELETE("ultimatemoderationplus.admin.warn-command.delete"),
    WARN_COMMAND_EDIT("ultimatemoderationplus.admin.warn-command.edit"),
    WARN_COMMAND_VIEW("ultimatemoderationplus.admin.warn-command.view"),

    ;

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
