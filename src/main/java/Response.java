import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

public class Response {
  private int statusCode = 200;
  private String protocol = "HTTP/1.1";
  private Map<String, String> header = new HashMap<>();
  private Map<Integer, String> statusMessage = new HashMap<>();
  private OutputStream outputStream;
  private List<String> supportedCompressions = List.of("gzip");

  public Response(OutputStream outputStream) {
    this.outputStream = outputStream;
    header.put("Content-Type", "text/plain");
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

  public void empty() {
    text("");
  }

  public void text(String body) {
    setContentType("text/plain");
    send(body);
  }

  public void html(String body) {
    setContentType("text/html");
    send(body);
  }

  public void json(String body) {
    setContentType("application/json");
    send(body);
  }

  public void file(String body) {
    setContentType("application/octet-stream");
    send(body);
  }

  private void send(String body) {
    PrintWriter writer = new PrintWriter(outputStream);
    byte[] compressed;
    try {
      compressed = getBody(body);
    } catch (IOException e) {
      compressed = body.getBytes();
    }
    String message = formatHeader(compressed.length);
    writer.print(message);
    writer.print(compressed);
    writer.close();
    System.out.println(message);
  }

  private String formatHeader(int bodySize) {
    StringBuilder sb = new StringBuilder();
    sb.append(protocol).append(" ").append(statusCode).append(" ").append(statusMessage.get(statusCode)).append("\r\n");
    header.put("Content-Length", Integer.toString(bodySize));
    for (Entry<String, String> entry : header.entrySet()) {
      sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
    }
    sb.append("\r\n");
    return sb.toString();
  }

  private byte[] getBody(String body) throws IOException {
    if (getCompression().equalsIgnoreCase("gzip")) {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      try (GZIPOutputStream gzip = new GZIPOutputStream(bytes)) {
        gzip.write(body.getBytes());
      }
      return bytes.toByteArray();
    }
    return body.getBytes();
  }

  private void setContentType(String contentType) {
    header.put("Content-Type", contentType);
  }

  private String getCompression() {
    return header.getOrDefault("Content-Encoding", "");
  }

  public void setCompression(List<String> availableCompressions) {
    for (String compression : availableCompressions) {
      if (supportedCompressions.contains(compression.trim().toLowerCase())) {
        header.put("Content-Encoding", compression);
        return;
      }
    }
  }
}
