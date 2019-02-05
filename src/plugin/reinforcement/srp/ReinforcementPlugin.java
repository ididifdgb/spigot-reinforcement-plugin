package plugin.reinforcement.srp;

import java.io.IOException;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

public class ReinforcementPlugin extends JavaPlugin {

    private SRPFile<Reinforcement, String> overworld = new SRPFile<Reinforcement, String>("reinforcements", new ReinforcementBufferable());
    private SRPFile<PlayerGroup, Short> groups = new SRPFile<PlayerGroup, Short>("groups", new PlayerGroupBufferable());

    private ReinforcementCommand command;
    private ReinforcementListener listener;

    private static HashMap<String, Reinforcement> refMap;
    private static HashMap<Short, PlayerGroup> groupMap;
    
    @Override
    public void onEnable() {
        try {
            this.command = new ReinforcementCommand();
            this.listener = new ReinforcementListener(command);
            
            overworld.readFromDisk(); // Make sure we read our reinforcement data from disk first
            refMap = overworld.getData(); // Set the map associated with reinforcement data
            
            groups.readFromDisk(); // The same process for groups
            groupMap = groups.getData();
            
            this.getCommand("reinforce").setExecutor(command);
            getServer().getPluginManager().registerEvents(listener, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static HashMap<String, Reinforcement> getRefMap() {
        return refMap;
    }
    
    public static HashMap<Short, PlayerGroup> getGroupMap() {
        return groupMap;
    }

    @Override
    public void onDisable() {
        overworld.dumpData(refMap); // Dump the reinforcement data to disk
        groups.dumpData(groupMap);
    }
}
