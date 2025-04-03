import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Response {
  int statusCode = 200;
  String protocol = "HTTP/1.1";
  Map<String, String> header = new HashMap<>();
  Map<Integer, String> statusMessage = new HashMap<>();
  String body;

  public Response() {
    initStatusMessage();
  }

  private void initStatusMessage() {
    statusMessage.put(200, "OK");
    statusMessage.put(201, "Created");
    statusMessage.put(404, "Not Found");
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

  @Override
  public String toString() {
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

}
