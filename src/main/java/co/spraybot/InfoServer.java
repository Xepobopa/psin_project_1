package co.spraybot;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class InfoServer {
    public static void main(String[] args) {
        File fileToSend = new File(Const.FILE_PATH);
        String fileName = fileToSend.getName();
        long fileSize = fileToSend.length();
        String fileMetadataInfo = fileName + "\n" + fileSize;

        try (DatagramSocket infoSocket = new DatagramSocket(Const.SERVER_PORT)) {
            System.out.println("Start server...");

            while (true) {
                byte receivingBuffer[] = new byte[infoSocket.getReceiveBufferSize()];
                DatagramPacket recievedPacket = new DatagramPacket(receivingBuffer, receivingBuffer.length);
                infoSocket.receive(recievedPacket);

                byte receivedData[] = recievedPacket.getData();
                String receivedMessage = new String(receivedData).trim();
                System.out.println(recievedPacket.getAddress() + " " + receivedMessage);

                if ("giveFileMetadata".equals(receivedMessage)) {
                    byte bufferToSend[] = fileMetadataInfo.getBytes();
                    DatagramPacket packetToSend = new DatagramPacket(
                        bufferToSend,
                        bufferToSend.length,
                        recievedPacket.getAddress(),
                        recievedPacket.getPort()
                    );
                    infoSocket.send(packetToSend);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
