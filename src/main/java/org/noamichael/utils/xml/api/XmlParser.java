package org.noamichael.utils.xml.api;

/**
 *
 * @author michael
 */
public interface XmlParser {
    
    /**
     * Returns the {@link ParsedXmlDocument} of the given string.
     * @param rawXml
     * @return 
     */
    public ParsedXmlDocument getParsedXmlDocument(String rawXml);
    
    
}
