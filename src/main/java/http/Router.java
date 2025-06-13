package http;

import java.util.HashMap;
import java.util.Map;

import handlers.HTTPHandler;
import handlers.NotFoundHandler;
import handlers.OptionsHandler;

/**
 * Maintains routing logic for HTTP requests.
 * Maps HTTP methods and paths to corresponding handlers.
 * Used to direct incoming requests to the appropriate HTTPHandler.
 */
public class Router {

    Map<String, Map<String, HTTPHandler>> routes = new HashMap<>();

    public HTTPHandler route(HTTPRequest request) {
        Map<String, HTTPHandler> methodRoutes = routes.get(request.getMethod());
        if (methodRoutes == null) {
            return new NotFoundHandler();
        }
        if (request.getMethod().equals("OPTIONS")) {
            return new OptionsHandler(this);
        }
        return methodRoutes.entrySet().stream()
                .filter(entry -> request.getPath().startsWith(entry.getKey()))
                .map(entry -> entry.getValue())
                .findFirst()
                .orElse(new NotFoundHandler());

    }

    public void addRoute(String method, String path, HTTPHandler handler) {
        Map<String, HTTPHandler> methodRoutes = routes.getOrDefault(method, new HashMap<>());
        methodRoutes.put(path, handler);
        routes.put(method, methodRoutes);
    }

    public Map<String, Map<String, HTTPHandler>> getRoutes() {
        return routes;
    }

}
