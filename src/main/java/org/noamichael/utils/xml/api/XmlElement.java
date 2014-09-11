package org.noamichael.utils.xml.api;

import java.util.List;
import java.util.Map;

/**
 *
 * @author michael
 */
public interface XmlElement {
    
    public String getName();

    public Map<String, String> getAttributes();

    public String getValue();

    public List<XmlElement> getChildren();
    
    public XmlElement setName(String name);
    
    public XmlElement getParent();

    public XmlElement setAttributes(Map<String, String> attributes);

    public XmlElement setValue(String value);

    public XmlElement setChildren(List<XmlElement> children);
    
    public XmlElement setParent(XmlElement parent);
    
}
