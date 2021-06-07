package io.github.matirosen.chatbot;

import io.github.matirosen.chatbot.commands.MainCommand;
import io.github.matirosen.chatbot.listeners.ChatListener;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.modules.CoreModule;
import io.github.matirosen.chatbot.utils.MessageHandler;
import me.yushust.inject.Injector;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.gui.core.GUIListeners;

import javax.inject.Inject;

public class BotPlugin extends JavaPlugin {

    @Inject
    private ChatListener chatListener;
    @Inject
    private FileManager fileManager;
    @Inject
    private MainCommand mainCommand;

    private static MessageHandler messageHandler;

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
        mainCommand.start();

        messageHandler = new MessageHandler(fileManager);

        Bukkit.getPluginManager().registerEvents(new GUIListeners(), this);
    }

    public static MessageHandler getMessageHandler(){
        return messageHandler;
    }
}