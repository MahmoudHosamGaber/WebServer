import java.io.*;
import java.net.Socket;
import java.util.function.BiConsumer;

public class RequestHandler implements Runnable {
  Socket socket;

  public RequestHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try {
      Request request = new Request(socket.getInputStream());
      Response response = new Response(socket.getOutputStream());
      BiConsumer<Request, Response> function = Router.getFunction(request.getMethod(), request.getPath());
      function.accept(request, response);
    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
