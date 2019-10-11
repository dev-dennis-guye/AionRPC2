package org.aion.rpcgenerator.rpc;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.data.Type;
import org.aion.rpcgenerator.util.SchemaUtils;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Element;

public class MethodSchema implements Mappable {

    private final String name;
    private final String paramName;
    private final String returnName;
    private Type paramType;
    private Type returnType;
    private List<String> comments;

    MethodSchema(String name, String paramName, String returnName,
        List<String> comments) {
        this.name = name;
        this.paramName = paramName;
        this.returnName = returnName;
        this.comments = comments;
    }

    public MethodSchema(Element node) {
        this(
            XMLUtils.valueFromAttribute(node, "name"),
            XMLUtils.valueFromAttribute(node, "returnType"),
            XMLUtils.valueFromAttribute(node, "param"),
            SchemaUtils.getComments(node.getChildNodes())
            );
    }

    public void setParamType(List<Type> types) {
        for (Type type : types) {
            if (type.name.equals(paramName)) {
                paramType = type;
                return;
            }
        }
        throw new NoSuchElementException();
    }


    public void setReturnType(List<Type> types) {
        for (Type type : types) {
            if (type.name.equals(returnName)) {
                returnType = type;
                return;
            }
        }
        throw new NoSuchElementException();
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("name", name),
            Map.entry("param", paramType.toMap()),
            Map.entry("returnType", returnType.toMap()),
            Map.entry("comments", comments)
        );
    }
}
