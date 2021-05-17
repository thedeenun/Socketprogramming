//G02
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Base64;
import java.util.Scanner;

public class AuthorizeServer {
    private DatagramSocket socketServer;
    private byte[] bufRec = new byte[256];
    private byte[] bufSend;
    private DatagramPacket packet;

    private int serverPort;
    private String secretKey;
    private String[] user_pass_action;
    private String token;
    private String action;
    private String decodeMessage;
    private boolean success = false;

    public void readServerFile(){
        try {
            File myObj = new File("server.config");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] data1 = data.split("=");
                if (data1[0].equals("authorize_server_port")){
                    serverPort = Integer.parseInt(data1[1]);
                }
                if (data1[0].equals("secret_key")){
                    secretKey = data1[1];
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void readUser_pass_actionFile(){
        user_pass_action = new String[4];
        int i = 0;

        try {
            File myObj = new File("user_pass_action.csv");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                user_pass_action[i] = data;
                i++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void checkMessage(){
        decode(token);
        String[] checkMsg = decodeMessage.split("\\.");
        for(int i = 0 ; i < user_pass_action.length ; i++){
            String[] msgCheck = user_pass_action[i].split(",");
            String[] msgCheck2 = msgCheck[2].split(":");
            if(checkMsg[0].equals(msgCheck[0]) && checkMsg[1].equals(msgCheck[1])) {
                if(action.equals(msgCheck2[0]) || action.equals(msgCheck2[1])){
                    success = true;
                    break;
                }
            }else {
                success = false;
            }
        }
    }

    public void decode(String token){
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        decodeMessage = new String(decodedBytes);
    }

    public void runServer() throws IOException {
        readServerFile();
        readUser_pass_actionFile();
        socketServer = new DatagramSocket(serverPort);
        System.out.println("Server Start");

        while(true) {
            packet = new DatagramPacket(bufRec, bufRec.length);
            socketServer.receive(packet);
            String messageFromDataServer = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Connected");

            String[] msgSplit = messageFromDataServer.split(":");
            token = msgSplit[0];
            action = msgSplit[1];
            checkMessage();

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            String messageToDataServer = action+":"+success;
            bufSend = messageToDataServer.getBytes();
            packet = new DatagramPacket(bufSend, bufSend.length, address, port);
            socketServer.send(packet);

        }
    }

    public static void main(String[] args) throws IOException {
        AuthorizeServer udpServer = new AuthorizeServer();
        udpServer.runServer();
    }
}
