package io.github.matirosen.chatbot;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import io.github.matirosen.chatbot.commands.MainCommand;
import io.github.matirosen.chatbot.listeners.ChatListener;
import io.github.matirosen.chatbot.listeners.CommandListener;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.modules.CoreModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.gui.menu.listener.InventoryClickListener;
import team.unnamed.gui.menu.listener.InventoryCloseListener;
import team.unnamed.gui.menu.listener.InventoryOpenListener;
import team.unnamed.inject.Injector;

import javax.inject.Inject;


public class BotPlugin extends JavaPlugin {

    @Inject
    private FileManager fileManager;
    @Inject
    private MainCommand mainCommand;
    @Inject
    private ChatListener chatListener;
    @Inject
    private CommandListener commandListener;

    public void onEnable(){
        try {
            Injector injector = Injector.create(new CoreModule(this));
            injector.injectMembers(this);
        } catch (Exception e){
            e.printStackTrace();
        }

        start();
    }

    private void start(){
        fileManager.loadAllFileConfigurations();
        mainCommand.start();
        chatListener.start();
        commandListener.start();

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new InventoryClickListener(), this);
        pluginManager.registerEvents(new InventoryOpenListener(), this);
        pluginManager.registerEvents(new InventoryCloseListener(this), this);

        int pluginId = 18072;
        new Metrics(this, pluginId);
        checkUpdates();
    }

    private void checkUpdates(){
        if (!fileManager.get("config").getBoolean("update-checker")) return;
        final String id = "93347";
        new UpdateChecker(this, UpdateCheckSource.SPIGOT, id)
                .setDownloadLink(id)
                .setDonationLink("https://paypal.me/RosenM00?country.x=AR&locale.x=es_XC")
                .setChangelogLink(id)
                .setNotifyOpsOnJoin(true)
                .setNotifyByPermissionOnJoin("chatbot.updatechecker")
                .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion())
                .checkEveryXHours(24)
                .checkNow();
    }

}