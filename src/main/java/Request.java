import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
  private String body;

  private Map<String, String> header;
  private Map<String, String> params;

  public Request(InputStream inputStream) throws IOException {
    header = new HashMap<>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    initHeader(reader);
    initBody(reader);
    initParams();
  }

  private void initHeader(BufferedReader reader) throws IOException {
    List<String> lines = new ArrayList<>();
    do {
      lines.add(reader.readLine());
    } while (!lines.getLast().isEmpty());
    String[] startLine = lines.getFirst().split(" ");
    header.put("method", startLine[0]);
    header.put("path", startLine[1]);
    header.put("protocol", startLine[2]);
    for (int i = 1; i < lines.size() - 1; i++) {
      String line = lines.get(i);
      int index = line.indexOf(":");
      String key = line.substring(0, index);
      String value = line.substring(index + 1, line.length());
      header.put(key.trim(), value.trim());
    }
    System.out.println("Request Header:\n" + lines.toString());
  }

  private void initBody(BufferedReader reader) throws IOException {
    int requestBodySize = getContentLength();
    char[] requestBodyBytes = new char[requestBodySize];
    reader.read(requestBodyBytes);
    body = new String(requestBodyBytes);
    System.out.println("Request Body:\n" + body);
  }

  private void initParams() {
    params = Router.getParams(getMethod(), getPath());
  }

  private int getContentLength() {
    return Integer.parseInt(header.getOrDefault("Content-Length", "0"));
  }

  public String getBody() {
    return body;
  }

  public String getMethod() {
    return header.get("method");
  }

  public String getPath() {
    return header.get("path");
  }

  public String getProtocol() {
    return header.get("protocol");
  }

  public String getHostname() {
    return header.get("Host");
  }

  public String getUserAgent() {
    return header.get("User-Agent");
  }

  public String getParams(String param) {
    return params.get(param);
  }

  public String getSupportedCompression() {
    return header.getOrDefault("Accept-Encoding", "");
  }
}
