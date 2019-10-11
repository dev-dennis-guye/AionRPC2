package org.aion.rpcgenerator.rpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.data.Type;
import org.aion.rpcgenerator.error.ErrorSchema;
import org.aion.rpcgenerator.util.SchemaUtils;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RPCSchema implements Mappable {

    private final String rpc;
    private List<ErrorSchema> errors = new ArrayList<>();
    private List<Type> types = new ArrayList<>();
    private List<MethodSchema> methods = new ArrayList<>();
    private List<String> comments = new ArrayList<>();
    public RPCSchema(Document rpcScheme, List<ErrorSchema> errorsSchemas) {
        Element root = rpcScheme.getDocumentElement();
        rpc = root.getNodeName();
        NodeList nodeList = root.getChildNodes();
        for (Element element: XMLUtils.elements(nodeList)) {
            SchemaDef def = SchemaDef.fromNode(element);
            switch (def) {
                case ERRORS:
                    errors = getErrors(element.getChildNodes(), errorsSchemas);
                    break;
                case TYPES:
                    types = getTypes(element.getChildNodes());
                    break;
                case METHOD:
                    methods = getMethodSchemas(element.getChildNodes());
                    break;
                case COMMENTS:
                    comments= SchemaUtils.getComments(element.getChildNodes());
            }
        }

        SchemaUtils.initializeNodes(types);

        for (MethodSchema methodSchema : methods) {
            methodSchema.setParamType(types);
            methodSchema.setReturnType(types);
        }
    }

    private static List<ErrorSchema> getErrors(NodeList nodeList, List<ErrorSchema> errorSchemas) {
        List<String> errorNames = XMLUtils.elements(nodeList).stream()
            .map(element -> XMLUtils.valueFromAttribute(element, "error_class"))
            .collect(Collectors.toList());

        return errorSchemas.stream()
            .filter(e -> errorNames.contains(e.getErrorClass()))
            .collect(Collectors.toUnmodifiableList());
    }

    private static List<Type> getTypes(NodeList nodeList) {
        return XMLUtils.elements(nodeList)
            .stream()
            .map(Type::fromNode)
            .collect(Collectors.toUnmodifiableList());
    }

    private static List<MethodSchema> getMethodSchemas(NodeList nodeList) {
        return XMLUtils.elements(nodeList).stream()
            .map(MethodSchema::new)
            .collect(Collectors.toUnmodifiableList());
    }


    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("rpc", rpc),
            Map.entry("methods",
                methods.stream().map(MethodSchema::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("errors",
                errors.stream().map(ErrorSchema::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("types",
                types.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("comments", comments)
        );
    }

    enum SchemaDef {
        ERRORS("errors"), TYPES("types"), METHOD("methods"), COMMENTS("comments");
        private static List<SchemaDef> values = Arrays.asList(values());
        private final String xmlNodeName;

        SchemaDef(String xmlNodeName) {
            this.xmlNodeName = xmlNodeName;
        }

        public static SchemaDef fromNode(Node node) {
            for (SchemaDef def : values) {
                if (def.xmlNodeName.equals(node.getNodeName())) {
                    return def;
                }
            }
            throw new NoSuchElementException();
        }
    }
}
