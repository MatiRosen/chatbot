package io.github.matirosen.chatbot.utils;

import org.bukkit.ChatColor;

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
}
