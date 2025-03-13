package co.spraybot;

import java.io.IOException;
import java.net.*;

public class InfoClient {
    public static void main(String[] args) {
        String message = "giveFileMetadata!";

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

            String getMassage = receivedMessage.split("\n")[0];
            long getSize = Long.parseLong(receivedMessage.split("\n")[1]);
            System.out.println(getMassage);
            System.out.println(getSize);

        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
