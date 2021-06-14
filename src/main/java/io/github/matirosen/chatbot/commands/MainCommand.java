package io.github.matirosen.chatbot.commands;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.chatComponents.ComponentRenderer;
import io.github.matirosen.chatbot.guis.MainMenu;
import io.github.matirosen.chatbot.utils.Utils;
import io.github.matirosen.chatbot.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class MainCommand implements TabExecutor {

    @Inject
    private BotPlugin plugin;
    @Inject
    private FileManager fileManager;
    @Inject
    private MainMenu mainMenu;
    @Inject
    private ComponentRenderer componentRenderer;

    public void start(){
        Objects.requireNonNull(plugin.getCommand("chatbot")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args){
        if (sender instanceof ConsoleCommandSender) {
            if (args.length >= 1 && args[0].equalsIgnoreCase("reload")){
                plugin.reloadConfig();
                fileManager.loadAllFileConfigurations();
                Bukkit.getLogger().log(Level.INFO, Utils.format("[ChatBot] &aPlugin reloaded!"));
                return true;
            }
            Bukkit.getLogger().log(Level.INFO, "ChatBot command can only be executed in-game");
            return false;
        }

        Player player = (Player) sender;

        if (player.isConversing()){
            player.sendMessage(Utils.format(BotPlugin.getMessageHandler().getMessage("already-configuring")));
            return false;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("menu")){
            if (!player.hasPermission("chatbot.menu")){
                BotPlugin.getMessageHandler().send(player, "no-permission");
                return false;
            }
            player.openInventory(mainMenu.build());
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("help")){
            if (!player.hasPermission("chatbot.help")){
                BotPlugin.getMessageHandler().send(player, "no-permission");
                return false;
            }
            BotPlugin.getMessageHandler().sendList(player, "help-command");
            return true;
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")){
            if (!player.hasPermission("chatbot.reload")){
                BotPlugin.getMessageHandler().send(player, "no-permission");
                return false;
            }
            plugin.reloadConfig();
            fileManager.loadAllFileConfigurations();
            BotPlugin.getMessageHandler().send(player, "plugin-reloaded");
            return true;
        }

        player.sendMessage(Utils.format("&d------ &a&l[&6&lBotChat&a&l] &d------\n\n"
                + "&9Author: &eMatiRosen\n"
                + "&3Version: &e" + plugin.getDescription().getVersion())
                + "\n\n");

        BotPlugin.getMessageHandler().send(player, "use-help");

        player.sendMessage(Utils.format("\n&bhttps://github.com/MatiRosen/chatbot\n"
                + "&d------ &a&l[&6&lChatBot&a&l] &d------"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args){
        List<String> tab = new ArrayList<>();
        if (!(sender instanceof Player)) return tab;

        if (sender.hasPermission("chatbot.menu")) tab.add("menu");
        if (sender.hasPermission("chatbot.help")) tab.add("help");
        if (sender.hasPermission("chatbot.reload")) tab.add("reload");

        return tab;
    }
}
