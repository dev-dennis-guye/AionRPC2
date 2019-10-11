package org.aion.rpcgenerator.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Node;

public class ConstrainedType extends Type {
    private final String regex;
    private final Integer min;
    private final Integer max;
    private final String baseType;
    private Type baseTypeDef=null;

    ConstrainedType(Node node){
        super(node);
        regex = XMLUtils.valueFromAttribute(node, "regex");
        if (XMLUtils.hasAttribute(node, "min")) {
            min=Integer.MAX_VALUE;
        }else {
            min=Integer.parseInt(XMLUtils.valueFromAttribute(node, "min"));
        }

        if (XMLUtils.hasAttribute(node, "min")) {
            max=Integer.MAX_VALUE;
        }else {
            String maxStr = (XMLUtils.valueFromAttribute(node, "min"));
            max= maxStr.equalsIgnoreCase("infinity") ? Integer.MAX_VALUE : Integer.parseInt(maxStr);
        }
        baseType=XMLUtils.valueFromAttribute(node, "baseType");
    }

    public boolean setBaseTypeDef(List<Type> types) {
        for (var type: types){
            if (type.name.equals(baseType)){
                baseTypeDef= type;
                return true;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public Map<String, Object> toMutableMap() {
        Map<String, Object> mutableMap = super.toMutableMap();
        mutableMap.put("regex", regex);
        mutableMap.put("min", min.toString());
        mutableMap.put("max", max.toString());
        mutableMap.put("baseType", baseTypeDef.toMap());
        return mutableMap;
    }
}
