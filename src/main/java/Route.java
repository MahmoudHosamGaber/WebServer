import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Route {
  String method;
  Pattern regex;
  List<String> params = new ArrayList<>();

  public Route(String method, String path) {
    this.method = method;
    this.regex = Pattern.compile(pathToRegex(path));
  }

  private String pathToRegex(String path) {
    String[] parts = path.split("/");
    if (parts.length == 0)
      return "^/$";
    StringBuilder regexBuilder = new StringBuilder("^");
    for (String part : parts) {
      if (part.isEmpty())
        continue;
      regexBuilder.append("/");
      if (part.startsWith(":")) {
        String param = part.substring(1);
        params.add(param);
        regexBuilder.append("([^/]+)");
      } else if (part.equals("*")) {
        params.add("wildcard");
        regexBuilder.append("(.*)");
      } else {
        regexBuilder.append(Pattern.quote(part));
      }
    }
    regexBuilder.append("$");
    return regexBuilder.toString();
  }

  public boolean doesMatch(String method, String path) {
    if (!this.method.equals(method))
      return false;
    Matcher matcher = regex.matcher(path);
    return matcher.matches();
  }

  public Map<String, String> getParams(String path) {
    System.out.println("Path Regex: " + regex.toString());
    System.out.println("Path Params: " + params.toString());
    Map<String, String> paramMap = new HashMap<>();
    Matcher matcher = regex.matcher(path);
    matcher.matches();
    for (int i = 0; i < params.size(); i++) {
      paramMap.put(params.get(i), matcher.group(i + 1));
    }
    return paramMap;
  }
}
