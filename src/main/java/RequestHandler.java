import java.util.*;
import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {
  Socket socket;

  public RequestHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    handleRequest();
  }

  void handleRequest() {
    try {
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
      char[] requestBodyBytes = new char[Integer.parseInt(header.get("Content-Length"))];
      reader.read(requestBodyBytes);
      String requestBody = new String(requestBodyBytes);
      System.out.println("Request Body: " + requestBody);
      int statusCode = 404;
      String body = "";
      String contentType = "text/plain";
      if (header.get("target").equals("/")) {
        statusCode = 200;
      } else if (header.get("target").startsWith("/echo/")) {
        statusCode = 200;
        body = header.get("target").substring("/echo/".length());
      } else if (header.get("target").equals("/user-agent")) {
        statusCode = 200;
        body = header.get("User-Agent");
      } else if (header.get("target").startsWith("/files/")) {
        statusCode = 200;
        contentType = "application/octet-stream";
        String fileName = header.get("target").substring("/files/".length());
        System.out.println("FileName: " + fileName);
        if (header.get("method").equals("GET")) {
          body = FileHandler.readAll(fileName);
          if (body == null) {
            statusCode = 404;
            body = "";
          }
        } else if (header.get("method").equals("POST")) {
          statusCode = 201;
          FileHandler.writeFile(fileName, requestBody);
        }
      }
      String response = formatResponse(statusCode, body, contentType);

      writer.print(response);
      writer.flush();
      System.out.print("Responded With:\n" + response);
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }

  static String formatResponse(int statusCode, String body, String contentType) {
    StringBuilder sb = new StringBuilder();
    String headerLine = "HTTP/1.1 200 OK";
    if (statusCode == 404)
      headerLine = "HTTP/1.1 404 Not Found";
    else if (statusCode == 201)
      headerLine = "HTTP/1.1 201 Created";
    sb.append(headerLine).append("\r\n");
    String[] header = { "Content-Type: " + contentType, "Content-Length: " + body.length() };
    for (String line : header)
      sb.append(line).append("\r\n");
    sb.append("\r\n");
    sb.append(body);
    return sb.toString();
  }
}
