package xyz.blueberrypancake.srp;

import java.util.HashMap;

import org.bukkit.ChatColor;
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

    private HashMap<Integer, Reinforcement> refMap;
    private HashMap<Material, Short> matMap;
    
    private ReinforcementCommand command;

    ReinforcementListener(HashMap<Integer, Reinforcement> refMap, ReinforcementCommand command) {
        this.refMap = refMap;
        this.matMap = new HashMap<Material, Short>();
        this.command = command;

        this.initMaterialMap();
    }

    private void initMaterialMap() {
        matMap.put(Material.STONE, (short) 250);
        matMap.put(Material.IRON_INGOT, (short) 750);
        matMap.put(Material.DIAMOND, (short) 1800);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Reinforcement lookup = new Reinforcement(block.getX(), block.getY(), block.getZ(), (short) player.getWorld().getEnvironment().ordinal());
        Reinforcement ref = refMap.get(lookup.hashCode());

        boolean mode = command.getReinforcementMode(player);

        if (mode) {
            player.sendMessage(ChatColor.AQUA + "Disabled reinforcement mode.");
            command.disableReinforcementMode(player);
            event.setCancelled(true);
            return;
        }

        if (ref != null) {
            ref.decreaseStrength();
            if (ref.getStrength() > 0) {
                event.setCancelled(true);
                if (player != null) {
                    player.sendMessage(ChatColor.RED + block.getType().toString() + " is locked.");
                }
            } else {
                refMap.remove(lookup.hashCode());
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

            Reinforcement lookup = new Reinforcement(block.getX(), block.getY(), block.getZ(), (short) player.getWorld().getEnvironment().ordinal());
            Reinforcement ref = refMap.get(lookup.hashCode());
            
            if (action == Action.LEFT_CLICK_BLOCK) {
                if (mode) {
                    if (ref != null) {
                        player.sendMessage(ChatColor.GOLD + "" + ref.getStrength() + " hits left.");
                    } else {
                        player.sendMessage(ChatColor.RED + "This block is not reinforced.");
                    }
                }
            } else if (action == Action.RIGHT_CLICK_BLOCK) {
                if (mode) {
                    // try to reinforce the block
                    ItemStack held = player.getInventory().getItemInMainHand();
                    if (held != null) {
                        attemptReinforce(ref, block.getLocation(), player, held);
                    }
                } else {
                    if (ref != null) {
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

    private void attemptReinforce(Reinforcement actualReinforcement, Location loc, Player player, ItemStack held) {
        Material m = held.getType();
        short strength = matMap.getOrDefault(m, (short) -1);
        if (strength > -1) {
            if (actualReinforcement == null || actualReinforcement.getStrength() < strength) {
                Reinforcement newRef = new Reinforcement(loc, strength, (short) 1, (short) player.getWorld().getEnvironment().ordinal()); // wip: actual group ids
                refMap.put(newRef.hashCode(), newRef);
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
