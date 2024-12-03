package xyz.lincolnsmp.main.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.lincolnsmp.main.api.External;
import static xyz.lincolnsmp.main.utils.Utils.encodeNumbers;

public class LogCommand implements CommandExecutor {

    private final External external;

    public LogCommand(External external) {
        this.external = external;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 5) {
            sender.sendMessage("Invalid syntax; usage: /log <x> <y> <z> <world> <block>");
            return true;
        }
        int[] coords = new int[3];
        for (int i = 0; i <= 2; i++) {
            try {
                coords[i] = Integer.parseInt(args[i]);
            }
            catch (NumberFormatException e) {
                sender.sendMessage("Invalid coordinates, please make sure you follow the correct format (eg: /log 74 60 21 world dirt)");
                return true;
            }
        }
        if (!sender.getServer().getWorld(args[3]).getBlockAt(coords[0], coords[1], coords[2]).getBlockData().getMaterial().toString().equalsIgnoreCase(args[4])) {
            sender.sendMessage("Block verification check failed; incorrect block ID.");
            return true;
        }
        String numbers = encodeNumbers(coords[0], coords[1], coords[2], player.getUniqueId(), args[3], external);
        sender.sendMessage("Open this link in your browser to view the logs:\nhttps://lincolnsmp.xyz/logs?d=" + numbers);
        return true;
    }
}
