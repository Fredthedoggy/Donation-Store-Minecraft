package net.donationstore.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.donationstore.commands.CommandFactory;
import net.donationstore.nukkit.logging.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandHandler {

    private final CommandFactory commandFactory;

    public CommandHandler() {
        commandFactory = new CommandFactory();
    }

    public void handleCommand(Config config, Command command, CommandSender sender, PluginBase plugin, String[] args) {
        if (config.getString("secret_key") == null || config.getString("webstore_api_location") == null) {
            if (args[0].equals("connect")) {
                if (sender instanceof Player) {
                    Log.send(sender, "For security reasons, that command can only be executed from the console.");
                } else {
                    try {
                        Log.displayLogs(sender, commandFactory.getCommand(args).runCommand());

                        plugin.getConfig().set("secret_key", args[1]);
                        plugin.getConfig().set("webstore_api_location", args[2]);

                        plugin.saveConfig();
                    } catch(Exception exception) {
                            Log.toConsole(exception.getMessage());
                    }
                }
            } else {
                Log.send(sender, "Cannot run commands as this plugin has not yet been setup.");
            }
        } else {
            try {
                List<String> listOfArgs = new ArrayList<>();

                listOfArgs.add(args[0]);
                listOfArgs.add(config.getString("secret_key"));
                listOfArgs.add(config.getString("webstore_api_location"));

                Collections.addAll(listOfArgs, Arrays.copyOfRange(args, 1, args.length));

                if (sender instanceof Player) {
                    listOfArgs.add(((Player) sender).getUniqueId().toString());
                    if(sender.hasPermission(commandFactory.getCommand(listOfArgs.toArray(new String[0])).getPermission())) {
                        Log.displayLogs(sender, commandFactory.getCommand(listOfArgs.toArray(new String[0])).runCommand());
                    } else {
                        Log.send(sender, "You don't have permission to execute that command.");
                    }
                } else {
                    Log.displayLogs(sender, commandFactory.getCommand(listOfArgs.toArray(new String[0])).runCommand());
                }
            } catch(Exception exception) {
                System.out.println("Exception as String:" + exception);
                System.out.println("Stack Trace: ");
                exception.printStackTrace();
                Log.send(sender, exception.getMessage());
            }
        }
    }
}
