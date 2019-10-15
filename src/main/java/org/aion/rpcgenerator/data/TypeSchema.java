package org.aion.rpcgenerator.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TypeSchema implements Mappable {

    private List<Type> compositeTypes;
    private List<Type> constrainedTypes;
    private List<Type> enumTypes;
    private List<Type> paramTypes;
    private List<Type> primitiveTypes;

    public TypeSchema(Document document) {
        Element root = document.getDocumentElement();
        compositeTypes = readTypes(root, "composite", "type-composite");
        constrainedTypes = readTypes(root, "constrained", "type-constrained");
        enumTypes = readTypes(root, "enum", "type-enum");
        paramTypes = readTypes(root, "param", "type-params-wrapper");
        primitiveTypes = readTypes(root, "primitives", "type-primitive");
    }

    private List<Type> readTypes(final Element root, final String tagName, final String typeName) {
        return XMLUtils.elements(root.getElementsByTagName(tagName)).stream()
            .flatMap(element -> XMLUtils.elements(element.getElementsByTagName(typeName)).stream())
            .map(Type::fromNode).collect(
                Collectors.toList());
    }


    @Override
    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("composite",
                compositeTypes.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("constrained", constrainedTypes.stream().map(Type::toMap)
                .collect(Collectors.toUnmodifiableList())),
            Map.entry("enumTypes",
                enumTypes.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("paramTypes",
                paramTypes.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("primitives",
                primitiveTypes.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList()))
        );
    }

    public List<Type> toList() {
        return Stream.of(compositeTypes, constrainedTypes, enumTypes, paramTypes, primitiveTypes)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
    }
}
