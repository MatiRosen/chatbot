package io.github.matirosen.chatbot.managers;

import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.util.List;
import java.util.Random;

public class MessageManager {

    @Inject
    private FileManager fileManager;
    @Inject
    private JavaPlugin plugin;

    public void sendMessageAsync(String message, Player player){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            FileConfiguration messagesFile = fileManager.get("messages");
            String key = getMatchedKey(messagesFile, message);
            if (key == null) return;

            String permission = messagesFile.getString(key + ".permission");
            List<String> answers = getResponsesFromKey(messagesFile, key, permission == null || player.hasPermission(permission));

            if (answers.isEmpty()) return;
            FileConfiguration config = plugin.getConfig();
            Random random = new Random();
            String randomResponse = answers.get(random.nextInt(answers.size()));

            new BukkitRunnable(){
                @Override
                public void run(){
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
                        onlinePlayer.sendMessage(Utils.format(randomResponse.replace("%prefix%", config.getString("prefix"))));
                    }
                }
            }.runTaskLater(plugin, config.getInt("delay"));
        });
    }

    private String getMatchedKey(FileConfiguration messagesFile, String message) {
        for (String key : messagesFile.getKeys(false)){
            for (String s : messagesFile.getStringList(key + ".messages")){
                if (message.contains(s.toLowerCase())) return key;
            }
        }
        return null;
    }

    private List<String> getResponsesFromKey(FileConfiguration messagesFile, String key, boolean hasPermission){
        return messagesFile.getStringList(key + (hasPermission ? ".permission-responses" : ".no-permission-responses"));
    }
}