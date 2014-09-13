package org.noamichael.utils.measurement;

import org.noamichael.utils.xml.DefaultXmlParserImpl;
import org.noamichael.utils.xml.api.ParsedXmlDocument;
import org.noamichael.utils.xml.api.XmlElement;
import org.noamichael.utils.xml.api.XmlParser;

/**
 *
 * @author michael
 */
public class Main {

    public static void main(String[] args) {
        XmlParser xmlParser = new DefaultXmlParserImpl();
        ParsedXmlDocument parsedXmlDocument = xmlParser.getParsedXmlDocument("<web-app version=\"3.1\" xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd\"><context-param><param-name>javax.faces.PROJECT_STAGE</param-name><param-value>Development</param-value></context-param><servlet><servlet-name>Faces Servlet</servlet-name><servlet-class>javax.faces.webapp.FacesServlet</servlet-class><load-on-startup>1</load-on-startup></servlet><servlet-mapping><servlet-name>Faces Servlet</servlet-name><url-pattern>*.xhtml</url-pattern></servlet-mapping><session-config><session-timeout>\n"
                + "            30\n"
                + "        </session-timeout></session-config><welcome-file-list><welcome-file test=\"yes\">index.xhtml</welcome-file></welcome-file-list></web-app>");
        XmlElement xmlElement = parsedXmlDocument.getRootElement();
        printXmlTree(xmlElement);
    }

    public static void printXmlTree(XmlElement element) {
        for(XmlElement xmlElement: element.getChildren()){
            printXmlTree(xmlElement);
        }
        System.out.println(element.getName());
        System.out.println("    "+element.getAttributes());
        System.out.println("        "+element.getValue());
    }
}
