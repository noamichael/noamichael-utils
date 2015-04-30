package org.noamichael.utils.measurement;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Pattern;
import org.noamichael.utils.regex.RegexBuilder;
import org.noamichael.utils.regex.RegexExpression;
import org.noamichael.utils.regex.SimpleRegexBuilder;
import org.noamichael.utils.xml.api.XmlElement;

/**
 *
 * @author michael
 */
public class Main {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Test {
    }

    public static void main(String[] args) {
        RegexBuilder builder = new SimpleRegexBuilder();
        builder.startString()
                .exactly(3, RegexExpression.NUMBER)
                .exactly("-")
                .exactly(3, RegexExpression.NUMBER)
                .exactly("-")
                .exactly(4, RegexExpression.NUMBER)
                .endString();
        System.out.println("Regex:" + builder.buildToString());
        Pattern pattern = builder.build();
        System.out.println("Trying 123-231-2312:" + pattern.matcher("123-231-2312").matches());
        System.out.println("Trying 123-2331-2312:" + pattern.matcher("123-2331-2312").matches());
    }

    @Test
    public static void printXmlTree(XmlElement element) {
        for (XmlElement xmlElement : element.getChildren()) {
            printXmlTree(xmlElement);
        }
        System.out.println(element.getName());
        System.out.println("    " + element.getAttributes());
        System.out.println("        " + element.getValue());
    }
}
