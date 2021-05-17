//G02
import java.io.*;
import java.util.Base64;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthenticationServer {
    private ServerSocket serverSocket;
    private Socket connectionSocket;
    private PrintWriter output;
    private BufferedReader input;

    private int serverPort;
    private String secretKey;
    private String[] user_pass_action;
    private int success = 0;

    public void readServerFile(){
        try {
            File myObj = new File("server.config");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] data1 = data.split("=");
                if (data1[0].equals("authentication_server_port")){
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

    public void checkMessage(String message){
        String[] toCheck = message.split(",");
        for(int i = 0 ; i < user_pass_action.length ; i++){
            String[] check =  user_pass_action[i].split(",");
            if(toCheck[0].equals(check[0]) && toCheck[1].equals(check[1])){
                String msgtoToken = toCheck[0]+"."+toCheck[1]+"."+secretKey;
                encode(msgtoToken);
                success = 1;
                break;
            }
        }
    }

    public void encode(String msgtoToken){
        String tokenToClient = Base64.getEncoder().encodeToString(msgtoToken.getBytes());
        output.println(tokenToClient);
    }

    public void getMessage() throws IOException {

            for(int i = 0 ; i < 3 ; i++){
                String username = input.readLine();
                String password = input.readLine();
                String[] userSplit = username.split(":");
                String[] passSplit = password.split(":");
                String message = userSplit[1]+","+passSplit[1];
                checkMessage(message);

                if(success == 1){
                    break;
                }
                output.println("");
            }if(success == 0){
                //output.println("connection closed");
                connectionSocket.close();
        }
    }

    public void runServer() throws IOException {
        readServerFile();
        readUser_pass_actionFile();
        serverSocket = new ServerSocket(serverPort,1);
        System.out.println("Server Start");

        while(true) {
            connectionSocket = serverSocket.accept();
            output = new PrintWriter(connectionSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

            System.out.println("Connected");

            getMessage();
        }
    }

    public static void main(String[] args) throws IOException {
        AuthenticationServer authenticationServer = new AuthenticationServer();
        authenticationServer.runServer();
    }
}
