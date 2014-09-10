package org.noamichael.utils.xml;

import static org.noamichael.utils.se.StringUtil.isNullOrEmpty;
import org.noamichael.utils.xml.api.ParseException;
import org.noamichael.utils.xml.api.ParsedXmlDocument;
import org.noamichael.utils.xml.api.XmlElement;
import org.noamichael.utils.xml.api.XmlParser;

public class DefaultXmlParserImpl implements XmlParser {

    private DefaultParsedXmlDocument document;
    private boolean foundBeginTag;
    private boolean foundEndTag;
    private XmlElement currentParentElement;

    protected enum CharType {

        BEGIN_TAG, END_TAG, EQUALS, SINGLE_QUOTE, DOUBLE_QUOTE, CDATA;
    }

    protected enum ParsePhase {
        READING_TAG;
    }

    @Override
    public ParsedXmlDocument getParsedXmlDocument(String rawXml) {
        if (isNullOrEmpty(rawXml)) {
            throw new ParseException("Cannot parse null or empty xml.");
        }
        document = new DefaultParsedXmlDocument();
        
        return document;
    }
    

    protected CharType getCharType(char c) {
        switch (c) {
            case '\'': {
                return CharType.SINGLE_QUOTE;
            }
            case '"': {
                return CharType.DOUBLE_QUOTE;
            }
            case '<': {
                return CharType.BEGIN_TAG;
            }
            case '>': {
                return CharType.END_TAG;
            }
            case '=':{
                return CharType.EQUALS;
            }
            default: {
                return CharType.CDATA;
            }
        }
    }

}
