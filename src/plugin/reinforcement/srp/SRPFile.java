package plugin.reinforcement.srp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class SRPFile<T extends Keyable<K>, K> extends DataFile {

    private ByteBufferable<T, K> bufferable; // bufferable instance for mapping bytes to an object, and vice-versa
    
    SRPFile(String label, ByteBufferable<T, K> bufferable) {
        super(label, ".srp");
        this.bufferable = bufferable;
    }

    // Get the data associated with this byte array as a HashMap
    public HashMap<K, T> getData() {
        HashMap<K, T> data = new HashMap<K, T>();

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
    
    public boolean dumpData(HashMap<K, T> data) {
        return dumpData(new ArrayList<T>(data.values()));
    }

    // Dump the data array (in memory) to bytes and write to disk
    private boolean dumpData(ArrayList<T> data) {
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
