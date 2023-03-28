package io.github.matirosen.chatbot.guis;

import io.github.matirosen.chatbot.chatComponents.ComponentRenderer;
import io.github.matirosen.chatbot.conversations.MessagePrompt;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.managers.MessageManager;
import io.github.matirosen.chatbot.utils.MessageHandler;
import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.gui.item.ItemBuilder;
import team.unnamed.gui.menu.item.ItemClickable;
import team.unnamed.gui.menu.type.MenuInventory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class SeeMessageMenu {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private ComponentRenderer componentRenderer;
    @Inject
    private KeyMenu keyMenu;
    @Inject
    private MessageManager messageManager;
    @Inject
    private FileManager fileManager;
    @Inject
    private MessageHandler messageHandler;

    public Inventory build(String key, String path, Player player){
        FileConfiguration config = plugin.getConfig();
        int rows = config.getInt(path + "-menu.rows");

        return MenuInventory.newBuilder(Utils.format(config, config.getString(path + "-menu.title").replace("%key%", key)), rows)
                .item(getItemClickable("create", path, key, player))
                .item(getItemClickable("see", path, key, player))
                .item(getItemClickable("back", path, key, player))
                .build();
    }

    private ItemClickable getItemClickable(String s, String path, String key, Player player){
        FileConfiguration config = plugin.getConfig();
        String keyFile = path + "-menu.items." + s;

        String permission = fileManager.get("messages").getString(key + ".permission");

        if (permission == null){
            permission = "";
        }

        Material material = Material.valueOf(config.getString( keyFile + ".material").toUpperCase());
        String name = Utils.format(config, config.getString(keyFile + ".name").replace("%permission%", permission));
        int slot = config.getInt(keyFile + ".slot");
        List<String> lore = Arrays.asList(Utils.format(config, config.getStringList(keyFile + ".lore")));

        return ItemClickable.builder(slot)
                .item(ItemBuilder.builder(material).name(name).lore(lore).build())
                .action(event -> {
                    if (s.equalsIgnoreCase("create")){
                        ConversationFactory cf = new ConversationFactory(plugin);
                        Conversation conversation = cf
                                .withFirstPrompt(new MessagePrompt(plugin, fileManager, messageManager, this, key, path, messageHandler))
                                .withLocalEcho(false)
                                .withTimeout(plugin.getConfig().getInt("time-out"))
                                .buildConversation(player);
                        conversation.begin();

                        player.closeInventory();
                    } else if (s.equalsIgnoreCase("see")){
                        componentRenderer.sendComponents(player, key, path, 1);
                        player.closeInventory();
                    } else if (s.equalsIgnoreCase("back")){
                        player.openInventory(keyMenu.build(key, player));
                    }

                    event.setCancelled(true);
                    return true;
                }).build();
    }
}
