package nl.webutils.simplerouter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This is the main class for building/defining all the known routes. Once the
 * routes have been defined, its data is reentrant, i.e. thread safe.
 *
 * Created by Ruud de Grijs on 11-1-2019.
 *
 * @param <T>
 */
public class WebRouting<T> {

    private final List<RouteEntry<T>> instances;

    private WebRouting(List<RouteEntry<T>> entries) {
        this.instances = entries;
    }

    public static class Builder<T> {

        private final List<RouteEntry<T>> entries;

        public Builder() {
            entries = new ArrayList<>();
        }

        public Builder addRoute(MethodAction action, String path, T target) {
            RouteEntry entry = createInstance(action, path, target);
            entries.add(entry);
            return this;
        }

        public WebRouting<T> build() {
            return new WebRouting<>(Collections.unmodifiableList(entries));
        }

        private RouteEntry createInstance(MethodAction action, String path, T target) {
            if (path.indexOf('(') > -1) {
                return new RoutePatternKey(action, path, target);
            } else {
                return new RouteStringKey(action, path, target);
            }
        }

    }

    public MatchedValues matchEntry(MethodAction action, String path) {
        for (RouteEntry entry : instances) {
            MatchedValues matchedValues = entry.matches(action, path);
            if (matchedValues.isMatch()) {
                return matchedValues;
            }
        }

        return MatchedValues.NO_MATCH;
    }

}
