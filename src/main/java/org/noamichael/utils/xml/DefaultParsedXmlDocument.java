package org.noamichael.utils.xml;

import java.util.HashMap;
import java.util.Map;
import org.noamichael.utils.xml.api.ParsedXmlDocument;
import org.noamichael.utils.xml.api.XmlElement;

public class DefaultParsedXmlDocument implements ParsedXmlDocument {

    Map<String, XmlElement> elements;

    public DefaultParsedXmlDocument() {
        elements = new HashMap<>();
    }

    @Override
    public XmlElement getElement(String name) {
        return elements.get(name);
    }

    @Override
    public Map<String, String> getAttributes(String elementName) {
        return elements.get(elementName).getAttributes();
    }
    
    public void putElement(XmlElement value){
        elements.put(value.getName(), value);
    }
    

}
