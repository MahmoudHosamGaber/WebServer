import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--directory")) {
        FileHandler.setRootPath(args[i + 1]);
        System.out.println("File Path:" + args[i + 1]);
      }
    }
    try (ServerSocket serverSocket = new ServerSocket(4221)) {
      serverSocket.setReuseAddress(true);
      setRoutes();
      int avaiableProcessors = Runtime.getRuntime().availableProcessors();
      ExecutorService executorService = Executors.newFixedThreadPool(avaiableProcessors);
      while (true) {
        Socket socket = serverSocket.accept();
        System.out.println("accepted new connection");
        RequestHandler requestHandler = new RequestHandler(socket);
        executorService.execute(requestHandler);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static void setRoutes() {
    Router.get("/", (req, res) -> {
      res.empty();
    });
    Router.get("/echo/:text", (req, res) -> {
      res.text(req.getParams("text"));
    });
    Router.get("/user-agent", (req, res) -> {
      res.text(req.getUserAgent());
    });
    Router.get("/files/:fileName", (req, res) -> {
      String fileName = req.getParams("fileName");
      String fileContent = FileHandler.readAll(fileName);
      if (fileContent == null) {
        res.setStatusCode(404);
        res.empty();
        return;
      }
      res.file(fileContent);
    });
    Router.post("/files/:fileName", (req, res) -> {
      String fileName = req.getParams("fileName");
      FileHandler.writeFile(fileName, req.getBody());
      res.setStatusCode(201);
      res.empty();
    });
    Router.get("*", (req, res) -> {
      res.setStatusCode(404);
      res.html("<h1>Cannot GET /" + req.getParams("wildcard") + "</h1>");
    });
  }
}
