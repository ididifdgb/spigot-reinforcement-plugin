package xyz.blueberrypancake.srp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class SRPFile<T extends Keyable> extends DataFile {

    private ByteBufferable<T> bufferable; // bufferable instance for mapping bytes to an object, and vice-versa
    
    SRPFile(String label, ByteBufferable<T> bufferable) {
        super(label, ".srp");
        this.bufferable = bufferable;
    }

    // Get the data associated with this byte array as a HashMap
    public HashMap<String, T> getData() {
        HashMap<String, T> data = new HashMap<String, T>();

        if (this.bytes.length <= 0) {
            return data;
        }

        ByteBuffer buffer = ByteBuffer.allocate(this.bytes.length);
        buffer.put(bytes, 0, this.bytes.length);
        buffer.flip(); // reset buffer index after copying over the byte array

        while(buffer.remaining() > 0) {
            // Get an object from the instance
            T obj = bufferable.fromBuffer(buffer);
            data.put(obj.getKey(), obj);
        }

        return data;
    }

    // Dump the data array (in memory) to bytes and write to disk
    public boolean dumpData(ArrayList<T> data) {
        int totalLength = 0;
        for (T item : data) {
            bufferable.instance = item;
            totalLength += bufferable.toBytes().length;
        }
        
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        
        for (T item : data) {
            bufferable.instance = item;
            buffer.put(bufferable.toBytes());
        }

        this.bytes = buffer.array();

        try {
            this.writeData();
        } catch (IOException e) {
            System.out.println("Error writing data to disk! Length:" + data.size());
            return false;
        }
        
        return true;
    }

}
