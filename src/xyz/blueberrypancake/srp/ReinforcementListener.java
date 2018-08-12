package xyz.blueberrypancake.srp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Chest.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class ReinforcementListener implements Listener {

    private HashMap<Material, MaterialData> matMap;

    private ReinforcementCommand command;

    ReinforcementListener(ReinforcementCommand command) {
        this.matMap = new HashMap<Material, MaterialData>();
        this.command = command;

        this.initMaterialMap();
    }

    private void insertMaterial(Material material, short strength, byte id) {
        matMap.put(material, new MaterialData(material, strength, id));
    }

    private void initMaterialMap() {
        insertMaterial(Material.STONE, (short) 50, (byte) 1);
        insertMaterial(Material.IRON_INGOT, (short) 350, (byte) 2);
        insertMaterial(Material.OBSIDIAN, (short) 750, (byte) 3);
        insertMaterial(Material.DIAMOND, (short) 1800, (byte) 4);
    }

    private Material getMaterialFromID(byte id) {
        for (MaterialData m : matMap.values()) {
            if (m.id == id) {
                return m.material;
            }
        }
        return null;
    }

    private short getNewID() {
        try {
            return (short) (Collections.max(new ArrayList<Short>(ReinforcementPlugin.getGroupMap().keySet())) + (short) 1);
        } catch (Exception e) {
            return (short) 0;
        }
    }
    
    // Get the group ID of the Player from the Map
    private short getGroupID(Player player) {
        ArrayList<PlayerGroup> groups = new ArrayList<PlayerGroup>(ReinforcementPlugin.getGroupMap().values());
        for(PlayerGroup group : groups) {
            if(group.getUsername() == player.getDisplayName() && group.isOwner()) {
                return group.getID();
            }
        }
        return -1;
    }

    // Authenticate the player (check to see if they're in the group, if not, add them)
    private boolean authenticateUser(Player player, short groupID) {
        PlayerGroup group = ReinforcementPlugin.getGroupMap().get(groupID);
        return group != null && group.getUsername() == player.getDisplayName();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        byte dimension = (byte) player.getWorld().getEnvironment().ordinal();
        Reinforcement ref = getReinforcementAt(block, player, dimension);

        boolean mode = command.getReinforcementMode(player);

        if (mode) {
            player.sendMessage(ChatColor.AQUA + "Disabled reinforcement mode.");
            command.disableReinforcementMode(player);
            event.setCancelled(true);
            return;
        }

        // Attempt to damage the reinforcement
        if (ref != null) {
            if (ref.getStrength() > 0 && !authenticateUser(player, ref.getGroupID())) {
                ref.decreaseStrength();
                if (player != null) {
                    player.sendMessage(ChatColor.RED + block.getType().toString() + " is locked.");
                }
            } else {
                // Reinforcement is destroyed, drop an item and remove it
                Material material = getMaterialFromID(ref.getMaterial());
                if (material != null) {
                    player.getWorld().dropItem(block.getLocation(), new ItemStack(material));
                }
                ReinforcementPlugin.getRefMap().remove(ref.getKey());
            }
            event.setCancelled(true);
        }
    }

    private Chest getChest(Block block) {
        BlockData data = block.getBlockData();
        if (data instanceof Chest) {
            return (Chest) data;
        }
        return null;
    }

    // Gets a reinforcement at a position in world (special case for chests)
    private Reinforcement getReinforcementAt(Block block, Player player, byte dimension) {
        String key = Reinforcement.getKey(block, dimension);
        Reinforcement ref = ReinforcementPlugin.getRefMap().get(key);

        // If we can't find a reinforcement then this might be a double chest, check the
        // other side
        if (ref == null) {
            Chest chest = getChest(block);
            boolean isDoubleChest = chest != null && chest.getType() != Type.SINGLE;

            if (isDoubleChest) {
                BlockFace facing = chest.getFacing();
                Block other = null;

                if (facing == BlockFace.EAST || facing == BlockFace.WEST) {
                    int dz = (facing == BlockFace.EAST ? 1 : -1) * (chest.getType() == Type.LEFT ? 1 : -1);
                    other = player.getWorld().getBlockAt(block.getX(), block.getY(), block.getZ() + dz);
                } else if (facing == BlockFace.NORTH || facing == BlockFace.SOUTH) {
                    int dx = (facing == BlockFace.SOUTH ? 1 : -1) * (chest.getType() == Type.LEFT ? -1 : 1);
                    other = player.getWorld().getBlockAt(block.getX() + dx, block.getY(), block.getZ());
                }

                ref = ReinforcementPlugin.getRefMap().get(Reinforcement.getKey(other, dimension));
            }
        }

        return ref;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            boolean mode = command.getReinforcementMode(player);

            // Todo: if they're not the owner, cancel it, else allow and return
            if (mode) {
                event.setCancelled(true);
            }

            byte dimension = (byte) player.getWorld().getEnvironment().ordinal();
            Reinforcement ref = getReinforcementAt(block, player, dimension);

            // Info mode
            if (action == Action.LEFT_CLICK_BLOCK) {
                if (mode) {
                    if (ref != null) {
                        player.sendMessage(ChatColor.GOLD + "" + ref.getStrength() + " hits left.");
                    } else {
                        player.sendMessage(ChatColor.RED + "This block is not reinforced.");
                    }
                }
                // Reinforce mode
            } else if (action == Action.RIGHT_CLICK_BLOCK) {
                if (mode) {
                    // Try to reinforce the block using their held item
                    ItemStack held = player.getInventory().getItemInMainHand();
                    if (held != null) {
                        attemptReinforce(ref, block, player, held);
                    }
                } else { // Interacting with the block, try to see if it's locked
                    if (ref != null && !authenticateUser(player, ref.getGroupID())) {
                        onLocked(event, player, block);
                    }
                }
            }
        }
    }

    private void onLocked(PlayerInteractEvent event, Player player, Block block) {
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + block.getType().toString() + " is locked.");
    }

    private void reinforce(Block block, short strength, short groupID, byte material, byte dimension) {
        Reinforcement newRef = new Reinforcement(block.getLocation(), strength, groupID, material, dimension); // wip:
        ReinforcementPlugin.getRefMap().put(newRef.getKey(), newRef);
    }

    // Reinforce a (single) block
    private void attemptReinforce(Reinforcement actualReinforcement, Block block, Player player, ItemStack held) {
        Material material = held.getType();
        MaterialData data = matMap.getOrDefault(material, null);
        if (data != null) {
            // Reinforcement doesn't exist or (it's weak and they own it)
            if (actualReinforcement == null || (actualReinforcement.getStrength() < data.strength && authenticateUser(player, actualReinforcement.getGroupID()))) {
                short groupID = getGroupID(player);
                if (groupID < 0) { // User is not in a group
                    groupID = getNewID(); // Max ID + 1 of existing ids
                    ReinforcementPlugin.getGroupMap().put(groupID, new PlayerGroup(groupID, (byte) 0, player.getDisplayName()));
                }
                
                // Reinforce the single block
                reinforce(block, data.strength, (short)groupID, data.id, (byte) player.getWorld().getEnvironment().ordinal());

                // Use up the reinforcement material
                held.setAmount(Math.max(0, held.getAmount() - 1));
                player.getInventory().setItemInMainHand(held);

                player.sendMessage(ChatColor.GREEN + "Block reinforced.");
            } else if (actualReinforcement != null && actualReinforcement.getStrength() == data.strength) {
                player.sendMessage(ChatColor.AQUA + "Block already fully reinforced!");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        command.disableReinforcementMode(event.getPlayer()); // Reset their "reinforce" state
    }
}
