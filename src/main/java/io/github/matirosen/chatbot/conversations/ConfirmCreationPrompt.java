package io.github.matirosen.chatbot.conversations;

import io.github.matirosen.chatbot.BotMessage;
import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.guis.KeyMenu;
import io.github.matirosen.chatbot.guis.MainMenu;
import io.github.matirosen.chatbot.guis.SeeMessageMenu;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.managers.MessageManager;
import io.github.matirosen.chatbot.utils.MessageHandler;
import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ConfirmCreationPrompt extends StringPrompt {

    private final JavaPlugin plugin;
    private final MessageManager messageManager;
    private FileManager fileManager;
    private MainMenu mainMenu;
    private KeyMenu keyMenu;
    private final String key, creation;
    private String msg;
    private SeeMessageMenu seeMessageMenu;


    public ConfirmCreationPrompt(JavaPlugin plugin, MessageManager messageManager, MainMenu mainMenu, KeyMenu keyMenu, String key, String creation){
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.mainMenu = mainMenu;
        this.keyMenu = keyMenu;
        this.key = key;
        this.creation = creation;
    }

    public ConfirmCreationPrompt(JavaPlugin plugin, FileManager fileManager, SeeMessageMenu seeMessageMenu, MessageManager messageManager, String key, String msg, String creation){
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.messageManager = messageManager;
        this.creation = creation;
        this.msg = msg;
        this.seeMessageMenu = seeMessageMenu;
        this.key = key;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext){
        MessageHandler messageHandler = BotPlugin.getMessageHandler();
        FileConfiguration config = plugin.getConfig();
        String message = messageHandler.getMessage("finish-writing-" + creation).replace("%key%", key);

        if (!creation.equalsIgnoreCase("key")){
            message = message + Utils.format("\n&b"+ msg);
        }

        return message.replace("%yes%", config.getString("yes-word")).replace("%no%", config.getString("no-word"));
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String s){
        Objects.requireNonNull(s);
        FileConfiguration config = plugin.getConfig();

        if (s.equalsIgnoreCase("y") || s.equalsIgnoreCase(config.getString("yes-word"))){
            Player player = (Player) context.getForWhom();
            BotMessage botMessage = new BotMessage(key);

            if (creation.equalsIgnoreCase("messages")){
                botMessage.addMessage(msg);
            } else if (creation.equalsIgnoreCase("permission-responses")){
                botMessage.addPermissionResponse(msg);
            } else if (creation.equalsIgnoreCase("no-permission-responses")){
                botMessage.addNoPermissionResponse(msg);
            } else if (creation.equalsIgnoreCase("permission")){
                botMessage.addPermission(msg);
            }

            messageManager.saveMessage(botMessage);

            if (creation.equalsIgnoreCase("key")){
                player.openInventory(keyMenu.build(key));
            } else {
                player.openInventory(seeMessageMenu.build(key, creation));
            }

            BotPlugin.getMessageHandler().send(player, creation + "-created");

            return Prompt.END_OF_CONVERSATION;
        }

        else if (s.equalsIgnoreCase("n") || s.equalsIgnoreCase(config.getString("no-word"))){
            if (creation.equalsIgnoreCase("key")) return new CreateKeyPrompt(plugin, messageManager, mainMenu, keyMenu);

            return new MessagePrompt(plugin, fileManager, messageManager, seeMessageMenu, key, creation);
        }

        context.getForWhom().sendRawMessage(BotPlugin.getMessageHandler().getMessage("write-y-n")
                .replace("%yes%", config.getString("yes-word"))
                .replace("%no%", config.getString("no-word")));
        return this;
    }
}
