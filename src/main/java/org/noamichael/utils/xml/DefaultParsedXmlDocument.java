package org.noamichael.utils.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.noamichael.utils.xml.api.ParsedXmlDocument;
import org.noamichael.utils.xml.api.XmlElement;

public class DefaultParsedXmlDocument implements ParsedXmlDocument {

    private final Map<String, List<XmlElement>> elements;
    private XmlElement rootElement;

    public DefaultParsedXmlDocument() {
        elements = new HashMap<>();
    }

    @Override
    public List<XmlElement> getElement(String name) {
        return elements.get(name);
    }

    @Override
    public XmlElement getRootElement() {
        return rootElement;
    }

    public void putElement(XmlElement value) {
        List<XmlElement> currentElements = elements.get(value.getName());
        if (currentElements == null) {
            currentElements = new ArrayList();
            elements.put(value.getName(), currentElements);
        }
        currentElements.add(value);
        if (value.getParent() == null && rootElement == null) {
            rootElement = value;
        }
    }

}
