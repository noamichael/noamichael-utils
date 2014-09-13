package org.noamichael.utils.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.noamichael.utils.se.StringUtil.isNullOrEmpty;
import org.noamichael.utils.xml.api.ParseException;
import org.noamichael.utils.xml.api.ParsedXmlDocument;
import org.noamichael.utils.xml.api.XmlElement;
import org.noamichael.utils.xml.api.XmlParser;

public class DefaultXmlParserImpl implements XmlParser {

    private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String CDATA_START = "<![CDATA[";
    private static final String CDATA_END = "]]>";

    private DefaultParsedXmlDocument document;
    private SemiParsedElement currentParentElement;

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
        List<SemiParsedElement> semiParsedElements = new ArrayList();
        rawXml = rawXml.replace("\n", "").replace(XML_DECLARATION, "");
        collectTags(rawXml.toCharArray(), semiParsedElements);
        int i = 1;
        for (SemiParsedElement semiParsedElement : semiParsedElements) {
            System.out.println(i++ + semiParsedElement.tagName);
            if (semiParsedElement.getParent() != null) {
                System.out.println("    " + semiParsedElement.getParent().tagName);
            }
            if (!semiParsedElement.getChildren().isEmpty()) {
                document.putElement(createElementWithChildren(semiParsedElement, semiParsedElement.children));
            } else {
                if (document.getElement(semiParsedElement.tagName) == null) {
                    document.putElement(createElement(semiParsedElement));
                }
            }
        }
        return document;
    }

    private XmlElement createElement(SemiParsedElement child) {
        XmlElement xmlElement = new DefaultXmlElement();
        xmlElement.setAttributes(child.attributes);
        xmlElement.setName(child.getTagName());
        xmlElement.setValue(child.getInnerContent());
        return xmlElement;
    }

    private XmlElement createElementWithChildren(SemiParsedElement parent, List<SemiParsedElement> children) {
        XmlElement parentXmlElement = createElement(parent);
        for (SemiParsedElement parsedElement : children) {
            XmlElement childXmlElement = createElement(parsedElement);
            childXmlElement.setParent(parentXmlElement);
            document.putElement(childXmlElement);
            parentXmlElement.getChildren().add(childXmlElement);
        }

        return parentXmlElement;
    }

    private void collectTags(char[] rawData, List<SemiParsedElement> list) {
        collectTags(rawData, list, null);

    }

    private void collectTags(char[] rawData, List<SemiParsedElement> list, SemiParsedElement parent) {
        if (isEmpty(rawData)) {
            return;
        }
        String rawString = String.valueOf(rawData);
        String currentContentToParse = rawString;
        if (containsChildren(currentContentToParse)) {
            SemiParsedElement firstElement = getNextTag(rawString, parent);
            firstElement.setParent(parent);
            currentParentElement = firstElement;
            list.add(firstElement);
            currentContentToParse = firstElement.innerContent;
        }
        SemiParsedElement nextTag;

        while (!currentContentToParse.isEmpty()) {
            nextTag = getNextTag(currentContentToParse, currentParentElement);
            if (containsChildren(nextTag.innerContent)) {
                collectTags(currentContentToParse.toCharArray(), list, currentParentElement);
            } else {
                list.add(nextTag);
            }
            currentContentToParse = currentContentToParse.substring(nextTag.endTagEnd + 1, currentContentToParse.length()).trim();
        }

    }

    public SemiParsedElement getNextTag(String rawString, SemiParsedElement parent) {
        char[] rawData = rawString.toCharArray();
        SemiParsedElement nextElement = new SemiParsedElement();
        int[] beginTagPositions = getStartTagPositions(rawData);
        String foundTag = getTagName(rawString.substring(beginTagPositions[0], beginTagPositions[1]).toCharArray());
        int[] endTagPositions = getEndTagPositions(rawData, foundTag);
        if (parent != null) {
            parent.getChildren().add(nextElement);
        }
        nextElement.setParent(parent);
        nextElement.setStartTagBegin(beginTagPositions[0]);
        nextElement.setStartTagEnd(beginTagPositions[1]);
        nextElement.setEndTagBegin(endTagPositions[0]);
        nextElement.setEndTagEnd(endTagPositions[1]);
        nextElement.setTagName(foundTag);
        nextElement.setInnerContent(rawString.substring(beginTagPositions[1] + 1, endTagPositions[0]));
        int readAttributeStartingPoint = beginTagPositions[0] + foundTag.length() + 1;
        int readAttributeEndingPoint = beginTagPositions[1];
        if (readAttributeStartingPoint < readAttributeEndingPoint) {
            nextElement.setAttributes(readAttributes(rawString.substring(readAttributeStartingPoint, readAttributeEndingPoint)));
            System.out.println(nextElement.attributes);
        }
        return nextElement;
    }

    public boolean containsMoreElements(String data) {
        return containsChildren(data);
    }

    private Map<String, String> readAttributes(String string) {
        Map<String, String> attributes = new HashMap<>();
        string = string.replace("\"", "").trim();
        String[] splitAttrs = string.split("=");
        int currentIndex = 0;
        int nextIndex = 1;
        while (currentIndex < splitAttrs.length && nextIndex < splitAttrs.length) {
            attributes.put(splitAttrs[currentIndex], splitAttrs[nextIndex]);
            currentIndex = currentIndex + 2;
            nextIndex = nextIndex + 2;
        }
        return attributes;
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

    private int[] getEndTagPositions(char[] rawData, String tagName) {
        int beginTagPosition = String.valueOf(rawData).indexOf("</" + tagName + ">");
        int endTagPosition = beginTagPosition + tagName.length() + 2;
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

    public boolean containsChildren(String data) {
        return data.contains("<") || data.contains(">");
    }

    public boolean isEmpty(char[] data) {
        for (char c : data) {
            if (c != '\n' && c != ' ') {
                return false;
            }
        }
        return true;
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

        private SemiParsedElement parent;
        private List<SemiParsedElement> children = new ArrayList<>();
        private Map<String, String> attributes = new HashMap();
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

        @Override
        public String toString() {
            return "SemiParsedElement{" + "startTagBegin=" + startTagBegin + ", startTagEnd=" + startTagEnd + ", endTagBegin=" + endTagBegin + ", endTagEnd=" + endTagEnd + ", tagName=" + tagName + ", innerContent=" + innerContent + '}';
        }

        /**
         * @return the parent
         */
        public SemiParsedElement getParent() {
            return parent;
        }

        /**
         * @param parent the parent to set
         */
        public void setParent(SemiParsedElement parent) {
            this.parent = parent;
        }

        /**
         * @return the children
         */
        public List<SemiParsedElement> getChildren() {
            return children;
        }

        /**
         * @param children the children to set
         */
        public void setChildren(List<SemiParsedElement> children) {
            this.children = children;
        }

        /**
         * @return the attributes
         */
        public Map<String, String> getAttributes() {
            return attributes;
        }

        /**
         * @param attributes the attributes to set
         */
        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }

    }

}
