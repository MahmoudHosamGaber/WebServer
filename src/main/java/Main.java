import java.util.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    System.out.println("Logs from your program will appear here!");

    try {
      ServerSocket serverSocket = new ServerSocket(4221);
      serverSocket.setReuseAddress(true);
      Socket socket = serverSocket.accept(); // Wait for connection from client.
      System.out.println("accepted new connection");

      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

      String startLine = reader.readLine();
      System.out.println(startLine);
      String[] startLineParts = startLine.split(" ");
      String method = startLineParts[0];
      String requestTargets = startLineParts[1];
      String protocol = startLineParts[2];

      String response = "HTTP/1.1 404 Not Found\r\n\r\n";
      if (requestTargets.equals("/"))
        response = "HTTP/1.1 200 OK\r\n\r\n";
      writer.println(response);
      System.out.print("Responded With:\n" + response);
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
