package io.github.matirosen.chatbot.utils;

import io.github.matirosen.chatbot.managers.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.List;

public class MessageHandler {

    @Inject
    private FileManager fileManager;
    @Inject
    private JavaPlugin plugin;

    public void send(CommandSender player, String id){
        ConfigurationSection messageSection = fileManager.get("language");

        String message = messageSection.getString(id);
        if (message == null || message.isEmpty()) return;

        player.sendMessage(format(message.replace("%prefix%", fileManager.get("config").getString("prefix"))
                .replace("%player_name%", player.getName())));
    }

    public void sendList(CommandSender player, String id){
        ConfigurationSection messageSection = fileManager.get("language");

        List<String> messageList = messageSection.getStringList(id);

        if (messageList == null || messageList.isEmpty()) return;

        for (String message : messageList){
            player.sendMessage(format(message.replace("%prefix%", fileManager.get("config").getString("prefix"))
                    .replace("%player_name%", player.getName())
                    .replace("%author%", plugin.getDescription().getAuthors().get(0))));
        }
    }

    public String getMessage(String id){
        ConfigurationSection messageSection = fileManager.get("language");

        String message = messageSection.getString(id);
        if (message == null || message.isEmpty()) return "";

        return format(message.replace("%prefix%", fileManager.get("config").getString("prefix")));
    }

    private String format(String s){
        return ChatColor.translateAlternateColorCodes('&',s);
    }
}
