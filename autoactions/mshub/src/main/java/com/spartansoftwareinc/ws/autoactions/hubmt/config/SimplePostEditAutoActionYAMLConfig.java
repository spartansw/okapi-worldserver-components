package com.spartansoftwareinc.ws.autoactions.hubmt.config;

import java.util.ArrayList;
import java.util.Collections;
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

    public void setActions(Map<String, List<Map<String, Object>>> actions) {
        this.actions = new HashMap<>();
        for (String language : actions.keySet()) {
            final List<Map<String, Object>> actionSetList = actions.get(language);
            final List<Action> actionsList = new ArrayList<>();
            this.actions.put(language, actionsList);
            for (Map<String, Object> action : actionSetList) {
                actionsList.add(new Action((String) action.get("search"), (String) action.get("replace"),
                    (Boolean) action.get("allowFurtherReplacements")));
            }
        }
    }

    public List<Action> getActionsForLanguage(String language) {
        final List<Action> actions = this.actions.get(language);
        return actions != null ? actions : Collections.emptyList();
    }

    public static class Action {
        private String search;
        private String replace;
        private Boolean allowFurtherReplacements;

        private Pattern patternCompiled = null;

        public Action(String search, String replace, Boolean allowFurtherReplacements) {
            this.search = search;
            this.replace = replace;
            this.allowFurtherReplacements = allowFurtherReplacements;
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

        public Boolean getAllowFurtherReplacements() {
            return allowFurtherReplacements == null || allowFurtherReplacements;
        }

        public void setAllowFurtherReplacements(Boolean allowFurtherReplacements) {
            this.allowFurtherReplacements = allowFurtherReplacements;
        }
    }
}
