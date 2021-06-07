package io.github.matirosen.chatbot.listeners;

import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.text.Normalizer;
import java.util.List;
import java.util.Random;

public class ChatListener implements Listener {

    @Inject
    private FileManager fileManager;
    @Inject
    private JavaPlugin plugin;

    @Inject
    public ChatListener(JavaPlugin plugin){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event){
        String message = Normalizer.normalize(event.getMessage().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            FileConfiguration messagesFile = fileManager.get("messages");
            for (String key : messagesFile.getKeys(false)){
                List<String> messages = messagesFile.getStringList(key + ".messages");
                for (String s : messages){
                    System.out.println(s);
                    if (!message.contains(s.toLowerCase())) continue;

                    String permission = messagesFile.getString(key + ".permission");
                    boolean hasPermission = permission == null || event.getPlayer().hasPermission(permission);
                    String perm = hasPermission ? ".permission-responses" : ".no-permission-responses";
                    List<String> answers = messagesFile.getStringList(key + perm);

                    if (answers.isEmpty()) return;
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            Random random = new Random();
                            String randomResponse = answers.get(random.nextInt(answers.size()));
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.sendMessage(Utils.format(randomResponse.replace("%prefix%", plugin.getConfig().getString("prefix"))));
                            }
                        }
                    }.runTaskLater(plugin, 20);
                    return;
                }
            }
        });
    }
}
