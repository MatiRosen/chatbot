package io.github.matirosen.chatbot.guis;

import io.github.matirosen.chatbot.managers.MessageManager;
import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.gui.item.ItemBuilder;
import team.unnamed.gui.menu.item.ItemClickable;
import team.unnamed.gui.menu.type.MenuInventory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfirmRemoveMenu {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private KeyMenu keyMenu;
    @Inject
    private MessageManager messageManager;
    @Inject
    private MainMenu mainMenu;

    public Inventory build(String key, Player player){
        String title = Utils.format(Objects.requireNonNull(plugin.getConfig().getString("remove-menu.title")).replace("%key%", key));
        return MenuInventory.newBuilder(title, 1)
                .item(getItemClickable(2, "confirm", key, player))
                .item(getItemClickable(5, "cancel", key, player))
                .build();
    }

    private ItemClickable getItemClickable(int slot, String s, String key, Player player){
        FileConfiguration config = plugin.getConfig();
        String keyFile = "remove-menu.items." + s;

        Material material = Material.valueOf(config.getString(keyFile + ".material").toUpperCase());
        String name = Utils.format(config.getString(keyFile + ".name").replace("%key%", key));
        List<String> lore = new ArrayList<>();

        for (String t : config.getStringList(keyFile + ".lore")){
            lore.add(Utils.format(t.replace("%key%", key)));
        }

        return ItemClickable.builder(slot)
                .item(ItemBuilder.builder(material)
                        .name(name)
                        .lore(lore)
                        .build())
                .action(inventory -> {
                    if (s.equalsIgnoreCase("confirm")){
                        messageManager.removeKey(key);
                        player.openInventory(mainMenu.build(player));
                    } else if (s.equalsIgnoreCase("cancel")){
                        player.openInventory(keyMenu.build(key, player));
                    }
                    //event.setCancelled(true);
                    return true;
                })
                .build();
    }
}