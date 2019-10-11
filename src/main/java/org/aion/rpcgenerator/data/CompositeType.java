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

public class CompositeType extends Type {

    private List<Field> fieldList = new ArrayList<>();

    CompositeType(Node node) {
        super(node);
        NodeList nodes = node.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node fieldNode = nodes.item(i);
            if (fieldNode.getNodeName().equals("field")) {
                fieldList.add(new Field(
                    XMLUtils.valueFromAttribute(fieldNode, "fieldName"),
                    XMLUtils.valueFromAttribute(fieldNode, "type"),
                    XMLUtils.valueFromAttribute(fieldNode, "required")
                ));
            }
        }
    }

    public boolean setFieldTypeDef(List<Type> types) {
        boolean result = true;
        for (Field field : fieldList) {
            result &= field.setTypeDef(types);
        }
        return result;
    }

    @Override
    protected Map<String, Object> toMutableMap() {
        Map<String, Object> superMap = super.toMutableMap();
        List<Map<String, Object>> mapList = fieldList.stream().map(Field::toMap)
            .collect(Collectors.toUnmodifiableList());
        superMap.put("field", mapList);
        return superMap;
    }

    private static class Field {

        private String fieldName;
        private String typeName;
        private String required;
        private Type type;

        private Field(String fieldName, String typeName, String required) {
            this.fieldName = fieldName;
            this.typeName = typeName;
            this.required = required;
        }

        Map<String, Object> toMap() {
            return Map.ofEntries(
                Map.entry("fieldName", fieldName),
                Map.entry("type", type.toMap()),
                Map.entry("required", required)
            );
        }

        boolean setTypeDef(List<Type> types) {
            for (var _type : types) {
                if (_type.name.equals(this.typeName)) {
                    this.type = _type;
                    return true;
                }
            }
            throw new NoSuchElementException();
        }

    }
}
