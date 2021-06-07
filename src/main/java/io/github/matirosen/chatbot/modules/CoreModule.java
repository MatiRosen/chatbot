package io.github.matirosen.chatbot.modules;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.listeners.ChatListener;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.managers.MessageManager;
import me.yushust.inject.Binder;
import me.yushust.inject.Module;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreModule implements Module {

    private final BotPlugin plugin;

    public CoreModule(BotPlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void configure(Binder binder){
        binder.bind(JavaPlugin.class).toInstance(plugin);
        binder.bind(BotPlugin.class).toInstance(plugin);

        binder.bind(FileManager.class).singleton();
        binder.bind(MessageManager.class).singleton();
        binder.bind(ChatListener.class).singleton();
    }
}