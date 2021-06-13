package io.github.matirosen.chatbot.listeners;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.chatComponents.ComponentRenderer;
import io.github.matirosen.chatbot.managers.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.List;

public class CommandListener implements Listener {

    @Inject
    private ComponentRenderer componentRenderer;
    @Inject
    private FileManager fileManager;

    @Inject
    public CommandListener(JavaPlugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        String[] args = event.getMessage().split(" ");

        if (args.length != 5 || !args[0].equalsIgnoreCase("/chatbot")
                || (!args[1].equalsIgnoreCase("see") && !args[1].equalsIgnoreCase("remove"))) return;

        Player player = event.getPlayer();

        String key = args[2];
        String s = args[3];
        int page = Integer.parseInt(args[4]);

        if (args[1].equalsIgnoreCase("see")){
            componentRenderer.sendComponents(player, key, s, page);
        } else if (args[1].equalsIgnoreCase("remove")){
            if (!player.hasPermission("chatbot.remove")){
                BotPlugin.getMessageHandler().send(player, "no-permission");
                return;
            }
            FileConfiguration messagesFile = fileManager.get("messages");
            List<String> messages = messagesFile.getStringList(key + "."+ s);
            messages.remove(page-1);
            messagesFile.set(key + "."+ s, messages);

            fileManager.saveFile(messagesFile, "messages.yml");
            componentRenderer.sendComponents(player, key, s, 1);
        }

        event.setCancelled(true);
    }
}
