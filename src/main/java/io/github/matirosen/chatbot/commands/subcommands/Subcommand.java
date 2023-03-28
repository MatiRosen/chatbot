package io.github.matirosen.chatbot.commands.subcommands;

import org.bukkit.command.CommandSender;

public abstract class Subcommand {

    private final String name;
    private String permission;
    private final int argsLength;

    protected Subcommand(String name, int argsLength) {
        this.name = name;
        this.argsLength = argsLength;
    }

    protected Subcommand(String name, String permission, int argsLength) {
        this(name, argsLength);
        this.permission = permission;
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean hasPermission(CommandSender sender) {
        if (permission == null || sender == null || sender.isOp()) return true;

        return sender.hasPermission(permission);
    }

    public boolean argsLengthMatches(int args) {
        return this.argsLength <= args;
    }
}
