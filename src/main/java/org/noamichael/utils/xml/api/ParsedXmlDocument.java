package org.noamichael.utils.xml.api;

import java.util.Map;

/**
 *
 * @author michael
 */
public interface ParsedXmlDocument {
    
    public XmlElement getElement(String name);    
    
    public Map<String, String> getAttributes(String elementName);
    
}
