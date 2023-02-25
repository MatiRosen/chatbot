package io.github.matirosen.chatbot.guis;

import io.github.matirosen.chatbot.managers.FileManager;
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

    public Inventory build(String key, Player player){
        FileConfiguration config = plugin.getConfig();
        String title = Utils.format(Objects.requireNonNull(config.getString("key-menu.title")).replace("%key%", key));
        return MenuInventory.newBuilder(title, config.getInt("key-menu.rows"))
                .item(getItemClickable("messages", key, player))
                .item(getItemClickable("permission-responses", key, player))
                .item(getItemClickable("no-permission-responses", key, player))
                .item(getItemClickable("permission", key, player))
                .item(getItemClickable( "remove-key", key, player))
                .item(getItemClickable("back", key, player))
                .build();
    }

    private ItemClickable getItemClickable(String s, String key, Player player){
        FileConfiguration config = plugin.getConfig();
        String keyFile = "key-menu.items." + s;

        Material material = Material.valueOf(config.getString( keyFile + ".material").toUpperCase());
        int slot = config.getInt(keyFile + ".slot");
        String name = Utils.format(config.getString(keyFile + ".name")
                .replace("%key%", key)
                .replace("%permission%", fileManager.get("messages").getString(key + ".permission")));
        List<String> lore = new ArrayList<>();
        for (String t : config.getStringList(keyFile + ".lore")){
            lore.add(Utils.format(t.replace("%key%", key)));
        }

        return ItemClickable.builder(slot)
                .item(ItemBuilder.builder(material)
                        .name(name)
                        .lore(lore)
                        .build())
                .action(event -> {
                    if (s.equalsIgnoreCase("messages")){
                        player.openInventory(seeMessageMenu.build(key, "messages", player));
                    }else if (s.equalsIgnoreCase("permission-responses")){
                        player.openInventory(seeMessageMenu.build(key, "permission-responses", player));
                    }else if (s.equalsIgnoreCase("no-permission-responses")){
                        player.openInventory(seeMessageMenu.build(key, "no-permission-responses", player));
                    }else if (s.equalsIgnoreCase("permission")){
                        player.openInventory(seeMessageMenu.build(key, "permission", player));
                    } else if (s.equalsIgnoreCase("remove-key")){
                        player.openInventory(confirmRemoveMenu.build(key, player));
                    } else if (s.equalsIgnoreCase("back")){
                        player.openInventory(mainMenu.build(player));
                    } else return false;
                    event.setCancelled(true);
                    return true;
                })
                .build();
    }
}
