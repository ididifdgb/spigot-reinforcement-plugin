package xyz.blueberrypancake.srp;

import java.nio.ByteBuffer;

public abstract class ByteBufferable<T extends Keyable> {

    protected T instance;
    
    ByteBufferable() { }
    
    ByteBufferable(T instance) {
        this.instance = instance;
    }
    
    abstract T fromBuffer(ByteBuffer buffer);
    abstract byte[] toBytes();
}
