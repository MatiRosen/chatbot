package io.github.matirosen.chatbot;

import org.bukkit.ChatColor;

public class Utils {

    public static String format(String s){
        return ChatColor.translateAlternateColorCodes('&',s);
    }
}
