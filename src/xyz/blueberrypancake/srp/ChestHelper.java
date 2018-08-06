package xyz.blueberrypancake.srp;

import org.bukkit.block.Block;
import org.bukkit.material.Chest;
import org.bukkit.material.MaterialData;

public class ChestHelper {

    public static boolean isChest(Block block) {
        MaterialData data = block.getState().getData();
        return data instanceof Chest;
    }
    
}
