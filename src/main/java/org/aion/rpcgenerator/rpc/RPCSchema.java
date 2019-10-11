package org.aion.rpcgenerator.rpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.data.CompositeType;
import org.aion.rpcgenerator.data.ConstrainedType;
import org.aion.rpcgenerator.data.EnumType;
import org.aion.rpcgenerator.data.ParamType;
import org.aion.rpcgenerator.data.Type;
import org.aion.rpcgenerator.error.ErrorSchema;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RPCSchema {

    enum SchemaDef {
        ERRORS("errors"), TYPES("types"), METHOD("methods");
        private final String xmlNodeName;
        SchemaDef(String xmlNodeName) {
            this.xmlNodeName = xmlNodeName;
        }

        private static List<SchemaDef> values = Arrays.asList(values());

        public static SchemaDef fromNode(Node node){
            for (SchemaDef def: values){
                if (def.xmlNodeName.equals(node.getNodeName())){
                    return def;
                }
            }
            throw new NoSuchElementException();
        }
    }
    private final String rpc;
    private List<ErrorSchema> errors = new ArrayList<>();
    private List<Type> types = new ArrayList<>();
    private List<MethodSchema> methods = new ArrayList<>();

    public RPCSchema(Document rpcScheme, List<ErrorSchema> errorsSchemas){
        Element root =rpcScheme.getDocumentElement();
        rpc = root.getNodeName();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            SchemaDef def = SchemaDef.fromNode(nodeList.item(i));
            switch (def){
                case ERRORS:
                    errors = getErrors(nodeList.item(i).getChildNodes(),errorsSchemas);
                    break;
                case TYPES:
                    types = getTypes(nodeList.item(i).getChildNodes());
                    break;
                case METHOD:
                    methods = getMethodSchemas(nodeList.item(i).getChildNodes());
                    break;
            }
        }

        for (Type type: types){
            if (type instanceof CompositeType){
                ((CompositeType) type).setFieldTypeDef(types);
            }
            else if (type instanceof ConstrainedType){
                ((ConstrainedType) type).setBaseTypeDef(types);
            }
            else if (type instanceof EnumType){
                ((EnumType) type).setEnumTypes(types);
            }
            else if (type instanceof ParamType){
                ((ParamType) type).setFieldTypeDef(types);
            }
        }

        for (MethodSchema methodSchema: methods){
            methodSchema.setParamType(types);
            methodSchema.setReturnType(types);
        }
    }

    public Map<String, Object> toMap(){
        return Map.ofEntries(
            Map.entry("rpc", rpc),
            Map.entry("methods", methods.stream().map(MethodSchema::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("errors", errors.stream().map(ErrorSchema::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("types", types.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList()))
        );
    }
    private static List<ErrorSchema> getErrors(NodeList nodeList, List<ErrorSchema> errorSchemas){
        List<String> errorNames = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            errorNames.add(XMLUtils.valueFromAttribute(nodeList.item(i), "error_class"));
        }
        return errorSchemas.stream()
            .filter(e-> errorNames.contains(e.getErrorClass()))
            .collect(Collectors.toUnmodifiableList());
    }

    private static List<Type> getTypes(NodeList nodeList){
        List<Type> types = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            types.add(Type.fromNode(nodeList.item(i)));
        }
        return Collections.unmodifiableList(types);
    }

    private static List<MethodSchema> getMethodSchemas(NodeList nodeList){
        List<MethodSchema> methodSchemas = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            methodSchemas.add(new MethodSchema(nodeList.item(i)));
        }
        return Collections.unmodifiableList(methodSchemas);
    }
}
