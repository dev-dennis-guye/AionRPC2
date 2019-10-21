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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtils {

    private XMLUtils() {
        throw new UnsupportedOperationException("Cannot instantiate " + getClass().getSimpleName());
    }

    public static String valueFromAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    public static boolean hasAttribute(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName) != null;
    }

    public static Document fromString(String string)
        throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8)));
    }

    public static Document fromFile(String file)
        throws IOException, SAXException, ParserConfigurationException {
        List<String> document = Files.readAllLines(Paths.get(file));
        return fromString(String.join("", document));
    }

    public static Document fromFile(File file)
        throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        return builder.parse(file);
    }

    public static List<Element> elements(NodeList nodeList) {
        List<Element> elements = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element) {
                elements.add((Element) nodeList.item(i));
            }
        }
        return Collections.unmodifiableList(elements);
    }

    public static Optional<Element> elementFromTag(Element parent, String tag){
        NodeList nodeList = parent.getElementsByTagName(tag);
        if (nodeList.getLength()>0){
            return Optional.of((Element) nodeList.item(0));
        }else {
            return Optional.empty();
        }
    }
}
