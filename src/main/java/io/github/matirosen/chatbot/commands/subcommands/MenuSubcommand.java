package io.github.matirosen.chatbot.commands.subcommands;

import io.github.matirosen.chatbot.guis.MainMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class MenuSubcommand extends Subcommand{

    @Inject
    private MainMenu mainMenu;

    protected MenuSubcommand() {
        super("menu", "chatbot.menu", 1);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        player.openInventory(mainMenu.build(player));
        return true;
    }
}
