package org.noamichael.utils.xml;

import java.util.ArrayList;
import java.util.List;
import static org.noamichael.utils.se.StringUtil.isNullOrEmpty;
import org.noamichael.utils.xml.api.ParseException;
import org.noamichael.utils.xml.api.ParsedXmlDocument;
import org.noamichael.utils.xml.api.XmlElement;
import org.noamichael.utils.xml.api.XmlParser;

public class DefaultXmlParserImpl implements XmlParser {

    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private DefaultParsedXmlDocument document;
    private XmlElement currentParentElement;

    protected enum CharType {

        BEGIN_TAG, END_TAG, EQUALS, SINGLE_QUOTE, DOUBLE_QUOTE, CDATA, SPACE, SLASH;
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
        int[] positions = getStartTagPositions(rawXml.toCharArray());
        String beginTag = rawXml.substring(positions[0], positions[1] + 1);
        System.out.println(beginTag);
        System.out.println(getTagName(beginTag.toCharArray()));

        int[] endTagPosition = getEndTagPositions(rawXml.toCharArray());
        String endTag = rawXml.substring(endTagPosition[0], endTagPosition[1] + 1);
        System.out.println(endTag);
        System.out.println(getTagName(endTag.toCharArray()));

        return document;
    }

    private void collectTags(char[] rawData) {
        List<SemiParsedElement> semiParsedElements = new ArrayList();
        int i = 0;

    }

    private int[] getStartTagPositions(char[] rawData) {
        int currentIndex = 0;
        int beginTagPosition = -1;
        int endTagPosition = -1;
        boolean kill = false;
        boolean foundBeginningTag = false;
        while (currentIndex < rawData.length && !kill) {
            CharType charType = getCharType(rawData[currentIndex]);
            switch (charType) {
                case BEGIN_TAG: {
                    if (foundBeginningTag == true) {
                        throw new ParseException("The beginning of the tag was already found.");
                    }
                    beginTagPosition = currentIndex;
                    foundBeginningTag = true;
                    break;
                }
                case END_TAG: {
                    if (foundBeginningTag == false) {
                        throw new ParseException("Found < before >");
                    }
                    endTagPosition = currentIndex;
                    kill = true;
                    continue;
                }
            }
            currentIndex++;
        }
        return new int[]{beginTagPosition, endTagPosition};
    }

    private int[] getEndTagPositions(char[] rawData) {
        int currentIndex = rawData.length - 1;
        int beginTagPosition = -1;
        int endTagPosition = -1;
        boolean kill = false;
        boolean foundEndTag = false;
        while (currentIndex >= 0 && !kill) {
            CharType charType = getCharType(rawData[currentIndex]);
            switch (charType) {
                case BEGIN_TAG: {
                    if (!foundEndTag) {
                        throw new ParseException("Found the first tag before the last!");
                    }
                    beginTagPosition = currentIndex;
                    if (!peakForward(rawData, currentIndex).equals(CharType.SLASH)) {
                        System.out.println("Non-single-endtag");
                    }
                    kill = true;
                    continue;
                }
                case END_TAG: {
                    if (foundEndTag) {
                        throw new ParseException("The end of the tag was already found");
                    }
                    foundEndTag = true;
                    endTagPosition = currentIndex;
                    break;
                }
            }
            currentIndex--;
        }
        return new int[]{beginTagPosition, endTagPosition};
    }

    private CharType peakForward(char[] data, int index) {
        if (index + 1 >= data.length) {
            throw new ParseException("Tried to peak forward out of index.");
        }
        return getCharType(data[index + 1]);
    }

    protected String getTagName(char[] rawData) {
        String tagName = "";
        int i = 1;
        while (i < rawData.length && rawData[i] != ' ') {
            if (rawData[i] != '/' && rawData[i] != '>') {
                tagName += rawData[i];
            }
            i++;
        }
        return tagName;
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
            case '=': {
                return CharType.EQUALS;
            }
            case ' ': {
                return CharType.SPACE;
            }
            case '/': {
                return CharType.SLASH;
            }
            default: {
                return CharType.CDATA;
            }
        }
    }

    protected class SemiParsedElement {

        private int startTagBegin;
        private int startTagEnd;
        private int endTagBegin;
        private int endTagEnd;
        private String tagName;
        private String innerContent;

        /**
         * @return the startTagBegin
         */
        public int getStartTagBegin() {
            return startTagBegin;
        }

        /**
         * @param startTagBegin the startTagBegin to set
         */
        public void setStartTagBegin(int startTagBegin) {
            this.startTagBegin = startTagBegin;
        }

        /**
         * @return the startTagEnd
         */
        public int getStartTagEnd() {
            return startTagEnd;
        }

        /**
         * @param startTagEnd the startTagEnd to set
         */
        public void setStartTagEnd(int startTagEnd) {
            this.startTagEnd = startTagEnd;
        }

        /**
         * @return the endTagBegin
         */
        public int getEndTagBegin() {
            return endTagBegin;
        }

        /**
         * @param endTagBegin the endTagBegin to set
         */
        public void setEndTagBegin(int endTagBegin) {
            this.endTagBegin = endTagBegin;
        }

        /**
         * @return the endTagEnd
         */
        public int getEndTagEnd() {
            return endTagEnd;
        }

        /**
         * @param endTagEnd the endTagEnd to set
         */
        public void setEndTagEnd(int endTagEnd) {
            this.endTagEnd = endTagEnd;
        }

        /**
         * @return the tagName
         */
        public String getTagName() {
            return tagName;
        }

        /**
         * @param tagName the tagName to set
         */
        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        /**
         * @return the innerContent
         */
        public String getInnerContent() {
            return innerContent;
        }

        /**
         * @param innerContent the innerContent to set
         */
        public void setInnerContent(String innerContent) {
            this.innerContent = innerContent;
        }

    }

}
