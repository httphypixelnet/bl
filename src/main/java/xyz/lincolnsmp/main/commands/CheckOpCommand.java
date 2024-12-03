package xyz.lincolnsmp.main.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static org.bukkit.Bukkit.getServer;

public class CheckOpCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /checkop <player>");
            return true;
        }

        String targetPlayerName = args[0];
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);

        if (targetPlayer.hasPlayedBefore() || targetPlayer.isOnline()) {
            boolean isOp = targetPlayer.isOp();
            sender.sendMessage("Player '" + targetPlayerName + "' " + (isOp ? "is" : "is not") + " an operator. (Permission level: " + targetPlayer.getPlayer().getEffectivePermissions());
        } else {
            sender.sendMessage("Player '" + targetPlayerName + "' has never played on this server.");
        }
        return true;
    }
}