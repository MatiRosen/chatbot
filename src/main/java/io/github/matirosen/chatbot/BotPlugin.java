package io.github.matirosen.chatbot;

import io.github.matirosen.chatbot.listeners.ChatListener;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.modules.CoreModule;
import me.yushust.inject.Injector;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;

public class BotPlugin extends JavaPlugin {

    @Inject
    private ChatListener chatListener;
    @Inject
    private FileManager fileManager;

    public void onload(){
        try {
            Injector injector = Injector.create(new CoreModule(this));
            injector.injectMembers(this);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onEnable(){
        fileManager.loadAllFileConfigurations();
    }
}