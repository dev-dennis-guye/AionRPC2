package org.aion.rpcgenerator.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EnumType extends Type {

    private List<EnumValues> enumValues = new ArrayList<>();

    EnumType(Node node) {
        super(node);
        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeName().equals("value")) {
                enumValues.add(new EnumValues(
                    XMLUtils.valueFromAttribute(childNode, "name"),
                    XMLUtils.valueFromAttribute(childNode, "type"),
                    XMLUtils.valueFromAttribute(childNode, "var")
                ));
            }
        }
    }
    public boolean setEnumTypes(List<Type> types){
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

    private static class EnumValues {

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
