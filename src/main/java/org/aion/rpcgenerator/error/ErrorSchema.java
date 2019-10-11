package org.aion.rpcgenerator.error;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ErrorSchema {
    private final String errorClass;
    private final int code;
    private final String message;

    public ErrorSchema(Node node){
        errorClass = node.getAttributes().getNamedItem("error_class").getNodeValue();
        code = Integer.parseInt(node.getAttributes().getNamedItem("code").getNodeValue());
        message = node.getAttributes().getNamedItem("message").getNodeValue();
    }

    public Map<String, Object> toMap(){
        return Map.ofEntries(
            Map.entry("error_class", errorClass),
            Map.entry("code", Integer.toString(code)),
            Map.entry("message", message)
        );
    }

    public String getErrorClass() {
        return errorClass;
    }

    public static List<ErrorSchema> fromDocument(Document document){
        Element root = document.getDocumentElement();
        if (root.getNodeName().equals("errors")){
            NodeList errorNodes = root.getChildNodes();
            List<ErrorSchema> errorSchemas = new ArrayList<>();
            for (int i = 0; i < errorNodes.getLength(); i++) {
                Node node = errorNodes.item(i);
                errorSchemas.add(new ErrorSchema(node));
            }
            return Collections.unmodifiableList(errorSchemas);
        } else {
            throw new IllegalArgumentException("Expected errors.xml");
        }
    }
}
