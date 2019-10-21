package org.aion.rpcgenerator.data;

import java.util.List;
import java.util.Map;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Element;

public class ArrayType extends Type {

    private final String nestedTypeName;
    private Type nestedType;

    ArrayType(String name, List<String> comments, String nestedType) {
        super(name, comments);
        this.nestedTypeName = nestedType;
    }

    ArrayType(Element element) {
        super(element);
        nestedTypeName = XMLUtils.valueFromAttribute(element, "nestedType");
    }

    public void setNestedType(List<Type> nestedType){
        for (Type type: nestedType){
            if (nestedTypeName.equals(type.name)){
                this.nestedType =type;
                return;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    protected Map<String, Object> toMutableMap() {
        Map<String,Object> mutableMap= super.toMutableMap();
        mutableMap.put("nestedType", nestedType.toMap());
        return mutableMap;
    }
}
