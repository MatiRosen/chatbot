package io.github.matirosen.chatbot.guis.key;

import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.gui.abstraction.item.ItemClickable;
import team.unnamed.gui.core.gui.type.GUIBuilder;
import team.unnamed.gui.core.item.type.ItemBuilder;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagesMenu {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private FileManager fileManager;

    public Inventory build(String key){
        FileConfiguration config = plugin.getConfig();

        List<ItemStack> entities = new ArrayList<>();

        String[] lore = Utils.format(config.getStringList("messages-menu.items.messages.lore"));
        int counter = 0;

        for (String message : fileManager.get("messages").getStringList(key + ".messages")){
            List<String> messageLore = new ArrayList<>();
            messageLore.add(Utils.format("&7" + counter));
            counter++;
            messageLore.addAll(Arrays.asList(lore));

            entities.add(ItemBuilder.newBuilder(Material.valueOf(config.getString("messages-menu.items.messages.material").toUpperCase()))
                    .setName(Utils.format(config.getString("messages-menu.items.messages.name").replace("%message%", message)))
                    .setLore(messageLore)
                    .build());
        }

        return GUIBuilder.builderPaginated(ItemStack.class, Utils.format(config.getString("messages-menu.title")).replace("%key%", key))
                .setBounds(0, 45)
                .setEntities(entities)
                .setItemParser(item -> ItemClickable.builder()
                        .setItemStack(item)
                        .setAction(event -> {
                            //event.getWhoClicked().openInventory(keyMenu.build(item.getItemMeta().getLore().get(0).substring(2)));
                            event.setCancelled(true);
                            return true;
                        })
                        .build())
                .setNextPageItem(page -> Utils.getChangePageItem(plugin, "", "next", page))
                .setPreviousPageItem(page -> Utils.getChangePageItem(plugin, "", "previous", page))
                .build();
    }

    private ItemClickable getItem(int slot, String s, String key){
        FileConfiguration config = plugin.getConfig();
        String keyFile = "messages-menu.items." + s;

        //Material material =

        return null;
    }
}
