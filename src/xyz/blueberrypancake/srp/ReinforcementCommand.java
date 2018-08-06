package xyz.blueberrypancake.srp;

import java.util.HashMap;

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
                player.sendMessage("\u00A7aReinforcement mode enabled!");
            } else {
                player.sendMessage("\u00A7cReinforcement mode disabled!");
            }
        }

        return true;
    }

    public void resetPlayerState(Player player) {
        players.put(player.getDisplayName(), false);
    }

    public boolean getReinforcementMode(Player player) {
        return players.getOrDefault(player.getDisplayName(), false);
    }

}
