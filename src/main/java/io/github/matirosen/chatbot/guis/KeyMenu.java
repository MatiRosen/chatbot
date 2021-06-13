package io.github.matirosen.chatbot.guis;

import io.github.matirosen.chatbot.chatComponents.ComponentRenderer;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.gui.abstraction.item.ItemClickable;
import team.unnamed.gui.core.gui.type.GUIBuilder;
import team.unnamed.gui.core.item.type.ItemBuilder;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class KeyMenu {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private ConfirmRemoveMenu confirmRemoveMenu;
    @Inject
    private SeeMessageMenu seeMessageMenu;
    @Inject
    private FileManager fileManager;
    @Inject
    private MainMenu mainMenu;

    public Inventory build(String key){
        FileConfiguration config = plugin.getConfig();

        return GUIBuilder.builder(Utils.format(config.getString("key-menu.title").replace("%key%", key)), 1)
                .addItem(getItemClickable(0, "messages", key))
                .addItem(getItemClickable(1, "permission-responses", key))
                .addItem(getItemClickable(2, "no-permission-responses", key))
                .addItem(getItemClickable(3, "permission", key))
                .addItem(getItemClickable(4, "remove-key", key))
                .addItem(getItemClickable(8, "back", key))
                .build();
    }

    private ItemClickable getItemClickable(int slot, String s, String key){
        FileConfiguration config = plugin.getConfig();
        String keyFile = "key-menu.items." + s;

        Material material = Material.valueOf(config.getString( keyFile + ".material").toUpperCase());
        String name = Utils.format(config.getString(keyFile + ".name")
                .replace("%key%", key)
                .replace("%permission%", fileManager.get("messages").getString(key + ".permission")));
        List<String> lore = new ArrayList<>();
        for (String t : config.getStringList(keyFile + ".lore")){
            lore.add(Utils.format(t.replace("%key%", key)));
        }

        return ItemClickable.builder(slot)
                .setItemStack(ItemBuilder.newBuilder(material)
                        .setName(name)
                        .setLore(lore)
                        .build())
                .setAction(event -> {
                    if (s.equalsIgnoreCase("messages")){
                        event.getWhoClicked().openInventory(seeMessageMenu.build(key, "messages"));
                    }else if (s.equalsIgnoreCase("permission-responses")){
                        event.getWhoClicked().openInventory(seeMessageMenu.build(key, "permission-responses"));
                    }else if (s.equalsIgnoreCase("no-permission-responses")){
                        event.getWhoClicked().openInventory(seeMessageMenu.build(key, "no-permission-responses"));
                    }else if (s.equalsIgnoreCase("permission")){
                        event.getWhoClicked().closeInventory();
                        //conversations
                    } else if (s.equalsIgnoreCase("remove-key")){
                        event.getWhoClicked().openInventory(confirmRemoveMenu.build(key));
                    } else if (s.equalsIgnoreCase("back")){
                        event.getWhoClicked().openInventory(mainMenu.build());
                    }

                    event.setCancelled(true);
                    return true;
                })
                .build();
    }
}
