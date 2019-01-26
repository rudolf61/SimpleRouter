package nl.webutils.simplerouter;

/**
 * Created by RUUD on 11-1-2017.
 */
public abstract class AbstractRouteEntry<T> implements RouteEntry<T> {
    private T            target;
    private MethodAction action;

    public AbstractRouteEntry(MethodAction action, T target) {
        this.target = target;
        this.action = action;
    }

    @Override
    public T getTarget() {
        return target;
    }

    @Override
    public MethodAction getMethod() {
        return action;
    }

    
}
