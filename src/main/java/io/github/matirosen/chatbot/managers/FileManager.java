package io.github.matirosen.chatbot.managers;

import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FileManager {

    @Inject
    private JavaPlugin plugin;

    private final HashMap<String, FileConfiguration> configurationMap = new HashMap<>();

    private static final String LANG_FORMAT = "language-%s.yml";


    public void loadAllFileConfigurations(){
        configurationMap.clear();

        loadFileConfiguration("language-en.yml");
        loadFileConfiguration("language-es.yml");

        configurationMap.put("config", loadFileConfiguration("config.yml"));
        configurationMap.put("messages", loadFileConfiguration("messages.yml"));

        String lang = String.format(LANG_FORMAT, get("config").getString("language"));
        FileConfiguration langFileConfiguration = loadFileConfiguration(lang);

        if (langFileConfiguration == null) {
            Bukkit.getConsoleSender().sendMessage(Utils.format(get("config"), "&c[ChatBot] Language file not found. Using 'language-en.yml'"));
            langFileConfiguration = loadFileConfiguration(String.format(LANG_FORMAT, "en"));

            if (langFileConfiguration == null) {
                Bukkit.getConsoleSender().sendMessage(Utils.format(get("config"), "&c[ChatBot] language-en.yml file not found. Disabling..."));
                Bukkit.getPluginManager().disablePlugin(plugin);
                return;
            }
        }
        configurationMap.put("language", langFileConfiguration);
        this.fileUpdater();
    }

    private void fileUpdater(){
        FileConfiguration config = get("config");
        boolean changed = false;

        if (!config.contains("hex-formatting.start-tag")){
            config.set("hex-formatting.start-tag", "&#");
            changed = true;
        }
        if (!config.contains("hex-formatting.end-tag")){
            config.set("hex-formatting.end-tag", "");
            changed = true;
        }

        if (!config.contains("update-checker")){
            config.set("update-checker", true);
            changed = true;
        }

        if (changed){
            saveFile(config, "config.yml");
            configurationMap.put("config", loadFileConfiguration("config.yml"));
        }
    }

    public void saveFile(FileConfiguration fileConfiguration, String path){
        File file = new File(plugin.getDataFolder(), path);
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration get(String name){
        return configurationMap.get(name);
    }

    public FileConfiguration loadFileConfiguration(String name){
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()){
            try{
                plugin.saveResource(name, true);
            } catch (IllegalArgumentException e){
                return null;
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
}
