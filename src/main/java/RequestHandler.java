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
      PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
      Response response = new Response(writer);
      response.setStatusCode(statusCode);
      response.setBody(body);
      response.setContentType(contentType);
      response.send();
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
