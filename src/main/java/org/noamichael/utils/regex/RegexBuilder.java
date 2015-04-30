package org.noamichael.utils.regex;

import java.util.regex.Pattern;

/**
 *
 * @author Michael
 */
public interface RegexBuilder {

    RegexBuilder startString();

    RegexBuilder endString();

    RegexBuilder any();

    RegexBuilder or(RegexBuilder builder);

    RegexBuilder or(String value);

    RegexBuilder tab();

    RegexBuilder space();

    RegexBuilder newline();

    RegexBuilder exactly(String value);

    RegexBuilder exactly(int num, String value);

    RegexBuilder exactly(int num, RegexExpression value);

    RegexBuilder set(String set);

    RegexBuilder oneOrMore(String value);

    Pattern build();

    String buildToString();

    //RegexBuilder oneOrMore(char value);
}
