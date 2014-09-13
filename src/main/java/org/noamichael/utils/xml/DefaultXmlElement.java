package org.noamichael.utils.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.noamichael.utils.xml.api.XmlElement;

/**
 *
 * @author michael
 */
public class DefaultXmlElement implements XmlElement {

    private Map<String, String> attributes;
    private XmlElement parent;
    private List<XmlElement> children;
    private String value;
    private String name;

    public DefaultXmlElement() {
        this.attributes = new HashMap();
        this.children = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public List<XmlElement> getChildren() {
        return children;
    }

    @Override
    public XmlElement getParent() {
        return parent;
    }

    @Override
    public XmlElement setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

    @Override
    public XmlElement setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public XmlElement setChildren(List<XmlElement> children) {
        this.children = children;
        return this;
    }

    @Override
    public XmlElement setParent(XmlElement parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public XmlElement setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "DefaultXmlElement{"+"name="+name + ", attributes=" + attributes + ", parent=" + (parent == null ? "null" : parent.getName()) + ", children=" + children.size() + ", value=" + value +'}';
    }
    
    

}
