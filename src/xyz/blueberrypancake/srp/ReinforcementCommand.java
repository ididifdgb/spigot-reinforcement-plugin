package xyz.blueberrypancake.srp;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReinforcementCommand implements CommandExecutor {

    private HashMap<String, Boolean> players = new HashMap<String, Boolean>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean enabled = players.getOrDefault(player.getDisplayName(), false);
            players.put(player.getDisplayName(), !enabled);

            if (!enabled) {
                player.sendMessage(ChatColor.GREEN + "Reinforcement mode enabled!");
            } else {
                player.sendMessage(ChatColor.RED + "Reinforcement mode disabled!");
            }
        }

        return true;
    }

    public boolean getReinforcementMode(Player player) {
        return players.getOrDefault(player.getDisplayName(), false);
    }
    
    public void disableReinforcementMode(Player player) {
        players.put(player.getDisplayName(), false);
    }

}
