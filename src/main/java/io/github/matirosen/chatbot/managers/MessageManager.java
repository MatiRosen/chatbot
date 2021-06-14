package io.github.matirosen.chatbot.managers;

import io.github.matirosen.chatbot.BotMessage;
import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.utils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.text.Normalizer;
import java.util.*;

public class MessageManager {

    @Inject
    private FileManager fileManager;
    @Inject
    private JavaPlugin plugin;
    private final Map<Player, Long> coolDownMap = new HashMap<>();

    public Set<String> getKeys(){
        return fileManager.get("messages").getKeys(false);
    }

    public void saveMessage(BotMessage botMessage){
        FileConfiguration messagesFile = fileManager.get("messages");
        String key = botMessage.getKey();
        if (!messagesFile.getKeys(false).contains(key)) createSections(messagesFile, key);

        List<String> messages = messagesFile.getStringList(key +".messages");
        messages.addAll(botMessage.getMessages());
        List<String> permResponses = messagesFile.getStringList(key +".permission-responses");
        permResponses.addAll(botMessage.getPermissionResponses());
        List<String> noPermResponses = messagesFile.getStringList(key +".no-permission-responses");
        noPermResponses.addAll(botMessage.getNoPermissionResponses());

        messagesFile.set(key + ".messages", messages);
        messagesFile.set(key + ".permission-responses", permResponses);
        messagesFile.set(key + ".no-permission-responses", noPermResponses);
        messagesFile.set(key + ".permission", botMessage.getPermission());

        fileManager.saveFile(messagesFile, "messages.yml");
    }

    public void removeKey(String key){
        FileConfiguration messages = fileManager.get("messages");
        messages.set(key, null);
        fileManager.saveFile(messages, "messages.yml");
    }

    public void sendMessageAsync(String message, Player player){
        if (coolDownMap.containsKey(player)){
            int secondsPassed = (int) (System.currentTimeMillis()/1000 - coolDownMap.get(player)/1000);
            int coolDown = plugin.getConfig().getInt("player-cooldown");

            if (secondsPassed < coolDown){
                player.sendMessage(BotPlugin.getMessageHandler().getMessage("player-in-cooldown")
                        .replace("%time%", String.valueOf(coolDown - secondsPassed)));
                return;
            }
            coolDownMap.remove(player);
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendMessage(message, player));
    }

    private void createSections(FileConfiguration fileConfiguration, String key){
        fileConfiguration.createSection(key);
        fileConfiguration.createSection(key + ".messages");
        fileConfiguration.createSection(key + ".permission");
        fileConfiguration.createSection(key + ".permission-responses");
        fileConfiguration.createSection(key + ".no-permission-responses");
    }

    private void sendMessage(String message, Player player){
        FileConfiguration messagesFile = fileManager.get("messages");
        String key = getMatchedKey(messagesFile, message);
        if (key == null) return;

        String permission = messagesFile.getString(key + ".permission");
        List<String> answers = getResponsesFromKey(messagesFile, key, permission == null || player.hasPermission(permission) || permission.isEmpty());

        if (answers.isEmpty()) return;
        FileConfiguration config = plugin.getConfig();
        Random random = new Random();
        String randomResponse = answers.get(random.nextInt(answers.size()));

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            randomResponse = PlaceholderAPI.setPlaceholders(player, randomResponse);
        }

        String finalRandomResponse = randomResponse;
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
                    onlinePlayer.sendMessage(Utils.format(finalRandomResponse.replace("%prefix%", config.getString("prefix"))
                            .replace("%player%", player.getDisplayName())));
                }
                if (!player.hasPermission("chatbot.bypass.cooldown")){
                    coolDownMap.put(player, System.currentTimeMillis());
                }
            }
        }.runTaskLater(plugin, config.getInt("delay"));
    }

    private String getMatchedKey(FileConfiguration messagesFile, String message) {
        for (String key : getKeys()){
            for (String s : messagesFile.getStringList(key + ".messages")){
                if (message.contains(Normalizer.normalize(s.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", ""))) return key;
            }
        }
        return null;
    }

    private List<String> getResponsesFromKey(FileConfiguration messagesFile, String key, boolean hasPermission){
        return messagesFile.getStringList(key + (hasPermission ? ".permission-responses" : ".no-permission-responses"));
    }
}