package org.noamichael.utils.measurement;

import org.noamichael.utils.xml.DefaultXmlParserImpl;
import org.noamichael.utils.xml.api.XmlParser;

/**
 *
 * @author michael
 */
public class Main {

    public static void main(String[] args) {
        Dimension dimensions = new Dimension();
        dimensions.addValue(Dimension.Imperial.FEET, 5);
        dimensions.addValue(Dimension.Imperial.YARDS, 3);
        dimensions.addValue(Dimension.Imperial.INCHES, 100);
        Dimension dimension2 = new Dimension();
        dimension2.addValue(Dimension.Imperial.MILES, 3);
        XmlParser xmlParser = new DefaultXmlParserImpl();
        xmlParser.getParsedXmlDocument("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
"    <modelVersion>4.0.0</modelVersion>\n" +
"    <groupId>org.noamichael</groupId>\n" +
"    <artifactId>noamichael-utils</artifactId>\n" +
"    <version>1.0-SNAPSHOT</version>\n" +
"    <packaging>jar</packaging>\n" +
"    <dependencies>\n" +
"        <dependency>\n" +
"            <groupId>javax</groupId>\n" +
"            <artifactId>javaee-web-api</artifactId>\n" +
"            <version>7.0</version>\n" +
"            <scope>provided</scope>\n" +
"        </dependency>\n" +
"    </dependencies>\n" +
"    <properties>\n" +
"        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
"        <maven.compiler.source>1.8</maven.compiler.source>\n" +
"        <maven.compiler.target>1.8</maven.compiler.target>\n" +
"    </properties>\n" +
"</project>");
    }
}
