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
      HashMap<String, String> header = new HashMap<>();

      String line = reader.readLine();
      System.out.println(line);
      String[] startLineParts = line.split(" ");
      header.put("method", startLineParts[0]);
      header.put("protocol", startLineParts[2]);
      header.put("target", startLineParts[1]);
      line = reader.readLine();
      while (!line.isEmpty()) {
        int index = line.indexOf(":");
        String key = line.substring(0, index);
        String value = line.substring(index + 1, line.length());
        header.put(key.trim(), value.trim());
        line = reader.readLine();
      }
      int statusCode = 404;
      String body = "";
      if (header.get("target").equals("/")) {
        statusCode = 200;
      } else if (header.get("target").startsWith("/echo/")) {
        statusCode = 200;
        body = header.get("target").substring("/echo/".length());
      } else if (header.get("target").equals("/user-agent")) {
        statusCode = 200;
        body = header.get("User-Agent");
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
