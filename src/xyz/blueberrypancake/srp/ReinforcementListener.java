package xyz.blueberrypancake.srp;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

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
        Reinforcement r = rmap.get(loc);
        if (r != null) {
            r.decreaseStrength();
            if (r.getStrength() > 0) {
                event.setCancelled(true);
                Player player = event.getPlayer();
                if (player != null) {
                    player.sendMessage("\u00A76" + r.getStrength() + " hits left.");
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
                if (mode) {
                    Reinforcement r = rmap.get(loc);
                    if (r != null) {
                        player.sendMessage("\u00A76" + r.getStrength() + " hits left.");
                    } else {
                        player.sendMessage("\u00A7cThis block is not reinforced.");
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
                        event.setCancelled(true);
                    }
                }
            }
        }
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
                player.sendMessage("\u00A7aBlock reinforced.");
            } else if (actualReinforcement != null && actualReinforcement.getStrength() == strength) {
                player.sendMessage("\u00A7bBlock already fully reinforced!");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        command.resetPlayerState(event.getPlayer()); // Reset their "reinforce" state
    }
}
