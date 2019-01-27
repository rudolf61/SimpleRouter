package nl.webutils.simplerouter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by RUUD on 11-1-2017.
 */
public interface RouteEntry<T> {

    MatchedValues matches(MethodAction method, String path);

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

}
