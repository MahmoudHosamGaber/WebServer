import java.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    System.out.println("Logs from your program will appear here!");
    String filePath = args[1];
    FileReader.setRootPath(filePath);
    System.out.println("File Path:" + filePath);
    try {
      ServerSocket serverSocket = new ServerSocket(4221);
      serverSocket.setReuseAddress(true);
      while (true) {
        Socket socket = serverSocket.accept(); // Wait for connection from client.
        System.out.println("accepted new connection");
        RequestHandler requestHandler = new RequestHandler(socket);
        Thread handler = new Thread(requestHandler);
        handler.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
