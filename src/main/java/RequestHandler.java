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
      Request request = new Request(socket.getInputStream());
      int statusCode = 404;
      String body = "";
      String contentType = "text/plain";
      if (request.getPath().equals("/")) {
        statusCode = 200;
      } else if (request.getPath().startsWith("/echo/")) {
        statusCode = 200;
        body = request.getPath().substring("/echo/".length());
      } else if (request.getPath().equals("/user-agent")) {
        statusCode = 200;
        body = request.getUserAgent();
      } else if (request.getPath().startsWith("/files/")) {
        statusCode = 200;
        contentType = "application/octet-stream";
        String fileName = request.getPath().substring("/files/".length());
        System.out.println("FileName: " + fileName);
        if (request.getMethod().equals("GET")) {
          body = FileHandler.readAll(fileName);
          if (body == null) {
            statusCode = 404;
            body = "";
          }
        } else if (request.getMethod().equals("POST")) {
          statusCode = 201;
          FileHandler.writeFile(fileName, request.getBody());
        }
      }
      Response response = new Response();
      response.setStatusCode(statusCode);
      response.setBody(body);
      response.setContentType(contentType);

      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
      writer.print(response.toString());
      writer.flush();
      System.out.print("Responded With:\n" + response.toString());
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
