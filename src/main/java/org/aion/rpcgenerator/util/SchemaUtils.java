package org.aion.rpcgenerator.util;

import java.util.List;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.data.CompositeType;
import org.aion.rpcgenerator.data.ConstrainedType;
import org.aion.rpcgenerator.data.EnumType;
import org.aion.rpcgenerator.data.ParamType;
import org.aion.rpcgenerator.data.Type;
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

    public static void initializeNodes(List<Type> types) {
        for (Type type : types) {
            if (type instanceof CompositeType) {
                ((CompositeType) type).setFieldTypeDef(types);
            } else if (type instanceof ConstrainedType) {
                ((ConstrainedType) type).setBaseTypeDef(types);
            } else if (type instanceof EnumType) {
                ((EnumType) type).setEnumTypes(types);
            } else if (type instanceof ParamType) {
                ((ParamType) type).setFieldTypeDef(types);
            }
        }
    }
}
