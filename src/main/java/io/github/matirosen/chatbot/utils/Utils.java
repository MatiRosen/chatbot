package io.github.matirosen.chatbot.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.gui.item.ItemBuilder;
import team.unnamed.gui.menu.item.ItemClickable;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public static String format(String s){
        return ChatColor.translateAlternateColorCodes('&',s);
    }

    public static String[] format(List<String> list){
        String[] formatted = new String[list.size()];

        for (int i = 0; i < list.size(); i ++){
            formatted[i] = format(list.get(i));
        }
        return formatted;
    }

    public static ItemClickable getChangePageItem(JavaPlugin plugin, String menu, String next, int pageNumber){
        FileConfiguration config = plugin.getConfig();

        int slot = next.equalsIgnoreCase("next") ? 50 : 48;
        Material material = Material.valueOf(config.getString(menu + next + "-page.material").toUpperCase());
        String name = Utils.format(config.getString(menu + next + "-page.name")
                .replace("%next_page%", pageNumber + "")
                .replace("%previous_page%", pageNumber + ""));
        List<String> lore = Arrays.asList(Utils.format(config.getStringList(menu + next + "-page.lore")));

        return ItemClickable.builder(slot)
                .item(ItemBuilder
                        .builder(material)
                        .name(name)
                        .lore(lore)
                        .build())
                .build();
    }
}
