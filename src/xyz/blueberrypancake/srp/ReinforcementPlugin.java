package xyz.blueberrypancake.srp;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

public class ReinforcementPlugin extends JavaPlugin {

    private SRPFile overworld = new SRPFile("reinforcements");

    private ReinforcementCommand command;
    private ReinforcementListener listener;

    @Override
    public void onEnable() {
        try {
            this.command = new ReinforcementCommand();
            this.listener = new ReinforcementListener(command);
            overworld.readFromDisk(); // Make sure we read our reinforcement data from disk first
            listener.initReinforcementMap(overworld.getReinforcements()); // Get the map associated with the reinforcement data
            this.getCommand("reinforce").setExecutor(command);
            getServer().getPluginManager().registerEvents(listener, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        overworld.dumpReinforcements(new ArrayList<Reinforcement>(this.listener.getReinforcementMap().values())); // Dump the reinforcement data to disk
    }
}
