package org.noamichael.utils.measurement;

import org.noamichael.utils.xml.DefaultXmlParserImpl;
import org.noamichael.utils.xml.api.ParsedXmlDocument;
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
        ParsedXmlDocument parsedXmlDocument = xmlParser.getParsedXmlDocument("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<actions>\n"
                + "        <action>\n"
                + "            <actionName>run</actionName>\n"
                + "            <packagings>\n"
                + "                <packaging>jar</packaging>\n"
                + "            </packagings>\n"
                + "            <goals>\n"
                + "                <goal>process-classes</goal>\n"
                + "                <goal>org.codehaus.mojo:exec-maven-plugin:1.2.1:exec</goal>\n"
                + "            </goals>\n"
                + "            <properties>\n"
                + "                <exec.args>-classpath %classpath org.noamichael.utils.measurement.Main</exec.args>\n"
                + "                <exec.executable>java</exec.executable>\n"
                + "            </properties>\n"
                + "        </action>\n"
                + "        <action>\n"
                + "            <actionName>debug</actionName>\n"
                + "            <packagings>\n"
                + "                <packaging>jar</packaging>\n"
                + "            </packagings>\n"
                + "            <goals>\n"
                + "                <goal>process-classes</goal>\n"
                + "                <goal>org.codehaus.mojo:exec-maven-plugin:1.2.1:exec</goal>\n"
                + "            </goals>\n"
                + "            <properties>\n"
                + "                <exec.args>-Xdebug -Xrunjdwp:transport=dt_socket,server=n,address=${jpda.address} -classpath %classpath org.noamichael.utils.measurement.Main</exec.args>\n"
                + "                <exec.executable>java</exec.executable>\n"
                + "                <jpda.listen>true</jpda.listen>\n"
                + "            </properties>\n"
                + "        </action>\n"
                + "        <action>\n"
                + "            <actionName>profile</actionName>\n"
                + "            <packagings>\n"
                + "                <packaging>jar</packaging>\n"
                + "            </packagings>\n"
                + "            <goals>\n"
                + "                <goal>process-classes</goal>\n"
                + "                <goal>org.codehaus.mojo:exec-maven-plugin:1.2.1:exec</goal>\n"
                + "            </goals>\n"
                + "            <properties>\n"
                + "                <exec.args>-classpath %classpath org.noamichael.utils.measurement.Main</exec.args>\n"
                + "                <exec.executable>java</exec.executable>\n"
                + "            </properties>\n"
                + "        </action>\n"
                + "    </actions>\n"
                + "");
        System.out.println(parsedXmlDocument.getElement("actions").get(0));

    }
}
