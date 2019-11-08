package org.aion.rpcgenerator.util;

import java.util.List;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.data.ArrayType;
import org.aion.rpcgenerator.data.CompositeType;
import org.aion.rpcgenerator.data.ConstrainedType;
import org.aion.rpcgenerator.data.EnumType;
import org.aion.rpcgenerator.data.ParamType;
import org.aion.rpcgenerator.data.Type;
import org.aion.rpcgenerator.data.UnionType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SchemaUtils {

    private SchemaUtils(){
        throw new UnsupportedOperationException("Cannot instantiate " + getClass().getSimpleName());
    }

    public static List<String> getComments(NodeList nodeList){
        return XMLUtils.elements(nodeList).stream()
            .filter(element -> element.getNodeName().equals("comment"))
            .map(Element::getTextContent)
            .collect(Collectors.toUnmodifiableList());
    }

    public static List<String> getErrors(NodeList nodeList){
        return XMLUtils.elements(nodeList).stream()
            .filter(element -> element.getNodeName().equals("error"))
            .map(e-> e.getAttribute("value"))
            .collect(Collectors.toUnmodifiableList());
    }

    public static void initializeTypes(List<Type> types) {
        for (Type type : types) {
            if (type instanceof CompositeType) {
                ((CompositeType) type).setFieldTypeDef(types);
            } else if (type instanceof ConstrainedType) {
                ((ConstrainedType) type).setBaseTypeDef(types);
            } else if (type instanceof EnumType) {
                ((EnumType) type).setEnumTypes(types);
            } else if (type instanceof ParamType) {
                ((ParamType) type).setFieldTypeDef(types);
            } else if (type instanceof ArrayType){
                ((ArrayType) type).setNestedType(types);
            }
            else if (type instanceof UnionType){
                ((UnionType) type).setTypes(types);
            }
        }
    }
}
