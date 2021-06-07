package io.github.matirosen.chatbot.listeners;

import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.text.Normalizer;

public class ChatListener implements Listener {

    @Inject
    private FileManager fileManager;
    @Inject
    private MessageManager messageManager;
    @Inject
    private JavaPlugin plugin;

    @Inject
    public ChatListener(JavaPlugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event){
        String message = Normalizer.normalize(event.getMessage().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        messageManager.sendMessageAsync(message, event.getPlayer());
    }
}
