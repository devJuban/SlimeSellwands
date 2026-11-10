package com.artillexstudios.axapi.placeholders;

import com.artillexstudios.axapi.placeholders.exception.PlaceholderException;
import com.artillexstudios.axapi.utils.functions.ThrowingFunction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {
    private final String placeholder;
    private final PlaceholderArguments arguments;
    private final Pattern pattern;
    private final boolean placeholderAPI;
    private final ThrowingFunction<PlaceholderContext, String, PlaceholderException> handler;
    private final ThreadLocal<Matcher> matchers;

    public Placeholder(String placeholder, PlaceholderArguments arguments, Pattern pattern, boolean placeholderAPI, ThrowingFunction<PlaceholderContext, String, PlaceholderException> handler, ThreadLocal<Matcher> matchers) {
        this.placeholder = placeholder;
        this.arguments = arguments;
        this.pattern = pattern;
        this.placeholderAPI = placeholderAPI;
        this.handler = handler;
        this.matchers = matchers;
    }

    public String placeholder() {
        return this.placeholder;
    }

    public PlaceholderArguments arguments() {
        return this.arguments;
    }

    public Pattern pattern() {
        return this.pattern;
    }

    public boolean placeholderAPI() {
        return this.placeholderAPI;
    }

    public ThrowingFunction<PlaceholderContext, String, PlaceholderException> handler() {
        return this.handler;
    }

    public ThreadLocal<Matcher> matchers() {
        return this.matchers;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Placeholder)) {
            return false;
        }
        Placeholder that = (Placeholder) object;
        return java.util.Objects.equals(this.placeholder, that.placeholder) && java.util.Objects.equals(this.arguments, that.arguments) && java.util.Objects.equals(this.pattern, that.pattern) && java.util.Objects.equals(this.placeholderAPI, that.placeholderAPI) && java.util.Objects.equals(this.handler, that.handler) && java.util.Objects.equals(this.matchers, that.matchers);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(placeholder, arguments, pattern, placeholderAPI, handler, matchers);
    }

    private static final Pattern placeholderRegex = Pattern.compile("<([a-zA-Z0-9]+)>");

    public Placeholder(String placeholder, PlaceholderArguments arguments, boolean placeholderAPI, ThrowingFunction<PlaceholderContext, String, PlaceholderException> handler) {
        this(placeholder, arguments, pattern(placeholder), placeholderAPI, handler, ThreadLocal.withInitial(() -> pattern(placeholder).matcher("")));
    }

    public PlaceholderContext newContext(PlaceholderParameters parameters, Matcher matcher) {
        return new PlaceholderContext(this, parameters, matcher);
    }

    public Matcher match(CharSequence line) {
        return this.matchers.get().reset(line);
    }

    private static Pattern pattern(String placeholder) {
        StringBuilder builder = new StringBuilder();
        Matcher matcher = placeholderRegex.matcher(placeholder);
        int begin = 0;

        while (matcher.find()) {
            builder.append(Pattern.quote(placeholder.substring(begin, matcher.start()))).append("++");
            builder.append("(?").append(matcher.group()).append("[^%]*?)");
            begin = matcher.end();
        }

        builder.append(Pattern.quote(placeholder.substring(begin)));

        return Pattern.compile(builder.toString());
    }
    @Override
    public String toString() {
        return "Placeholder[placeholder=" + this.placeholder + ", arguments=" + this.arguments + ", pattern=" + this.pattern + ", placeholderAPI=" + this.placeholderAPI + ", handler=" + this.handler + ", matchers=" + this.matchers + "]";
    }

}
