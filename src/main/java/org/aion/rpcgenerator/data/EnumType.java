package org.aion.rpcgenerator.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EnumType extends Type {

    private List<EnumValues> enumValues = new ArrayList<>();
    private String internalTypeName;
    private Type internalType;
    public EnumType(String name, List<String> comments,
        List<EnumValues> enumValues, String internalTypeName) {
        super(name, comments);
        this.enumValues = enumValues;
        this.internalTypeName = internalTypeName;
    }

    EnumType(Element node) {
        super(node);
        NodeList nodeList = node.getChildNodes();
        internalTypeName=XMLUtils.valueFromAttribute(node, "internalType");
        for (Element childNode : XMLUtils.elements(nodeList)) {
            if (childNode.getNodeName().equals("value")) {
                enumValues.add(new EnumValues(
                    XMLUtils.valueFromAttribute(childNode, "name"),
                    XMLUtils.valueFromAttribute(childNode, "var")
                ));
            }
        }
    }

    public boolean setEnumTypes(List<Type> types) {
        for (Type type: types) {
            if (type.name.equals(internalTypeName)){
                internalType=type;
                return true;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public Map<String, Object> toMutableMap() {
        Map<String, Object> superMap = super.toMutableMap();
        superMap.put("values", enumValues.stream()
            .map(EnumValues::toMap)
            .collect(Collectors.toUnmodifiableList()));
        superMap.put("internalType", internalType.toMap());
        return superMap;
    }

    public static class EnumValues implements Mappable {

        private final String name;
        private final String value;

        public EnumValues(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public Map<String, Object> toMap() {
            return Map.ofEntries(
                Map.entry("name", name),
                Map.entry("value", value)
            );
        }
    }
}
