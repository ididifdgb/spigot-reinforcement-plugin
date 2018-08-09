package xyz.blueberrypancake.srp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class SRPFile {

    private byte[] bytes = null;

    private String filename;
    private File file;

    SRPFile(String dimension) {
        this.filename = dimension + ".srp";
    }

    private boolean createFile() throws IOException {
        file = new File(filename);
        file.createNewFile();

        if (file.exists()) {
            if (!file.setWritable(true)) {
                System.out.println("Could not create writable reinforcement file " + filename + "!");
                return false;
            }

            if (!file.canWrite() || !file.canWrite()) {
                System.out.println("Do not have appropriate rw permissions for " + filename + "!");
                return false;
            }
        }
        return true;
    }

    // Read the .srp file from disk and if it doesn't exist, create it, then read into the byte array
    public boolean readFromDisk() throws IOException {
        if (!createFile()) {
            System.out.println("Problem creating file");
            return false;
        }

        // Try to read into the byte array
        try {
            readBytes();
        } catch (IOException e) {
            System.out.println("Error reading into byte array!");
            return false;
        }

        return true;
    }

    private void readBytes() throws IOException {
        FileInputStream is = new FileInputStream(file);
        this.bytes = new byte[(int) file.length()];
        is.read(bytes);
        is.close();
    }

    private void writeBytes() throws IOException {
        if (file != null && file.exists()) {
            file.delete();
            createFile();
        }

        FileOutputStream io = new FileOutputStream(file);
        io.write(this.bytes);
        io.close();
    }

    // Get the reinforcements associated with this byte array as a HashMap
    public HashMap<String, Reinforcement> getReinforcements() {
        HashMap<String, Reinforcement> reinforcements = new HashMap<String, Reinforcement>();

        if (this.bytes.length < Reinforcement.STRUCT_SIZE) {
            return reinforcements;
        }

        ByteBuffer buffer = ByteBuffer.allocate(this.bytes.length);
        buffer.put(bytes, 0, this.bytes.length);
        buffer.flip(); // reset buffer index after copying over the byte array

        // Could use reflection to get the exact size of the fields (?)
        for (int i = 0; i < this.bytes.length / Reinforcement.STRUCT_SIZE; i++) {
            // Add a reinforcement from the buffer
            Reinforcement r = new Reinforcement(buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getShort(), buffer.getShort(), buffer.getShort());
            reinforcements.put(Reinforcement.getKey(r.getX(), r.getY(), r.getZ(), r.getDimension()), r);
        }

        return reinforcements;
    }

    // Dump the reinforcements array (in memory) to bytes and write to disk
    public boolean dumpReinforcements(ArrayList<Reinforcement> reinforcements) {
        ByteBuffer buffer = ByteBuffer.allocate(Reinforcement.STRUCT_SIZE * reinforcements.size());
        
        for (Reinforcement r : reinforcements) {
            buffer.put(r.toBytes());
        }

        this.bytes = buffer.array();

        try {
            this.writeBytes();
        } catch (IOException e) {
            System.out.println("Error writing reinforcement data to disk! Length:" + reinforcements.size());
            return false;
        }
        
        return true;
    }

}
