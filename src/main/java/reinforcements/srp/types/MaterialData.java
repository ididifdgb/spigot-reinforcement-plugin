package reinforcements.srp.types;

import org.bukkit.Material;

public class MaterialData {
    
    public Material material;
    public short strength;
    public byte id;

    public MaterialData(Material material, short strength, byte id) {
        this.material = material;
        this.strength = strength;
        this.id = id;
    }
}
