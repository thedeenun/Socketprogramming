//G02
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class DataServer {
    private ServerSocket serverSocket;
    private Socket connectionSocket;
    private PrintWriter output;
    private BufferedReader input;
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] bufSend;
    private byte[] bufRec = new byte[256];
    private DatagramPacket packet;

    private int serverPort;
    private int serverPortUDP;
    private String[] datalist;
    private String token;
    private String action;
    private String mapping;
    private String someVar;

    public void readServerFile(){
        try {
            File myObj = new File("server.config");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] data1 = data.split("=");
                if (data1[0].equals("data_server_port")){
                    serverPort = Integer.parseInt(data1[1]);
                }if (data1[0].equals("authorize_server_port")){
                    serverPortUDP = Integer.parseInt(data1[1]);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void readDatalistFile(){
        datalist = new String[5];
        int i = 0;

        try {
            File myObj = new File("data_list.csv");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                datalist[i] = data;
                i++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void checkMapping(String messageFromAuthorizeServer) throws IOException {
            String[] a = messageFromAuthorizeServer.split(":");
            if(a[1].equals("false")){
                output.println("connection closed");
                connectionSocket.close();
            }else{
                if(a[0].equals("nametoip")){
                    for(int i = 0 ; i < datalist.length ; i++){
                        String[] b = datalist[i].split(",");
                        if(mapping.equals(b[0])){
                            someVar = b[1];
                            break;
                        }else{
                            someVar = "not found";
                        }
                    }
                }else if(a[0].equals("iptoname")){
                    for(int i = 0 ; i < datalist.length ; i++){
                        String[] b = datalist[i].split(",");
                        if(mapping.equals(b[1])){
                            someVar = b[0];
                            break;
                        }else{
                            someVar = "not found";
                        }
                    }
                }
            }
    }

    public void runServer() throws IOException {
        readServerFile();
        readDatalistFile();
        serverSocket = new ServerSocket(serverPort,1);
        System.out.println("Server Start");


        while(true) {
            connectionSocket = serverSocket.accept();
            output = new PrintWriter(connectionSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            System.out.println("Connected");
            while (true){
                //get message from Client
                String message = input.readLine();
                String[] msgSplit = message.split(":");
                token = msgSplit[0];
                action = msgSplit[1];
                mapping = msgSplit[2];

                if(action.equals("quit")){
                    //output.println("connection closed");
                    connectionSocket.close();
                    return ;
                }else if(action.equals("iptoname") || action.equals("nametoip")){
                    //connect to AuthorizeServer
                    socket = new DatagramSocket();
                    address = InetAddress.getByName("127.0.0.1");

                    // Send message to AuthorizeServer
                    String messageToServer = token+":"+action; //size = 13
                    bufSend = messageToServer.getBytes();
                    packet = new DatagramPacket(bufSend, bufSend.length, address, serverPortUDP);
                    socket.send(packet);

                    //receive message from AuthorizeServer
                    packet = new DatagramPacket(bufRec, bufRec.length);
                    socket.receive(packet);
                    String messageFromAuthorizeServer = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(messageFromAuthorizeServer);

                    checkMapping(messageFromAuthorizeServer);
                    output.println(someVar);
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        DataServer dataServer = new DataServer();
        dataServer.runServer();
    }
}
