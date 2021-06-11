package io.github.matirosen.chatbot.conversations;

import io.github.matirosen.chatbot.BotMessage;
import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.guis.KeyMenu;
import io.github.matirosen.chatbot.guis.MainMenu;
import io.github.matirosen.chatbot.managers.MessageManager;
import io.github.matirosen.chatbot.utils.MessageHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ConfirmKeyCreationPrompt extends StringPrompt {

    private final JavaPlugin plugin;
    private final MessageManager messageManager;
    private final MainMenu mainMenu;
    private final KeyMenu keyMenu;
    private final String key;

    public ConfirmKeyCreationPrompt(JavaPlugin plugin, MessageManager messageManager, MainMenu mainMenu, KeyMenu keyMenu, String key){
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.mainMenu = mainMenu;
        this.keyMenu = keyMenu;
        this.key = key;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext){
        MessageHandler messageHandler = BotPlugin.getMessageHandler();
        FileConfiguration config = plugin.getConfig();

        return messageHandler.getMessage("finish-writing-key").replace("%key%", key)
                .replace("%yes%", config.getString("yes-word"))
                .replace("%no%", config.getString("no-word"));
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String s){
        Objects.requireNonNull(s);
        FileConfiguration config = plugin.getConfig();

        if (s.equalsIgnoreCase("y") || s.equalsIgnoreCase(config.getString("yes-word"))){
            Player player = (Player) context.getForWhom();
            BotMessage botMessage = new BotMessage(key);
            messageManager.saveMessage(botMessage);
            player.sendMessage(BotPlugin.getMessageHandler().getMessage("key-created"));
            player.openInventory(keyMenu.build(key));

            return Prompt.END_OF_CONVERSATION;
        }

        else if (s.equalsIgnoreCase("n") || s.equalsIgnoreCase(config.getString("no-word"))){
            return new CreateKeyPrompt(plugin, messageManager, mainMenu, keyMenu);
        }

        context.getForWhom().sendRawMessage(BotPlugin.getMessageHandler().getMessage("write-y-n")
                .replace("%yes%", config.getString("yes-word"))
                .replace("%no%", config.getString("no-word")));
        return this;
    }
}
