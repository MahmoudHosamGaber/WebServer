import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    System.out.println("Logs from your program will appear here!");
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--directory")) {
        FileHandler.setRootPath(args[i + 1]);
        System.out.println("File Path:" + args[i + 1]);
      }
    }
    try {
      ServerSocket serverSocket = new ServerSocket(4221);
      serverSocket.setReuseAddress(true);
      setRoutes();
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

  private static void setRoutes() {
    Router.get("/", (req, res) -> {
      res.send();
    });
    Router.get("/echo/:text", (req, res) -> {
      res.setBody(req.getParams("text"));
      res.send();
    });
    Router.get("/user-agent", (req, res) -> {
      res.setBody(req.getUserAgent());
      res.send();
    });
    Router.get("/files/:fileName", (req, res) -> {
      String fileName = req.getParams("fileName");
      String fileContent = FileHandler.readAll(fileName);
      if (fileContent == null) {
        res.setStatusCode(404);
        res.send();
        return;
      }
      res.setContentType("application/octet-stream");
      res.setBody(fileContent);
      res.send();
    });
    Router.post("/files/:fileName", (req, res) -> {
      String fileName = req.getParams("fileName");
      FileHandler.writeFile(fileName, req.getBody());
      res.setStatusCode(201);
      res.send();
    });
  }
}
