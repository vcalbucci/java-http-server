import java.util.HashMap;

import handlers.HTTPHandler;
import handlers.NotFoundHandler;

public class Router {

    HashMap<String, HashMap<String, HTTPHandler>> routes = new HashMap<>();

    public HTTPHandler route(HTTPRequest request) {
        HashMap<String, HTTPHandler> methodRoutes = routes.get(request.getMethod());
        if (methodRoutes == null) {
            return new NotFoundHandler();
        }
        HTTPHandler handler = methodRoutes.get(request.getPath());
        if (handler == null) {
            return new NotFoundHandler();
        }
        return handler;
    }

    public void addRoute(String method, String path, HTTPHandler handler) {
        HashMap<String, HTTPHandler> methodRoutes = routes.getOrDefault(method, new HashMap<>());
        methodRoutes.put(path, handler);
        routes.put(method, methodRoutes);
    }

}
