package org.noamichael.utils.measurement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import org.noamichael.utils.se.ClassPathScanner;
import org.noamichael.utils.se.ClassPathScanner.ScannerSearchResult;
import static org.noamichael.utils.se.ClassPathScanner.scanProjectForAnnotation;
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
        List<ScannerSearchResult> results = scanProjectForAnnotation(Test.class, ElementType.CONSTRUCTOR);
        System.out.println(results.get(0).getResult());
        //System.out.println(ClassPathScanner.scanForClasses(clazz -> clazz.isInterface()));
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
