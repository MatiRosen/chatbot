package io.github.matirosen.chatbot.conversations;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.guis.SeeMessageMenu;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.managers.MessageManager;
import io.github.matirosen.chatbot.utils.MessageHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class MessagePrompt extends StringPrompt {

    private final JavaPlugin plugin;
    private final MessageManager messageManager;
    private final SeeMessageMenu seeMessageMenu;
    private final FileManager fileManager;
    private final String key, path;

    public MessagePrompt(JavaPlugin plugin, FileManager fileManager, MessageManager messageManager, SeeMessageMenu seeMessageMenu, String key, String path){
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.seeMessageMenu = seeMessageMenu;
        this.key = key;
        this.path = path;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext){
        MessageHandler messageHandler = BotPlugin.getMessageHandler();
        FileConfiguration config = plugin.getConfig();
        String message = messageHandler.getMessage("cancel-any-time").replace("%cancel%", config.getString("cancel-word")) + "\n";

        if (path.equalsIgnoreCase("permission")){
            String perm = fileManager.get("messages").getString(key + ".permission");
            if (perm != null && !perm.isEmpty())
                message = message + messageHandler.getMessage("actual-permission").replace("%permission%", perm) + "\n";
        }

        return message + messageHandler.getMessage("write-" + path);
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String s){
        Objects.requireNonNull(s);
        FileConfiguration config = plugin.getConfig();

        if (s.equalsIgnoreCase(config.getString("cancel-word"))){
            Player player = (Player) context.getForWhom();
            player.sendRawMessage(BotPlugin.getMessageHandler().getMessage(path + "-cancelled"));
            return Prompt.END_OF_CONVERSATION;
        }

        return new ConfirmCreationPrompt(plugin, fileManager, seeMessageMenu, messageManager, key, s, path);
    }
}
