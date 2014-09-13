package org.noamichael.utils.xml.api;

import java.util.List;

/**
 *
 * @author michael
 */
public interface ParsedXmlDocument {
    
    /**
     * Returns all of the {@link XmlElement} which are of the given name.
     * @param name
     * @return 
     */
    public List<XmlElement> getElement(String name);    
    
    /**
     * Returns the highest level element.
     * @return 
     */
    public XmlElement getRootElement();
      
}
