package org.aion.rpcgenerator.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLUtils {

    private XMLUtils() {
        throw new UnsupportedOperationException("Cannot instantiate " + getClass().getSimpleName());
    }

    public static String valueFromAttribute(Node node, String attributeName) {
        //throws if attribute does not contain the value
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    /**
     * Checks that attribute exists and returns a value if it does.
     * @param node the node
     * @param attributeName the name of the attribute
     * @return the value if it exists
     */
    public static Optional<String> optionalValueFromAttribute(Node node, String attributeName){
        return hasAttribute(node, attributeName)? Optional.of(valueFromAttribute(node, attributeName)) : Optional.empty();
    }

    /**
     * Checks that the attribute exists
     * @param node
     * @param attributeName
     * @return
     */
    public static boolean hasAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName) != null;
    }

    //Reads the xml document from a string
    public static Document fromString(String string)
        throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder builder = documentBuilder();
        return builder.parse(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Creates the document builder
     * @return
     * @throws ParserConfigurationException
     */
    private static DocumentBuilder documentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
        factory.setValidating(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new ErrorHandler() {//set validation
            private final Logger logger = LoggerFactory.getLogger(this.getClass());
            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                logger.warn("", exception);
                throw exception;
            }
        });
        return builder;
    }

    //reads an xml document given a file name
    public static Document fromFile(String file)
        throws IOException, SAXException, ParserConfigurationException {
        List<String> document = Files.readAllLines(Paths.get(file));
        return fromString(String.join("", document));
    }

    //reads an xml document given a file
    public static Document fromFile(File file)
        throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        return builder.parse(file);
    }

    /**
     * Filters out the elements from a node list
     * @param nodeList the childNodes
     * @return a list of elements
     */
    public static List<Element> elements(NodeList nodeList) {
        List<Element> elements = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element) {
                elements.add((Element) nodeList.item(i));
            }
        }
        return Collections.unmodifiableList(elements);
    }

    /**
     * Gets an element from the given tag name
     * @param parent
     * @param tag
     * @return
     */
    public static Optional<Element> elementFromTag(Element parent, String tag){
        List<Element> nodeList = XMLUtils.elements(parent.getElementsByTagName(tag));
        if (!nodeList.isEmpty()){
            return Optional.of((Element) nodeList.get(0));
        }else {
            return Optional.empty();
        }
    }
}
