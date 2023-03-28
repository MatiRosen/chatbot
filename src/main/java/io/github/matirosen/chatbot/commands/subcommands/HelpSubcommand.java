package io.github.matirosen.chatbot.commands.subcommands;

import io.github.matirosen.chatbot.utils.MessageHandler;
import org.bukkit.command.CommandSender;

import javax.inject.Inject;

public class HelpSubcommand extends Subcommand{

    @Inject
    private MessageHandler messageHandler;

    protected HelpSubcommand(){
        super("help","chatbot.help", 1);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args){

        messageHandler.sendList(sender, "help-command");
        return true;
    }
}
