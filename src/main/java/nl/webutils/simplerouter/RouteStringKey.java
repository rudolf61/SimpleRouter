package nl.webutils.simplerouter;

/**
 * Created by Ruud de Grijs on 2019
 * 
 * Path based on a plain path expression
 * 
 * @param <T>
 */
public class RouteStringKey<T> extends AbstractRouteEntry<T> {
    private final String route;

    public RouteStringKey(MethodAction action, String route, T target) {
        super(action, target);
        this.route = normalize(route);
    }


    @Override
    public boolean matches(MethodAction method, String path) {
        return method == getMethod() && normalize(path).equalsIgnoreCase(route);
    }

}
