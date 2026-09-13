package me.xaxis.ultimatemoderation.permissions;

public enum Permissions {

    NOTE_COMMAND("ultimatemoderation.admin.note-command"),
    NOTE_COMMAND_VIEW("ultimatemoderation.admin.note-command.view"),
    NOTE_COMMAND_EDIT("ultimatemoderation.admin.note-command.edit"),
    NOTE_COMMAND_DELETE("ultimatemoderation.admin.note-command.delete"),
    ;

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
