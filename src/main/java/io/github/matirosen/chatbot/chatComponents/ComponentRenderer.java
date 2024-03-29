package io.github.matirosen.chatbot.chatComponents;

import com.google.common.base.Strings;
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
    @Inject
    private MessageHandler messageHandler;

    public void sendComponents(Player player, String key, String s, int page){
        List<String> list = fileManager.get("messages").getStringList(key + "." + s);
        if (s.equalsIgnoreCase("permission")){
            list = new ArrayList<>();
            list.add(fileManager.get("messages").getString(key + ".permission"));
        }
        if (list.isEmpty()) return;

        List<BaseComponent[]> components = getComponents(list, key, s, page);
        if (page > components.size()) return;

        player.spigot().sendMessage(components.get(page - 1));
    }

    private List<BaseComponent[]> getComponents(List<String> list, String key, String s, int page){
        ComponentBuilder componentBuilder = new ComponentBuilder(messageHandler.getMessage("see-separator") + "\n");
        String title = messageHandler.getMessage("see-" + s + "-title")
                .replace("%page%", String.valueOf(page))
                .replace("%key%", key) + "\n\n";

        componentBuilder.append(title);
        List<BaseComponent[]> baseComponentList = new ArrayList<>();

        for (int i = 1; i < (list.size() + 1); i++){
            componentBuilder.append(list.get(i - 1) + "\n\n")
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(Utils
                                    .format(fileManager.get("config"), messageHandler.getMessage("remove-hover"))).create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatbot remove " + key + " " + s + " " + i));

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
        String arrow = messageHandler.getMessage(next + "-arrow");
        String hover = messageHandler.getMessage("hover-" + next + "-arrow");
        int actualPage = next.equalsIgnoreCase("next") ? page + 1 : page - 1;

        String leftArrowSpace = Strings.repeat(" ", 14);
        String rightArrowSpace = hasPrevious ? Strings.repeat(" ", 18) : Strings.repeat(" ", 35);


        componentBuilder.append(next.equalsIgnoreCase("next") ? rightArrowSpace : leftArrowSpace).reset()
                .append(arrow)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatbot see " + key + " " + s + " " + actualPage));

        return componentBuilder;
    }

    private BaseComponent[] getMiddleArrows(String key, String s, int page, ComponentBuilder componentBuilder){
        getArrow("previous", key, s, page, componentBuilder, true);
        componentBuilder = getArrow("next", key, s, page, componentBuilder, true).append("\n" + messageHandler.getMessage("see-separator")).reset();

        return componentBuilder.create();
    }
}
