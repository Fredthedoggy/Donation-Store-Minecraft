package net.donationstore.nukkit;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.donationstore.logging.Logging;

import net.donationstore.nukkit.command.CommandHandler;
import net.donationstore.nukkit.logging.Log;
import net.donationstore.nukkit.queue.QueueTask;

public class DonationStorePlugin extends PluginBase {

    private PluginBase plugin;
    private QueueTask queueTask;
    private Config config = getConfig();
    private CommandHandler commandHandler;

    @Override
    public void onEnable() {

        plugin = this;

        Log.toConsole(String.format(Logging.enableLog(), "Nukkit", "v2.3"));

        config = plugin.getConfig();

        if (config.getInt("queue_delay") == 0) {
            config.set("queue_delay", 180);
        }

        saveConfig();

        queueTask = new QueueTask();
        commandHandler = new CommandHandler();

        queueTask.run(config, plugin);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("ds")) {

            if (args.length == 0) {
                Log.send(sender, "Webstore and Helpdesk for Game Servers");
                Log.send(sender, "Bukkit Plugin - Version 2.3");
                Log.send(sender, "https://donationstore.net");
                Log.send(sender, "Type /ds help for command information");
            } else {
                commandHandler.handleCommand(config, command, sender, plugin, args);
            }
        }

        return true;
    }

    @Override
    public void onDisable() {
        Log.toConsole("Stopping plugin, bye bye!");
    }
}
