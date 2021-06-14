package io.github.matirosen.chatbot.conversations;

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

public class CreateKeyPrompt extends StringPrompt {

    private final JavaPlugin plugin;
    private final MainMenu mainMenu;
    private final KeyMenu keyMenu;
    private final MessageManager messageManager;

    public CreateKeyPrompt(JavaPlugin plugin, MessageManager messageManager, MainMenu mainMenu, KeyMenu keyMenu){
        this.mainMenu = mainMenu;
        this.messageManager = messageManager;
        this.keyMenu = keyMenu;
        this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext){
        MessageHandler messageHandler = BotPlugin.getMessageHandler();
        FileConfiguration config = plugin.getConfig();

        return messageHandler.getMessage("cancel-any-time").replace("%cancel%", config.getString("cancel-word"))
                + "\n" + messageHandler.getMessage("write-key");
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String s){
        Objects.requireNonNull(s);
        FileConfiguration config = plugin.getConfig();

        if (s.equalsIgnoreCase(config.getString("cancel-word"))){
            Player player = (Player) context.getForWhom();
            player.sendRawMessage(BotPlugin.getMessageHandler().getMessage("key-creation-cancelled"));
            player.openInventory(mainMenu.build());
            return Prompt.END_OF_CONVERSATION;
        }

        if (s.contains(" ")){
            Player player = (Player) context.getForWhom();
            player.sendRawMessage(BotPlugin.getMessageHandler().getMessage("only-one-word"));
            return this;
        }

        return new ConfirmCreationPrompt(plugin, messageManager, mainMenu, keyMenu, s, "key");
    }
}
