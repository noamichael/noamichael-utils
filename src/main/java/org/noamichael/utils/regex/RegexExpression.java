package org.noamichael.utils.regex;

/**
 *
 * @author Michael
 */
public interface RegexExpression {

    public static final RegexExpression NUMBER = () -> {
        return "\\d";
    };
    public static final RegexExpression CHARACTER = () -> {
        return "[a-zA-Z]";
    };

    String value();

}
