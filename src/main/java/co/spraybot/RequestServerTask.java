package co.spraybot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class RequestServerTask implements Runnable {
    private Interval partsToSend;
    private File fileToSend;
    private RandomAccessFile raf;

    public RequestServerTask(Interval partsToSend, File fileToSend) {
        this.partsToSend = partsToSend;
        this.fileToSend = fileToSend;
        try {
            raf = new RandomAccessFile(fileToSend, "r");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {

    }
}
