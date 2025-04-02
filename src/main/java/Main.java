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

      int statusCode = 404;
      String body = "";
      if (requestTargets.equals("/")) {
        statusCode = 200;
      } else if (requestTargets.startsWith("/echo/")) {
        statusCode = 200;
        body = requestTargets.substring("/echo/".length());
      }
      String response = formatResponse(statusCode, body);

      writer.print(response);
      writer.flush();
      System.out.print("Responded With:\n" + response);
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  static String formatResponse(int statusCode, String body) {
    StringBuilder sb = new StringBuilder();
    String headerLine = "HTTP/1.1 200 OK";
    if (statusCode == 404)
      headerLine = "HTTP/1.1 404 Not Found";
    sb.append(headerLine).append("\r\n");
    String[] header = { "Content-Type: text/plain", "Content-Length: " + body.length() };
    for (String line : header)
      sb.append(line).append("\r\n");
    sb.append("\r\n");
    sb.append(body);
    return sb.toString();
  }
}
