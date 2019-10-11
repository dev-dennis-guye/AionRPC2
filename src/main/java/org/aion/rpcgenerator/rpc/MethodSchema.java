package org.aion.rpcgenerator.rpc;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.aion.rpcgenerator.data.Type;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Node;

public class MethodSchema {

    private final String name;
    private final String paramName;
    private final String returnName;
    private Type paramType;
    private Type returnType;

    private MethodSchema(String name, String paramName, String returnName) {
        this.name = name;
        this.paramName = paramName;
        this.returnName = returnName;
    }

    public MethodSchema(Node node){
        this(
            XMLUtils.valueFromAttribute(node, "name"),
            XMLUtils.valueFromAttribute(node, "returnType"),
            XMLUtils.valueFromAttribute(node, "param")
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

    public Map<String, Object> toMap(){
        return Map.ofEntries(
            Map.entry("name", name),
            Map.entry("param", paramType.toMap()),
            Map.entry("returnType", returnType.toMap())
        );
    }
}
