package io.github.matirosen.chatbot.guis;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.conversations.CreateKeyPrompt;
import io.github.matirosen.chatbot.managers.MessageManager;
import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
import team.unnamed.gui.menu.type.StringLayoutMenuInventoryBuilder;

import javax.inject.Inject;
import java.util.*;

public class MainMenu {

    @Inject
    private JavaPlugin plugin;
    @Inject
    private MessageManager messageManager;
    @Inject
    private KeyMenu keyMenu;

    public Inventory build(Player player){
        FileConfiguration config = plugin.getConfig();
        String title = Utils.format(config.getString("main-menu.title"));
        int rows = config.getInt("main-menu.rows");

        StringLayoutMenuInventoryBuilder menu = MenuInventory.newPaginatedBuilder(ItemStack.class, title, rows)
                .entities(this.getEntities())
                .entityParser(item -> getKeyItem(item, player))
                .bounds(0, 54)
                .skippedSlots(getSkippedSlots())
                .nextPageItem(page -> getChangePageItem(page, "next"))
                .previousPageItem(page -> getChangePageItem(page, "previous"))
                .itemIfNoPreviousPage(getChangePageItem(0, "no-next"))
                .itemIfNoNextPage(getChangePageItem(0, "no-previous"));

        menu = this.buildLayout(menu, player);
        return menu.build();
    }

    private List<ItemStack> getEntities(){
        FileConfiguration config = plugin.getConfig();
        List<ItemStack> entities = new ArrayList<>();
        String[] lore = Utils.format(config.getStringList("main-menu.items.keys.lore"));
        String name = config.getString("main-menu.items.keys.name");
        Material material = Material.valueOf(config.getString("main-menu.items.keys.material").toUpperCase());

        for (String key : messageManager.getKeys()){
            List<String> messageLore = new ArrayList<>();
            messageLore.add(Utils.format("&7" + key));
            messageLore.addAll(Arrays.asList(lore));

            entities.add(ItemBuilder.builder(material)
                    .name(Utils.format(name.replace("%key%", key)))
                    .lore(messageLore)
                    .build());
        }

        return entities;
    }

    private ItemClickable getKeyItem(ItemStack item, Player player){
        return ItemClickable.builder()
                .item(item)
                .action(event -> {
                    String menuKey = item.getItemMeta().getLore().get(0).substring(2);
                    player.openInventory(keyMenu.build(menuKey, player));
                    event.setCancelled(true);
                    return true;
                })
                .build();
    }

    private StringLayoutMenuInventoryBuilder buildLayout(StringLayoutMenuInventoryBuilder menu, Player player){
        FileConfiguration config = plugin.getConfig();
        List<String> lines = Objects.requireNonNull(config.getConfigurationSection("main-menu.layout")).getStringList("lines");

        this.getItems(lines).forEach(menu::layoutItem);

        menu.layoutItem('c', getCreateItem(player));

        return menu.layoutLines(lines);
    }

    private Map<Character, ItemClickable> getItems(List<String> lines){
        FileConfiguration config = plugin.getConfig();
        Map<Character, ItemClickable> map = new HashMap<>();
        ConfigurationSection configurationSection = config.getConfigurationSection("main-menu.items");
        if (configurationSection == null) return map;

        for (String s : configurationSection.getKeys(false)){
            if (lines.stream().noneMatch(line -> line.contains(s))) continue;
            String[] materialName = configurationSection.getString(s + ".material").toUpperCase().split(":");
            String name = configurationSection.getString(s + ".name");
            String[] lore = Utils.format(configurationSection.getStringList(s + ".lore"));

            ItemClickable item = ItemClickable.onlyItem(createItemBuilder(materialName).name(Utils.format(name)).lore(lore).build());
            map.put(s.charAt(0), item);
        }
        return map;
    }

    private ItemClickable getCreateItem(Player player){
        FileConfiguration config = plugin.getConfig();
        Material material = Material.valueOf(config.getString("main-menu.items.create.material").toUpperCase());
        String name = Utils.format(config.getString("main-menu.items.create.name"));
        String[] lore = Utils.format(config.getStringList("main-menu.items.create.lore"));

        return ItemClickable.builder()
                .item(ItemBuilder.builder(material)
                        .name(name)
                        .lore(lore)
                        .build())
                .action(event -> {
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
                .build();
    }

    private ItemClickable getChangePageItem(int page, String next){
        FileConfiguration config = plugin.getConfig();
        String key = "main-menu.items." + next + "-page";
        String[] materialName = config.getString(key + ".material").toUpperCase().split(":");
        String name = Utils.format(config.getString(key + ".name"));
        String[] lore = Utils.format(config.getStringList(key + ".lore"));

        page = next.equalsIgnoreCase("next") ? page + 1 : page - 1;

        return ItemClickable.onlyItem(createItemBuilder(materialName)
                .name(name.replace("%page%", page + ""))
                .lore(lore)
                .build());
    }

    private ItemBuilder createItemBuilder(String[] materialName){
        ItemBuilder itemBuilder;
        if (materialName.length == 2){
            DyeColor dyeColor = DyeColor.valueOf(materialName[1]);
            itemBuilder = ItemBuilder.dyeBuilder(materialName[0], dyeColor);
        } else{
            itemBuilder = ItemBuilder.builder(Material.valueOf(materialName[0]));
        }

        return itemBuilder;
    }

    private List<Integer> getSkippedSlots(){
        FileConfiguration config = plugin.getConfig();
        List<String> lines = Objects.requireNonNull(config.getConfigurationSection("main-menu.layout")).getStringList("lines");
        List<Integer> skipped = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String s = lines.get(i);
            for (int j = 0; j < s.length(); j++) {
                char c = s.charAt(j);
                if (c != 'e') {
                    int index = j;
                    for (int k = 0; k < i; k++) {
                        index += lines.get(k).length();
                    }
                    skipped.add(index);
                }
            }
        }
        return skipped;
    }

}
