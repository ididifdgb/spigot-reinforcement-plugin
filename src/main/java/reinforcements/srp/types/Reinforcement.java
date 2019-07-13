package reinforcements.srp.types;

import org.bukkit.Location;
import org.bukkit.block.Block;

import reinforcements.interfaces.Keyable;

public class Reinforcement implements Keyable<String> {

    private int x;
    private int y;
    private int z;
    private short strength;
    private short group_id;
    private byte material;
    private byte dimension;
    
    Reinforcement(Location loc, short strength, short group_id, byte material, byte dimension) {
        this((int) loc.getX(), (int) loc.getY(), (int) loc.getZ(), strength, group_id, material, dimension);
    }
    
    public Reinforcement(int x, int y, int z, byte material, byte dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.material = material;
        this.dimension = dimension;
    }

    public Reinforcement(int x, int y, int z, short strength, short group_id, byte material, byte dimension) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.strength = strength;
        this.group_id = group_id;
        this.material = material;
        this.dimension = dimension;
    }

    public void decreaseStrength() {
        this.strength -= 1;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public short getStrength() {
        return this.strength;
    }

    public short getGroupID() {
        return this.group_id;
    }
    
    public byte getDimension() {
        return this.dimension;
    }
    
    public byte getMaterial() {
        return this.material;
    }

    public String toString() {
        return "(x:" + this.x + ",y:" + this.y + ",z:" + this.z + ",group_id:" + this.group_id + "strength:" + this.strength + ",dimension:" + this.dimension + ")";
    }

    @Override
    public String getKey() {
        return x + "," + y + "," + z + "," + dimension;
    }
    
    public static String getKey(int x, int y, int z, byte dimension) {
        return x + "," + y + "," + z + "," + dimension;
    }
    
    public static String getKey(Location location, byte dimension) {
        return Reinforcement.getKey(location.getBlockX(), location.getBlockY(), location.getBlockZ(), dimension);
    }
    
    public static String getKey(Block block, byte dimension) {
        return Reinforcement.getKey(block.getLocation(), dimension);
    }
}
