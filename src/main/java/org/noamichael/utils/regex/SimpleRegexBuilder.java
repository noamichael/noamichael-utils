package org.noamichael.utils.regex;

import java.util.regex.Pattern;

/**
 *
 * @author Michael
 */
public class SimpleRegexBuilder implements RegexBuilder {

    private StringBuilder sb = new StringBuilder();

    @Override
    public RegexBuilder startString() {
        sb.append("^");
        return this;
    }

    @Override
    public RegexBuilder endString() {
        sb.append("$");
        return this;
    }

    @Override
    public RegexBuilder any() {
        sb.append("*");
        return this;
    }

    @Override
    public RegexBuilder or(RegexBuilder builder) {
        or(builder.buildToString());
        return this;
    }

    @Override
    public RegexBuilder or(String value) {
        sb.append("|").append(value);
        return this;
    }

    @Override
    public RegexBuilder tab() {
        sb.append("\\t");
        return this;
    }

    @Override
    public RegexBuilder space() {
        return this;
    }

    @Override
    public RegexBuilder newline() {
        sb.append("\\n");
        return this;
    }

    @Override
    public RegexBuilder exactly(String value) {
        sb.append(value);
        return this;
    }

    @Override
    public RegexBuilder exactly(int num, String value) {
        sb.append("(").append(value).append(")")
                .append("{").append(num).append("}");
        return this;
    }

    @Override
    public RegexBuilder exactly(int num, RegexExpression value) {
        exactly(num, value.value());
        return this;
    }

    @Override
    public RegexBuilder set(String set) {
        sb.append("[").append(set).append("]");
        return this;
    }

    @Override
    public RegexBuilder oneOrMore(String value) {
        sb.append(value).append("+");
        return this;
    }

    @Override
    public Pattern build() {
        return Pattern.compile(sb.toString());
    }

    @Override
    public String buildToString() {
        return sb.toString();
    }

}
