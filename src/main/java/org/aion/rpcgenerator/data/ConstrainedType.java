package org.aion.rpcgenerator.data;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Element;

public class ConstrainedType extends Type {

    private final String regex;
    private final Integer min;
    private final Integer max;
    private final String baseType;
    private Type baseTypeDef = null;

    ConstrainedType(Element node) {
        super(node);
        if (XMLUtils.hasAttribute(node, "regex")) {
            regex = XMLUtils.valueFromAttribute(node, "regex");
        }
        else {
            regex=".*";
        }

        if (XMLUtils.hasAttribute(node, "min")) {
            min = Integer.parseInt(XMLUtils.valueFromAttribute(node, "min"));
        } else {
            min = -1;
        }

        if (XMLUtils.hasAttribute(node, "max")) {
            String maxStr = (XMLUtils.valueFromAttribute(node, "max"));
            max = maxStr.equalsIgnoreCase("infinity") ? Integer.MAX_VALUE : Integer.parseInt(maxStr);
        } else {
            max = Integer.MAX_VALUE;
        }
        baseType = XMLUtils.valueFromAttribute(node, "baseType");
    }

    public ConstrainedType(String name, List<String> comments, String regex, Integer min,
        Integer max, String baseType) {
        super(name, comments);
        this.regex = regex;
        this.min = min;
        this.max = max;
        this.baseType = baseType;
    }

    public boolean setBaseTypeDef(List<Type> types) {
        for (var type : types) {
            if (type.name.equals(baseType)) {
                baseTypeDef = type;
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
