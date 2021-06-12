package io.github.matirosen.chatbot.chatComponents;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.utils.MessageHandler;
import io.github.matirosen.chatbot.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ComponentRenderer {

    @Inject
    private FileManager fileManager;

    public void sendComponents(Player player, String key, String s, int page){
        List<String> list = fileManager.get("messages").getStringList(key + "." + s);
        if (list.isEmpty()) return;

        List<BaseComponent[]> components = getComponents(list, key, s, page);
        if (page > components.size()) return;

        player.spigot().sendMessage(components.get(page - 1));
    }

    private List<BaseComponent[]> getComponents(List<String> list, String key, String s, int page){
        MessageHandler messageHandler = BotPlugin.getMessageHandler();
        ComponentBuilder componentBuilder = new ComponentBuilder(messageHandler.getMessage("see-separator") + "\n");
        String title = messageHandler.getMessage("see-" + s + "-title")
                .replace("%page%", String.valueOf(page))
                .replace("%key%", key) + "\n\n";

        componentBuilder.append(title);
        List<BaseComponent[]> baseComponentList = new ArrayList<>();

        for (int i = 1; i < (list.size() + 1); i++){
            componentBuilder.append(list.get(i - 1) + "\n\n")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.format("&cClick to remove")).create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));

            if (i % 5 == 0 && i != list.size()){
                if (page == 1){
                    baseComponentList.add(getArrow("next", key, s, page, componentBuilder, false).
                            append("\n" + messageHandler.getMessage("see-separator")).reset().create());
                }else{
                    baseComponentList.add(getMiddleArrows(key, s, page, componentBuilder));
                }
                componentBuilder = new ComponentBuilder(title);
            } else if(i == list.size()){
                if (page > 1){
                    baseComponentList.add(getArrow("previous", key, s, page, componentBuilder, false).
                            append("\n" + messageHandler.getMessage("see-separator")).reset().create());
                } else {
                    baseComponentList.add(componentBuilder.append("\n" + messageHandler.getMessage("see-separator")).reset().create());
                }
            }
        }
        return baseComponentList;
    }

    private ComponentBuilder getArrow(String next, String key, String s, int page, ComponentBuilder componentBuilder, boolean hasPrevious){
        MessageHandler messageHandler = BotPlugin.getMessageHandler();

        String arrow = messageHandler.getMessage(next + "-arrow");
        String hover = messageHandler.getMessage("hover-" + next + "-arrow");
        int actualPage = next.equalsIgnoreCase("next") ? page + 1 : page - 1;
        String spaces = hasPrevious ?  next.equalsIgnoreCase("next") ? "                  " : "              "
                : next.equalsIgnoreCase("next") ? "                                   " : "              ";


        componentBuilder.append(spaces).reset()
                .append(arrow)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatbot see " + key + " " + s + " " + actualPage));

        return componentBuilder;
    }

    private BaseComponent[] getMiddleArrows(String key, String s, int page, ComponentBuilder componentBuilder){
        MessageHandler messageHandler = BotPlugin.getMessageHandler();

        getArrow("previous", key, s, page, componentBuilder, true);
        componentBuilder = getArrow("next", key, s, page, componentBuilder, true).append("\n" + messageHandler.getMessage("see-separator")).reset();

        return componentBuilder.create();
    }
}
