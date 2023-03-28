package io.github.matirosen.chatbot.commands.subcommands;

import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.utils.MessageHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;

public class ReloadSubcommand extends Subcommand{

    @Inject
    private FileManager fileManager;
    @Inject
    private JavaPlugin plugin;
    @Inject
    private MessageHandler messageHandler;


    protected ReloadSubcommand() {
        super("reload","chatbot.reload", 1);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        fileManager.loadAllFileConfigurations();

        messageHandler.send(sender, "plugin-reloaded");
        return true;
    }
}
