package org.noamichael.utils.xml.api;

import java.util.List;

/**
 *
 * @author michael
 */
public interface ParsedXmlDocument {
    
    public List<XmlElement> getElement(String name);    
    
    public XmlElement getRootElement();
      
}
