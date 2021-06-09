package net.donationstore.nukkit.queue;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Config;
import net.donationstore.commands.CommandManager;
import net.donationstore.models.Command;
import net.donationstore.models.request.UpdateCommandExecutedRequest;
import net.donationstore.models.response.PaymentsResponse;
import net.donationstore.models.response.QueueResponse;
import net.donationstore.nukkit.logging.Log;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class QueueTask {

    private CommandManager commandManager;

    public void run(Config config, PluginBase plugin) {
        plugin.getServer().getScheduler().scheduleDelayedRepeatingTask(plugin, () -> {
            plugin.getServer().getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {
                @Override
                public void onRun() {
                    if (config.getString("secret_key") == null || config.getString("webstore_api_location") == null) {
                        Log.toConsole("You must connect the plugin to your webstore before it can start executing purchased packages.");
                        Log.toConsole("Use /ds connect");
                    } else {
                        commandManager = new CommandManager(config.getString("secret_key"), config.getString("webstore_api_location"));
                        try {
                            UpdateCommandExecutedRequest updateCommandExecutedRequest = new UpdateCommandExecutedRequest();

                            QueueResponse queueResponse = commandManager.getCommands();

                            for (PaymentsResponse payment : queueResponse.payments) {
                                for (Command command : payment.commands) {

                                    Player player;

                                    if (queueResponse.webstore.webstoreType.equals("OFF")) {
                                        player = plugin.getServer().getPlayerExact(command.username);
                                    } else {
                                        Optional<Player> optionalPlayer = plugin.getServer().getPlayer(UUID.fromString(command.uuid));
                                        player = optionalPlayer.orElse(null);
                                    }

                                    boolean canExecuteCommand = false;

                                    if (command.requireOnline) {
                                        if (player != null) {
                                            canExecuteCommand = true;
                                        }
                                    } else {
                                        canExecuteCommand = true;
                                    }

                                    if (canExecuteCommand) {
                                        plugin.getServer().getScheduler().scheduleTask(plugin, () -> runCommand(command.command, plugin));
                                        updateCommandExecutedRequest.getCommands().add(command.id);
                                    }
                                }
                            }
                            commandManager.updateCommandsToExecuted(updateCommandExecutedRequest);
                        } catch (Exception e) {
                            Log.toConsole(e.getMessage());
                            Log.toConsole(e.getCause().toString());
                            Log.toConsole(Arrays.toString(e.getStackTrace()));
                        }
                    }
                }
            });
            // 4800
        }, 1, config.getInt("queue_delay") * 20);

    }

    public void runCommand(String command, PluginBase plugin) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
    }
}
