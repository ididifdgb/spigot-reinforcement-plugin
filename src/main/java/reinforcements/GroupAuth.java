package reinforcements;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.entity.Player;

import reinforcements.srp.types.PlayerGroup;

public class GroupAuth {

    public static short getNewID() {
        ArrayList<Short> keys = new ArrayList<Short>(ReinforcementPlugin.getGroupMap().keySet());
        if(keys.size() > 0) {
            return (short) (Collections.max(keys) + 1);
        }
        return (short) 0;
    }
    
    // Returns the group ID of the group the Player owns
    public static short getGroupID(Player player) {
        ArrayList<PlayerGroup> groups = new ArrayList<PlayerGroup>(ReinforcementPlugin.getGroupMap().values());
        for(PlayerGroup group : groups) {
            if(group.getUsername().equals(player.getDisplayName()) && group.isOwner()) {
                return group.getID();
            }
        }
        return -1;
    }

    // Authenticate the player (check to see if they're in the group, if not, add them)
    public static boolean authenticateUser(Player player, short groupID) {
        PlayerGroup group = ReinforcementPlugin.getGroupMap().get(groupID);
        return group != null && group.getUsername().equals(player.getDisplayName());
    }
}
