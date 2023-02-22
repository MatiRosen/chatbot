package io.github.matirosen.chatbot.guis;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.conversations.CreateKeyPrompt;
import io.github.matirosen.chatbot.managers.MessageManager;
import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.gui.item.ItemBuilder;
import team.unnamed.gui.menu.item.ItemClickable;
import team.unnamed.gui.menu.type.MenuInventory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainMenu {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private MessageManager messageManager;
    @Inject
    private KeyMenu keyMenu;

    public Inventory build(Player player){
        FileConfiguration config = plugin.getConfig();
        List<ItemStack> entities = new ArrayList<>();

        String[] lore = Utils.format(config.getStringList("main-menu.items.keys.lore"));

        for (String key : messageManager.getKeys()){
            List<String> messageLore = new ArrayList<>();
            messageLore.add(Utils.format("&7" + key));
            messageLore.addAll(Arrays.asList(lore));

            entities.add(ItemBuilder.builder(Material.valueOf(config.getString("main-menu.items.keys.material").toUpperCase()))
                    .name(Utils.format(config.getString("main-menu.items.keys.name").replace("%key%", key)))
                    .lore(messageLore)
                    .build());
        }

        return MenuInventory.newPaginatedBuilder(ItemStack.class, Utils.format(config.getString("main-menu.title")))
                .bounds(0, 45)
                .entities(entities)
                .entityParser(item -> ItemClickable.builder(2)
                        .item(item)
                        .action(event -> {
                            player.openInventory(keyMenu.build(item.getItemMeta().getLore().get(0).substring(2), player));
                            //event.setCancelled(true);
                            return true;
                        })
                        .build())
                .nextPageItem(page ->
                        Utils.getChangePageItem(plugin, "main-menu.items.", "next", page))
                .previousPageItem(page ->
                        Utils.getChangePageItem(plugin, "main-menu.items.","previous", page))
                .item(ItemClickable.builder(53)
                        .item(ItemBuilder.builder(Material.valueOf(config.getString("main-menu.items.create.material").toUpperCase()))
                                .name(Utils.format(config.getString("main-menu.items.create.name")))
                                .lore(Utils.format(config.getStringList("main-menu.items.create.lore")))
                                .build())
                        .action(inventory -> {
                            player.closeInventory();
                            //event.setCancelled(true);
                            if (!player.hasPermission("chatbot.add")){
                                BotPlugin.getMessageHandler().send(player, "no-permission");
                                return false;
                            }
                            ConversationFactory cf = new ConversationFactory(plugin);
                            Conversation conversation = cf
                                    .withFirstPrompt(new CreateKeyPrompt(plugin, messageManager, this, keyMenu))
                                    .withLocalEcho(false)
                                    .withTimeout(plugin.getConfig().getInt("time-out"))
                                    .buildConversation(player);
                            conversation.begin();
                            return true;
                        })
                        .build())
                .build();
    }
}
