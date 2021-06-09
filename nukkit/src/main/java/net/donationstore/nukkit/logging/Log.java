package net.donationstore.nukkit.logging;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

import java.util.List;

public class Log {

    public static void send(CommandSender sender, String log) {
        if (sender instanceof Player) {
            sender.sendMessage(String.format("%s[Donation Store]%s: %s", TextFormat.GREEN, TextFormat.WHITE, log));
        } else {
            Log.toConsole(String.format("[Donation Store]: %s", log));
        }
    }

    public static void toConsole(String log) {
        System.out.printf("[Donation Store]: %s%n", log);
    }

    public static void displayLogs(CommandSender sender, List<String> logs) {
        for(String log: logs) {
            Log.send(sender, log);
        }
    }
}
