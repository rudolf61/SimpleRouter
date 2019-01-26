package nl.webutils.simplerouter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rudolf de Grijs 2019
 * 
 * RouteEntry based on a REGEX. Supports string and number pattern. 
 * The Path pattern is defined as follows (<Path parameter name>:(int|string)) e.g. (id:int).
 * When it matches you will find id as a path parameter.
 * 
 * @param <T>
 */
public class RoutePatternKey<T> extends AbstractRouteEntry<T> {

    private static final Pattern parameters = Pattern.compile("\\(([a-zA-Z0-9_-]+)\\:(int|string)\\)");

    private final Pattern pattern;
    private final List<String> keys;
    private Map<String, String> matchValues;

    public RoutePatternKey(MethodAction action, String path, T target) {
        super(action, target);
        String normPath = normalize(path);

        Matcher m = parameters.matcher(normPath);
        StringBuffer sb = new StringBuffer();
        keys = new ArrayList<>();

        while (m.find()) {
            keys.add(m.group(1));
            String replaceValue = getRegex(m.group(2));
            m.appendReplacement(sb, replaceValue);
        }

        m.appendTail(sb);


        pattern = Pattern.compile(sb.toString());
    }

    private String getRegex(String type) {
        String regex = "[^/]+";
        switch (type) {
            case "int":
                regex = "\\d+";
                break;
            default:
                break;
        }

        return Matcher.quoteReplacement("(" + regex + ")");
    }

    public boolean matches(MethodAction action, String path) {

        if (action != getMethod())
            return false;

        String normPath = normalize(path);
        Matcher matcher = pattern.matcher(normPath);

        if (matcher.matches()) {

            int count = matcher.groupCount();
            matchValues = new HashMap<>();

            for (int i = 1; i <= count; i++) {
                String value = matcher.group(i);
                String key = keys.get(i - 1);
                matchValues.put(key, value);
            }

            return true;
        }
        else {
            matchValues = Collections.emptyMap();
            return false;
        }
    }

    @Override
    public Map<String, String> getMatchedValues() {
        return matchValues;
    }


    @Override
    public String getParameter(String key) {
        return matchValues.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return matchValues.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return matchValues.keySet();
    }

}
