package xyz.blueberrypancake.srp;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Chest;

public class ReinforcementListener implements Listener {

    private HashMap<Location, Reinforcement> rmap;
    private ReinforcementCommand command;

    private HashMap<Material, Short> mmap;

    ReinforcementListener(HashMap<Location, Reinforcement> rmap, ReinforcementCommand command) {
        this.rmap = rmap;
        this.mmap = new HashMap<Material, Short>();
        this.command = command;

        this.initMaterialMap();
    }

    private void initMaterialMap() {
        mmap.put(Material.STONE, (short) 250);
        mmap.put(Material.IRON_INGOT, (short) 250);
        mmap.put(Material.DIAMOND, (short) 1800);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = new Location(null, block.getX(), block.getY(), block.getZ());
        Player player = event.getPlayer();
        Reinforcement r = rmap.get(loc);

        boolean mode = command.getReinforcementMode(player);

        if (mode) {
            player.sendMessage(ChatColor.AQUA + "Disabled reinforcement mode.");
            command.disableReinforcementMode(player);
            event.setCancelled(true);
            return;
        }

        if (r != null) {
            r.decreaseStrength();
            if (r.getStrength() > 0) {
                event.setCancelled(true);
                if (player != null) {
                    player.sendMessage(ChatColor.RED + block.getType().toString() + " is locked.");
                }
            } else {
                rmap.remove(loc);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            boolean mode = command.getReinforcementMode(player);

            if (mode) {
                event.setCancelled(true);
            }

            Location loc = new Location(null, block.getX(), block.getY(), block.getZ());
            if (action == Action.LEFT_CLICK_BLOCK) {
                Reinforcement r = rmap.get(loc);
                if (mode) {
                    if (r != null) {
                        player.sendMessage(ChatColor.GOLD + "" + r.getStrength() + " hits left.");
                    } else {
                        player.sendMessage(ChatColor.RED + "This block is not reinforced.");
                    }
                }
            } else if (action == Action.RIGHT_CLICK_BLOCK) {
                if (mode) {
                    // try to reinforce the block
                    ItemStack held = player.getInventory().getItemInMainHand();
                    if (held != null) {
                        reinforce(loc, player, held);
                    }
                } else {
                    Reinforcement r = rmap.get(loc);

                    if (r != null) {
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

    private void reinforce(Location loc, Player player, ItemStack held) {
        Material m = held.getType();
        short strength = mmap.getOrDefault(m, (short) -1);
        if (strength > -1) {
            Reinforcement actualReinforcement = rmap.get(loc);

            if (actualReinforcement == null || actualReinforcement.getStrength() < strength) {
                Reinforcement newReinforcement = new Reinforcement(loc, strength, (short) 1);
                rmap.put(new Location(null, newReinforcement.getX(), newReinforcement.getY(), newReinforcement.getZ()),
                        newReinforcement);
                held.setAmount(Math.max(0, held.getAmount() - 1));
                player.getInventory().setItemInMainHand(held);
                player.sendMessage(ChatColor.GREEN + "Block reinforced.");
            } else if (actualReinforcement != null && actualReinforcement.getStrength() == strength) {
                player.sendMessage(ChatColor.AQUA + "Block already fully reinforced!");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        command.disableReinforcementMode(event.getPlayer()); // Reset their "reinforce" state
    }
}
