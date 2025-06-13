package http;

import java.util.HashMap;

import handlers.HTTPHandler;
import handlers.NotFoundHandler;

/**
 * Maintains routing logic for HTTP requests.
 * Maps HTTP methods and paths to corresponding handlers.
 * Used to direct incoming requests to the appropriate HTTPHandler.
 */
public class Router {

    HashMap<String, HashMap<String, HTTPHandler>> routes = new HashMap<>();

    public HTTPHandler route(HTTPRequest request) {
        HashMap<String, HTTPHandler> methodRoutes = routes.get(request.getMethod());
        if (methodRoutes == null) {
            return new NotFoundHandler();
        }
        return methodRoutes.entrySet().stream()
                .filter(entry -> request.getPath().startsWith(entry.getKey()))
                .map(entry -> entry.getValue())
                .findFirst()
                .orElse(new NotFoundHandler());

    }

    public void addRoute(String method, String path, HTTPHandler handler) {
        HashMap<String, HTTPHandler> methodRoutes = routes.getOrDefault(method, new HashMap<>());
        methodRoutes.put(path, handler);
        routes.put(method, methodRoutes);
    }

}
