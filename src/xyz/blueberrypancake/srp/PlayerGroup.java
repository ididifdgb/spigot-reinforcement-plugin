package xyz.blueberrypancake.srp;

public class PlayerGroup implements Keyable {
    
    private short id;
    private byte owner;
    private String username;
    
    PlayerGroup() { }
    
    PlayerGroup(short id, byte owner, String username) {
        this.id = id;
        this.owner = owner;
        this.username = username;
    }
    
    PlayerGroup(short id, String username) {
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
    
    public boolean isOwner() {
        return this.owner == 0;
    }
    
    public byte getOwner() {
        return this.owner;
    }
    
    @Override
    public String getKey() {
        return this.toString();
    }
}
