import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.ArrayList;
import java.util.HashMap;

public class Router {
  private static List<Route> routes = new ArrayList<>();
  private static List<BiConsumer<Request, Response>> functions = new ArrayList<>();
  private static BiConsumer<Request, Response> defaultFunction = (req, res) -> {
    res.setStatusCode(404);
    res.empty();
  };

  public static void get(String path, BiConsumer<Request, Response> function) {
    addRoute("GET", path, function);
  }

  public static void post(String path, BiConsumer<Request, Response> function) {
    addRoute("POST", path, function);
  }

  public static BiConsumer<Request, Response> getFunction(String method, String path) {
    for (int i = 0; i < routes.size(); i++) {
      Route route = routes.get(i);
      if (route.doesMatch(method, path))
        return functions.get(i);
    }
    return defaultFunction;
  }

  public static Map<String, String> getParams(String method, String path) {
    for (int i = 0; i < routes.size(); i++) {
      Route route = routes.get(i);
      if (route.doesMatch(method, path))
        return route.getParams(path);
    }
    return new HashMap<>();
  }

  private static void addRoute(String method, String path, BiConsumer<Request, Response> function) {
    routes.add(new Route(method, path));
    functions.add(function);
  }
}
