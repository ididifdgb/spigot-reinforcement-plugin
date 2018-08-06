package xyz.blueberrypancake.srp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class ReinforcementPlugin extends JavaPlugin {

    private SRPFile overworld = new SRPFile("overworld");

    private HashMap<Location, Reinforcement> rmap;

    private ReinforcementCommand command = new ReinforcementCommand();

    @Override
    public void onEnable() {
        try {
            overworld.readFromDisk(); // Make sure we read our reinforcement data from disk first
            rmap = overworld.getReinforcements(); // Get the map associated with the reinforcement data
            this.getCommand("reinforce").setExecutor(command);
            getServer().getPluginManager().registerEvents(new ReinforcementListener(rmap, command), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        overworld.dumpReinforcements(new ArrayList<Reinforcement>(rmap.values())); // Dump the reinforcement data to disk
    }
}
