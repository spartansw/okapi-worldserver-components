package com.spartansoftwareinc.ws.autoactions.hubmt.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SimplePostEditAutoActionYAMLConfig {


    private Map<String, List<Action>> actions;

    public SimplePostEditAutoActionYAMLConfig() {
    }

    public Map<String, List<Action>> getActions() {
        return actions;
    }

    public void setActions(Map<String, List<Map<String, String>>> actions) {
        this.actions = new HashMap<>();
        for (String language : actions.keySet()) {
            final List<Map<String, String>> actionSetList = actions.get(language);
            final List<Action> actionsList = new ArrayList<>();
            this.actions.put(language, actionsList);
            for (Map<String, String> action : actionSetList) {
                actionsList.add(new Action(action.get("search"), action.get("replace")));
            }
        }
    }

    public List<Action> getActionsForLanguage(String language) {
        return actions.get(language);
    }

    public static class Action {
        private String search;
        private String replace;

        private Pattern patternCompiled = null;

        public Action(String search, String replace) {
            this.search = search;
            this.replace = replace;
        }

        public Pattern getPatternCompiled() {
            if (patternCompiled == null) {
                patternCompiled = Pattern.compile(search);
            }
            return patternCompiled;
        }

        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
            this.search = search;
        }

        public String getReplace() {
            return replace;
        }

        public void setReplace(String replace) {
            this.replace = replace;
        }
    }
}
