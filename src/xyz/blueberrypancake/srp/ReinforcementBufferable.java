package xyz.blueberrypancake.srp;

import java.nio.ByteBuffer;

public class ReinforcementBufferable extends ByteBufferable<Reinforcement, String> {

    @Override
    public Reinforcement fromBuffer(ByteBuffer buffer) {
        return new Reinforcement(buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getShort(), buffer.getShort(), buffer.get(), buffer.get());
    }
    
    @Override
    public byte[] toBytes() {
        ByteBuffer bytes = ByteBuffer.allocate(18);
        bytes.putInt(instance.getX()); // 4 bytes
        bytes.putInt(instance.getY()); // 4 bytes
        bytes.putInt(instance.getZ()); // 4 bytes
        bytes.putShort(instance.getStrength()); // 2 bytes
        bytes.putShort(instance.getGroupID()); // 2 bytes
        bytes.put(instance.getMaterial()); // 1 byte
        bytes.put(instance.getDimension()); // 1 byte
        return bytes.array();
    }
}
