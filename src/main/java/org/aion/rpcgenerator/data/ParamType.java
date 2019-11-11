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

public class ParamType extends Type {

    private final List<Field> fieldList;

    ParamType(Element element) {
        super(element);
        NodeList nodes = element.getChildNodes();
        fieldList=new ArrayList<>();
        for (Element fieldNode : XMLUtils.elements(nodes)) {
            if (fieldNode.getNodeName().equals("field")) {
                fieldList.add(new Field(
                    Integer.parseInt(XMLUtils.valueFromAttribute(fieldNode, "index")),
                    XMLUtils.valueFromAttribute(fieldNode, "fieldName"),
                    XMLUtils.valueFromAttribute(fieldNode, "type"),
                    XMLUtils.valueFromAttribute(fieldNode, "required"),
                    XMLUtils.optionalValueFromAttribute(fieldNode, "defaultValue").orElse("")));
            }
        }
    }

    public ParamType(String name, List<String> comments,
        List<Field> fieldList) {
        super(name, comments);
        this.fieldList = fieldList;
    }

    @Override
    public Map<String, Object> toMutableMap() {
        Map<String, Object> mutableMap = super.toMutableMap();
        List<Map<String, Object>> mapList = fieldList.stream()
            .map(Field::toMap)
            .collect(Collectors.toUnmodifiableList());
        mutableMap.put("fields", mapList);
        return mutableMap;
    }

    public boolean setFieldTypeDef(List<Type> types) {
        boolean result = true;
        for (Field field : fieldList) {
            result &= field.setTypeDef(types);
        }
        return result;
    }

    public static class Field implements Mappable {

        private final Integer index;
        private final String fieldName;
        private final String typeName;
        private final String required;
        private Type type;
        private final String defaultValue;

        public Field(Integer index, String fieldName, String typeName, String required,
            String defaultValue) {
            if (required.equalsIgnoreCase("true") && !defaultValue.isEmpty())
                throw new IllegalStateException("Cannot give a default value to a required field");
            else {
                this.index = index;
                this.fieldName = fieldName;
                this.typeName = typeName;
                this.required = required;
                this.defaultValue = defaultValue;
            }
        }

        public Map<String, Object> toMap() {
            if (defaultValue.isEmpty()) {
                return Map.ofEntries(
                    Map.entry("fieldName", fieldName),
                    Map.entry("type", type.toMap()),
                    Map.entry("required", required),
                    Map.entry("index", index.toString()));
            } else {
                return Map.ofEntries(
                    Map.entry("fieldName", fieldName),
                    Map.entry("type", type.toMap()),
                    Map.entry("required", required),
                    Map.entry("index", index.toString()),
                    Map.entry("defaultValue", defaultValue)
                );
            }
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
