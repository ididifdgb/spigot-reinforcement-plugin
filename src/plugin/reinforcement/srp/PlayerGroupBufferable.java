package plugin.reinforcement.srp;

import java.nio.ByteBuffer;

public class PlayerGroupBufferable extends ByteBufferable<PlayerGroup, Short> {

    @Override
    public PlayerGroup fromBuffer(ByteBuffer buffer) {
        short id = buffer.getShort();
        byte owner = buffer.get();
        int length = buffer.getInt();
        
        String username = "";
        for(int i = 0; i < length; i++) {
            username += (char) buffer.get();
        }
        return new PlayerGroup(id, owner, username);
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer bytes = ByteBuffer.allocate(7 + instance.getUsername().getBytes().length);
        bytes.putShort(instance.getID()); // 2 bytes
        bytes.put(instance.getOwner()); // 1 byte
        bytes.putInt(instance.getUsername().length()); // 4 bytes
        bytes.put(instance.getUsername().getBytes());
        return bytes.array();
    }
}
