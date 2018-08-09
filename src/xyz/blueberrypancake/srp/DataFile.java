package xyz.blueberrypancake.srp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

abstract class DataFile {

    private String filename;
    protected File file;
    
    protected byte[] bytes = null;

    DataFile(String dimension, String extension) {
        this.filename = dimension + extension;
    }

    protected boolean createFile() throws IOException {
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

    // Read the file from disk and if it doesn't exist, create it, then read
    public boolean readFromDisk() throws IOException {
        if (!createFile()) {
            System.out.println("Problem creating " + filename);
            return false;
        }

        // Try to read
        try {
            readData();
        } catch (IOException e) {
            System.out.println("Error reading data from " + filename + "!");
            return false;
        }

        return true;
    }


    protected void readData() throws IOException {
        FileInputStream is = new FileInputStream(file);
        this.bytes = new byte[(int) file.length()];
        is.read(bytes);
        is.close();
    }

    protected void writeData() throws IOException {
        if (file != null && file.exists()) {
            file.delete();
            createFile();
        }

        FileOutputStream io = new FileOutputStream(file);
        io.write(this.bytes);
        io.close();
    }
}
