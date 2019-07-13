package reinforcements.srp.types;

import reinforcements.interfaces.Keyable;

public class PlayerGroup implements Keyable<Short> {
    
    private short id;
    private byte owner;
    private String username;
    
    PlayerGroup() { }
    
    public PlayerGroup(short id, byte owner, String username) {
        this.id = id;
        this.owner = owner;
        this.username = username;
    }
    
    public PlayerGroup(short id, String username) {
        this(id, (byte) 1, username); // assume user is not the owner
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String toString() {
        return id + "," + owner + "," + username;
    }
    
    public short getID() {
        return this.id;
    }
    
    public static String getKey(short newID) {
        return newID + "";
    }
    
    public boolean isOwner() {
        return this.owner == (byte) 0;
    }
    
    public byte getOwner() {
        return this.owner;
    }
    
    @Override
    public Short getKey() {
        return id;
    }
}
