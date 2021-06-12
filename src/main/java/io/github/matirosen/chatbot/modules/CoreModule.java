package io.github.matirosen.chatbot.modules;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.chatComponents.ComponentRenderer;
import io.github.matirosen.chatbot.guis.ConfirmRemoveMenu;
import io.github.matirosen.chatbot.guis.KeyMenu;
import io.github.matirosen.chatbot.guis.MainMenu;
import io.github.matirosen.chatbot.guis.key.MessagesMenu;
import io.github.matirosen.chatbot.listeners.ChatListener;
import io.github.matirosen.chatbot.listeners.CommandListener;
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
        binder.bind(CommandListener.class).singleton();

        binder.bind(MainMenu.class).singleton();
        binder.bind(KeyMenu.class).singleton();
        binder.bind(ConfirmRemoveMenu.class).singleton();
        binder.bind(MessagesMenu.class).singleton();
        binder.bind(ComponentRenderer.class).singleton();
    }
}