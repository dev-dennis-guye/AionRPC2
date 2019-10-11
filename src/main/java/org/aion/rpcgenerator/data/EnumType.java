package org.aion.rpcgenerator.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EnumType extends Type {

    private List<EnumValues> enumValues = new ArrayList<>();

    public EnumType(String name, List<String> comments,
        List<EnumValues> enumValues) {
        super(name, comments);
        this.enumValues = enumValues;
    }

    EnumType(Element node) {
        super(node);
        NodeList nodeList = node.getChildNodes();

        for (Element childNode : XMLUtils.elements(nodeList)) {
            if (childNode.getNodeName().equals("value")) {
                enumValues.add(new EnumValues(
                    XMLUtils.valueFromAttribute(childNode, "name"),
                    XMLUtils.valueFromAttribute(childNode, "type"),
                    XMLUtils.valueFromAttribute(childNode, "var")
                ));
            }
        }
    }

    public boolean setEnumTypes(List<Type> types) {
        boolean result = true;
        for (EnumValues enumValue :
            enumValues) {
            result &= enumValue.setTypeDef(types);
        }
        return result;
    }

    @Override
    public Map<String, Object> toMutableMap() {
        Map<String, Object> superMap = super.toMutableMap();
        superMap.put("values", enumValues.stream()
            .map(EnumValues::toMap)
            .collect(Collectors.toUnmodifiableList()));
        return superMap;
    }

    public static class EnumValues implements Mappable {

        private final String name;
        private final String type;
        private final String value;
        private Type typeDef;

        public EnumValues(String name, String type, String value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        public boolean setTypeDef(List<Type> types) {
            for (Type _type : types) {
                if (_type.name.equals(this.type)) {
                    this.typeDef = _type;
                    return true;
                }
            }
            throw new NoSuchElementException();
        }

        public Map<String, Object> toMap() {
            return Map.ofEntries(
                Map.entry("name", name),
                Map.entry("value", value),
                Map.entry("type", typeDef.toMap())
            );
        }
    }
}
