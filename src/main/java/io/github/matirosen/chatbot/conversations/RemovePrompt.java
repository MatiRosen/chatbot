package io.github.matirosen.chatbot.conversations;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.chatComponents.ComponentRenderer;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.utils.MessageHandler;
import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public class RemovePrompt extends StringPrompt {

    private final JavaPlugin plugin;
    private final FileManager fileManager;
    private final ComponentRenderer componentRenderer;
    private final String key, s;
    private final int i;

    public RemovePrompt(JavaPlugin plugin, FileManager fileManager, ComponentRenderer componentRenderer, String key, String s, int i){
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.componentRenderer = componentRenderer;
        this.key = key;
        this.s = s;
        this.i = i;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext){
        MessageHandler messageHandler = BotPlugin.getMessageHandler();
        FileConfiguration config = plugin.getConfig();
        String message = messageHandler.getMessage("remove-" + s);

        message = message + Utils.format("\n&b"+ fileManager.get("messages").getStringList(key + "."+ s).get(i-1));

        return message.replace("%yes%", config.getString("yes-word")).replace("%no%", config.getString("no-word"));
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String t){
        Objects.requireNonNull(t);
        FileConfiguration config = plugin.getConfig();
        Player player = (Player) context.getForWhom();

        if (t.equalsIgnoreCase("y") || t.equalsIgnoreCase(config.getString("yes-word"))){
            FileConfiguration messagesFile = fileManager.get("messages");
            List<String> messages = messagesFile.getStringList(key + "."+ s);
            messages.remove(i-1);
            messagesFile.set(key + "."+ s, messages);

            fileManager.saveFile(messagesFile, "messages.yml");
            componentRenderer.sendComponents(player, key, s, 1);
            player.sendRawMessage(BotPlugin.getMessageHandler().getMessage(s + "-removed"));
            return Prompt.END_OF_CONVERSATION;
        }

        else if (t.equalsIgnoreCase("n") || t.equalsIgnoreCase(config.getString("no-word"))){
            componentRenderer.sendComponents(player, key, s, 1);
            player.sendRawMessage(BotPlugin.getMessageHandler().getMessage(s + "-remove-cancelled"));
            return Prompt.END_OF_CONVERSATION;
        }

        context.getForWhom().sendRawMessage(BotPlugin.getMessageHandler().getMessage("write-y-n")
                .replace("%yes%", config.getString("yes-word"))
                .replace("%no%", config.getString("no-word")));
        return this;
    }
}
