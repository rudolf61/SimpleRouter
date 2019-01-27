/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.webutils.simplerouter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author rudol
 */
public class MatchedValues {

    public static final MatchedValues NO_MATCH = new MatchedValues(null, false);
    private Map<String, String> matchValues;
    private boolean match;
    private Object target;

    public MatchedValues(Object target, boolean matches) {
        this.match = matches;
        this.target = target;
    }

    public MatchedValues(Object target, Map<String, String> matches) {
        this.target = target;
        this.matchValues = matches;
        this.match = matches != null && matches.size() > 0;
    }

    public Map<String, String> getMatchedValues() {
        return matchValues;
    }

    public String getParameter(String key) {
        return matchValues.get(key);
    }

    public boolean containsKey(String key) {
        return matchValues.containsKey(key);
    }

    public Set<String> keySet() {
        return matchValues.keySet();
    }

    public boolean isMatch() {
        return match;
    }

    public <T> T getTarget() {
        return (T) target;
    }
    
}
