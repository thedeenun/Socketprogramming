import org.w3c.dom.ls.LSOutput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Test {
    private Socket authenticationSocket;
    private int authenticationPort = 8080;
    private String serverIp = "127.0.0.1";
    private PrintWriter authenticationOutput;
    private BufferedReader authenticationInput;

    private Socket dataSocket;
    private int dataPort = 8888;
    private PrintWriter dataOutput;
    private BufferedReader dataInput;

    private void runTestOne() throws IOException {
        int x = 0;
      while (x<3) {
          authenticationSocket = new Socket(serverIp, authenticationPort);
          authenticationOutput = new PrintWriter(authenticationSocket.getOutputStream(), true);
          authenticationInput = new BufferedReader(new InputStreamReader(authenticationSocket.getInputStream()));

          authenticationOutput.println("USER:gut");
          authenticationOutput.println("PASS:333333");

          String token = authenticationInput.readLine();
          System.out.println(token);
          System.out.println(x);
          x++;
      }
//
//        dataSocket = new Socket(serverIp, dataPort);
//        dataOutput = new PrintWriter(dataSocket.getOutputStream(), true);
//        dataInput = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
//
//        String result;
//
//        dataOutput.println(token + ":nametoip:bigdata");
//        result = dataInput.readLine();
//        System.out.println(result);
//
//        dataOutput.println(token + ":nametoip:bigdata");
//        result = dataInput.readLine();
//        System.out.println(result);
//
//        dataOutput.println(token + ":quit:bigdata");
//
//        System.out.println("quit");
//        result = dataInput.readLine();
//        System.out.println(result);
    }

    public static void main(String[] args) throws IOException {
        new Test().runTestOne();
    }
}