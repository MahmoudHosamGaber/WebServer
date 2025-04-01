import java.io.IOException;
import java.io.PrintWriter;
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
      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
      String response = "HTTP/1.1 200 OK\r\n\r\n";
      writer.println(response);
      System.out.println("Response 200 sent");
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
