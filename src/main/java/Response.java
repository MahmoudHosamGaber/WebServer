import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Response {
  private int statusCode = 200;
  private String protocol = "HTTP/1.1";
  private Map<String, String> header = new HashMap<>();
  private Map<Integer, String> statusMessage = new HashMap<>();
  private String body = "";
  private PrintWriter writer;

  public Response(OutputStream outputStream) {
    this.writer = new PrintWriter(outputStream);
    header.put("Content-Type", "text/plain");
    initStatusMessage();
  }

  private void initStatusMessage() {
    statusMessage.put(200, "OK");
    statusMessage.put(201, "Created");
    statusMessage.put(404, "Not Found");
  }

  public void send() {
    String message = formatMessage();
    writer.print(message);
    writer.close();
    System.out.print("Responded With:\n" + formatMessage());
  }

  private String formatMessage() {
    StringBuilder sb = new StringBuilder();
    sb.append(protocol).append(" ").append(statusCode).append(" ").append(statusMessage.get(statusCode)).append("\r\n");
    header.put("Content-Length", Integer.toString(body.length()));
    for (Entry<String, String> entry : header.entrySet()) {
      sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
    }
    sb.append("\r\n");
    sb.append(body);
    return sb.toString();
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public void setContentType(String contentType) {
    header.put("Content-Type", contentType);
  }
}
