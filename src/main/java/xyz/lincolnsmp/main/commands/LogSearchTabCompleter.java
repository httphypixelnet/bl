package xyz.lincolnsmp.main.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import xyz.lincolnsmp.main.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogSearchTabCompleter implements TabCompleter {
    Constants constants = new Constants();
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        int[] list = constants.list;
        if (command.getName().equalsIgnoreCase("log")) {
            if (args.length == 1 || args.length == 2 || args.length == 3) {
                List<String> newList = new ArrayList<>();
                for (int j : list) {
                    newList.add(args[args.length - 1] + j);
                }
                return newList;
            }
            if (args.length == 4) {
                return Bukkit.getWorlds().stream()
                        .map(World::getName)
                        .filter(worldName -> worldName.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
}