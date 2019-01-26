package nl.webutils.simplerouter;


import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by RUUD on 11-1-2017.
 */
public interface RouteEntry<T> {
    boolean matches(MethodAction method, String path);

    T getTarget();

    default MethodAction getMethod() {
        return MethodAction.GET;
    }

    default String normalize(String path) {
        String normPath = null;
        if (path.endsWith("/")) {
            normPath = path.substring(0, path.length() - 1);
        } else {
            normPath = path;
        }

        if (!path.startsWith("/")) {
            normPath = "/" + normPath;
        }

        return normPath;
    }

    /**
     * Get all matched values from the path i/a 
     * @return 
     */
    default Map<String, String> getMatchedValues() {
        return Collections.emptyMap();
    }

    /**
     * Get a specific parameter from the path
     * 
     * @param key
     * @return 
     */
    default String getParameter(String key) {
        return null;
    }

    /**
     * Does the path contain given path parameter
     * 
     * @param key
     * @return 
     */
    default boolean containsKey(String key) {
        return false;
    }

    /**
     * Return all parameter names as a set.
     * 
     * @return 
     */
    default Set<String> keySet() {
        return Collections.EMPTY_SET;
    }

}
