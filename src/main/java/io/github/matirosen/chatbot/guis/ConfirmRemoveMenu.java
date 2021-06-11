package io.github.matirosen.chatbot.guis;

import io.github.matirosen.chatbot.managers.MessageManager;
import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.gui.abstraction.item.ItemClickable;
import team.unnamed.gui.core.gui.type.GUIBuilder;
import team.unnamed.gui.core.item.type.ItemBuilder;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ConfirmRemoveMenu {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private KeyMenu keyMenu;
    @Inject
    private MessageManager messageManager;
    @Inject
    private MainMenu mainMenu;

    public Inventory build(String key){
        return GUIBuilder.builder(Utils.format(plugin.getConfig().getString("remove-menu.title").replace("%key%", key)), 1)
                .addItem(getItemClickable(2, "confirm", key))
                .addItem(getItemClickable(5, "cancel", key))
                .build();
    }

    private ItemClickable getItemClickable(int slot, String s, String key){
        FileConfiguration config = plugin.getConfig();
        String keyFile = "remove-menu.items." + s;

        Material material = Material.valueOf(config.getString(keyFile + ".material").toUpperCase());
        String name = Utils.format(config.getString(keyFile + ".name").replace("%key%", key));
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
                    if (s.equalsIgnoreCase("confirm")){
                        messageManager.removeKey(key);
                        event.getWhoClicked().openInventory(mainMenu.build());
                    } else if (s.equalsIgnoreCase("cancel")){
                        event.getWhoClicked().openInventory(keyMenu.build(key));
                    }
                    event.setCancelled(true);
                    return true;
                })
                .build();
    }
}