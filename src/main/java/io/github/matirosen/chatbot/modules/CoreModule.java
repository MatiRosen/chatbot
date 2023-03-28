package io.github.matirosen.chatbot.modules;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.chatComponents.ComponentRenderer;
import io.github.matirosen.chatbot.commands.subcommands.HelpSubcommand;
import io.github.matirosen.chatbot.commands.subcommands.MenuSubcommand;
import io.github.matirosen.chatbot.commands.subcommands.ReloadSubcommand;
import io.github.matirosen.chatbot.guis.ConfirmRemoveMenu;
import io.github.matirosen.chatbot.guis.KeyMenu;
import io.github.matirosen.chatbot.guis.MainMenu;
import io.github.matirosen.chatbot.guis.SeeMessageMenu;
import io.github.matirosen.chatbot.listeners.ChatListener;
import io.github.matirosen.chatbot.listeners.CommandListener;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.managers.MessageManager;
import io.github.matirosen.chatbot.utils.MessageHandler;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.inject.Binder;
import team.unnamed.inject.Module;

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
        binder.bind(SeeMessageMenu.class).singleton();
        binder.bind(MessageHandler.class).singleton();

        binder.bind(ComponentRenderer.class).singleton();

        binder.bind(HelpSubcommand.class).singleton();
        binder.bind(MenuSubcommand.class).singleton();
        binder.bind(ReloadSubcommand.class).singleton();

    }
}