package co.spraybot;

import java.io.*;
import java.net.*;
import java.util.List;

public class InfoClient {
    public static void main(String[] args) {
        String message = "giveFileMetadata!";
        DatagramSocket dataSocket = null;

        try (DatagramSocket getInfoSocket = new DatagramSocket(Const.SERVER_PORT)) {
            byte bufferToSend[] = message.getBytes();
            DatagramPacket packetToSend = new DatagramPacket(
                    bufferToSend,
                    bufferToSend.length,
                    InetAddress.getByName(Const.serverIP),
                    Const.SERVER_PORT
            );
            getInfoSocket.send(packetToSend);


            byte receivingBuffer[] = new byte[getInfoSocket.getReceiveBufferSize()];
            DatagramPacket recievedPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length);
            getInfoSocket.receive(recievedPacket);

            byte receivedData[] = recievedPacket.getData();
            String receivedMessage = new String(receivedData).trim();

            String filename = receivedMessage.split("\n")[0];
            long filesize = Long.parseLong(receivedMessage.split("\n")[1]);
            System.out.println(filename);
            System.out.println(filesize);

            File file = new File(filename);
            RandomAccessFile raf = new RandomAccessFile(file, "w");

            Interval intervalsIHave = Interval.empty(0, filesize);

            dataSocket = new DatagramSocket(Const.CLIENT_PORT);
            dataSocket.setSoTimeout(200);

            while (true) {
                byte[] receivedFileChunkPacketByte = new byte[dataSocket.getReceiveBufferSize()];
                DatagramPacket receivedFileChunkPacket = new DatagramPacket(receivedFileChunkPacketByte, receivedFileChunkPacketByte.length);

                //TODO: dokonc doma

                try {
                    dataSocket.receive(receivedFileChunkPacket);
                } catch (SocketTimeoutException e) {
                    List<Interval> intervalsToRequest = intervalsIHave.getEmptySubintervals(42);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(4 + 8 * intervalsToRequest.size() * 2);
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    baos.write(intervalsToRequest.size());
                    for (int i = 0; i < intervalsToRequest.size(); i++) {
                        oos.writeLong(intervalsToRequest.get(i).getMin());
                        oos.writeLong(intervalsToRequest.get(i).getMax());
                    }
                    oos.flush();

                    DatagramPacket requestChunks = new DatagramPacket(baos.toByteArray(), baos.size(), InetAddress.getByName(Const.serverIP), Const.SERVER_REQUEST_PORT);
                    getInfoSocket.send(requestChunks);
                }


            }

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (dataSocket != null) {
                dataSocket.close();
            }
        }
    }
}
