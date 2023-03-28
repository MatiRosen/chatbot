package io.github.matirosen.chatbot.commands;

import io.github.matirosen.chatbot.BotPlugin;
import io.github.matirosen.chatbot.commands.subcommands.HelpSubcommand;
import io.github.matirosen.chatbot.commands.subcommands.MenuSubcommand;
import io.github.matirosen.chatbot.commands.subcommands.ReloadSubcommand;
import io.github.matirosen.chatbot.commands.subcommands.Subcommand;
import io.github.matirosen.chatbot.managers.FileManager;
import io.github.matirosen.chatbot.utils.MessageHandler;
import io.github.matirosen.chatbot.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainCommand implements TabExecutor {

    @Inject
    private BotPlugin plugin;
    @Inject
    private FileManager fileManager;
    @Inject
    private MessageHandler messageHandler;
    @Inject
    private HelpSubcommand helpSubcommand;
    @Inject
    private MenuSubcommand menuSubcommand;
    @Inject
    private ReloadSubcommand reloadSubcommand;

    private final List<Subcommand> subcommands = new ArrayList<>();

    public void start(){
        Objects.requireNonNull(plugin.getCommand("chatbot")).setExecutor(this);
        subcommands.add(helpSubcommand);
        subcommands.add(menuSubcommand);
        subcommands.add(reloadSubcommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player)){
            Player player = (Player) sender;
            if (player.isConversing()){
                player.sendMessage(Utils.format(fileManager.get("config"), messageHandler.getMessage("already-configuring")));
                return false;
            }
        }

        if (args.length == 0){
            helpSubcommand.execute(sender, args);
            return true;
        }

        String name = args[0];
        Subcommand subCommand = subcommands.stream().filter(subC ->
                subC.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (subCommand == null || !subCommand.argsLengthMatches(args.length)) {
            helpSubcommand.execute(sender, args);
            return true;
        }

        if (!subCommand.hasPermission(sender)){
            FileConfiguration languageConfig = fileManager.get("language");
            sender.sendMessage(Utils.format(fileManager.get("config"), languageConfig.getString("no-permission")));
            return false;
        }

        if (!subCommand.execute(sender, Arrays.copyOfRange(args,1,args.length))){
            helpSubcommand.execute(sender, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        for (Subcommand subcommand : subcommands) {
            if (sender.hasPermission(subcommand.getPermission())){
                if (args.length == 1) list.add(subcommand.getName());
            }
        }
        return list;
    }
}