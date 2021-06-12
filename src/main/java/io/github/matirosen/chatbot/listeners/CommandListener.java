package io.github.matirosen.chatbot.listeners;

import io.github.matirosen.chatbot.chatComponents.ComponentRenderer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;

public class CommandListener implements Listener {

    @Inject
    private ComponentRenderer componentRenderer;

    @Inject
    public CommandListener(JavaPlugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        String[] args = event.getMessage().split(" ");

        if (args.length != 5 || (!args[0].equalsIgnoreCase("chatbot") && !args[1].equalsIgnoreCase("see"))) return;

        String key = args[2];
        String s = args[3];
        int page = Integer.parseInt(args[4]);

        componentRenderer.sendComponents(event.getPlayer(), key, s, page);
        event.setCancelled(true);
    }
}
