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
import team.unnamed.gui.abstraction.item.ItemClickable;
import team.unnamed.gui.core.gui.type.GUIBuilder;
import team.unnamed.gui.core.item.type.ItemBuilder;

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

    public Inventory build(){
        FileConfiguration config = plugin.getConfig();
        List<ItemStack> entities = new ArrayList<>();

        String[] lore = Utils.format(config.getStringList("main-menu.items.keys.lore"));

        for (String key : messageManager.getKeys()){
            List<String> messageLore = new ArrayList<>();
            messageLore.add(Utils.format("&7" + key));
            messageLore.addAll(Arrays.asList(lore));

            entities.add(ItemBuilder.newBuilder(Material.valueOf(config.getString("main-menu.items.keys.material").toUpperCase()))
                    .setName(Utils.format(config.getString("main-menu.items.keys.name").replace("%key%", key)))
                    .setLore(messageLore)
                    .build());
        }

        return GUIBuilder.builderPaginated(ItemStack.class, Utils.format(config.getString("main-menu.title")))
                .setBounds(0, 45)
                .setEntities(entities)
                .setItemParser(item -> ItemClickable.builder()
                        .setItemStack(item)
                        .setAction(event -> {
                            event.getWhoClicked().openInventory(keyMenu.build(item.getItemMeta().getLore().get(0).substring(2)));
                            event.setCancelled(true);
                            return true;
                        })
                        .build())
                .addItem(ItemClickable.builder(53)
                        .setItemStack(ItemBuilder.newBuilder(Material.valueOf(config.getString("main-menu.items.create.material").toUpperCase()))
                                .setName(Utils.format(config.getString("main-menu.items.create.name")))
                                .setLore(Utils.format(config.getStringList("main-menu.items.create.lore")))
                                .build())
                        .setAction(event -> {
                            Player player = (Player) event.getWhoClicked();
                            player.closeInventory();
                            event.setCancelled(true);
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
                .setNextPageItem(page ->
                        Utils.getChangePageItem(plugin, "main-menu.items.", "next", page))
                .setPreviousPageItem(page ->
                        Utils.getChangePageItem(plugin, "main-menu.items.","previous", page))
                .build();
    }
}
