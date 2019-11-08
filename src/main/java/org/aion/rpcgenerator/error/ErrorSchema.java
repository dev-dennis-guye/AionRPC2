package org.aion.rpcgenerator.error;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.util.SchemaUtils;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ErrorSchema implements Mappable {

    private final String errorClass;
    private final int code;
    private final String message;
    private final List<String> comments;
    private final String modifiable;

    public ErrorSchema(Element node) {
        errorClass = node.getAttributes().getNamedItem("error_class").getNodeValue();
        code = Integer.parseInt(node.getAttributes().getNamedItem("code").getNodeValue());
        message = node.getAttributes().getNamedItem("message").getNodeValue();
        comments= SchemaUtils.getComments(node.getChildNodes());
        modifiable = XMLUtils.valueFromAttribute(node, "modifiable");
    }

    public ErrorSchema(String errorClass, int code, String message,
        List<String> comments, String modifiable) {
        this.errorClass = errorClass;
        this.code = code;
        this.message = message;
        this.comments = comments;
        this.modifiable = modifiable;
    }

    public static List<ErrorSchema> fromDocument(Document document) {
        Element root = document.getDocumentElement();
        if (root.getNodeName().equals("errors")) {
            List<Element> errorNodes = XMLUtils.elements(root.getChildNodes());
            return errorNodes.stream().map(ErrorSchema::new)
                .collect(Collectors.toUnmodifiableList());
        } else {
            throw new IllegalArgumentException("Expected errors.xml");
        }
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("error_class", errorClass),
            Map.entry("code", Integer.toString(code)),
            Map.entry("message", message),
            Map.entry("comments", comments),
            Map.entry("modifiable", modifiable)
        );
    }

    public String getErrorClass() {
        return errorClass;
    }

    int getCode() {
        return code;
    }

    String getMessage() {
        return message;
    }

    List<String> getComments() {
        return comments;
    }
}
