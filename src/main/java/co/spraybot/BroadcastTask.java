package co.spraybot;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class BroadcastTask implements Runnable {
    private Interval partsToSend;
    private File fileToSend;
    private RandomAccessFile raf;

    public BroadcastTask(Interval partsToSend, File fileToSend) {
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
        try {
            Interval intervalChunk = partsToSend.getAndEraseNextFullSubintervalBlocked(Const.CHUNK_SIZE);

            raf.seek(intervalChunk.getMin());
            byte byteDataChunk[] = new byte[(int)intervalChunk.length()];
            int reallyReaded = raf.read(byteDataChunk, 0, (int)intervalChunk.length());

            ByteArrayOutputStream baos = new ByteArrayOutputStream((int)(8 + 4 + intervalChunk.length()));
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeLong(intervalChunk.getMin());
            oos.writeLong((int)intervalChunk.length());
            oos.write(byteDataChunk);
            oos.flush();

            DatagramPacket packetDataToSend = new DatagramPacket(baos.toByteArray(), baos.size(), InetAddress.getByName("255.255.255.255"), Const.CLIENT_PORT);


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
