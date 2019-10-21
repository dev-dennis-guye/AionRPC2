package org.aion.rpcgenerator.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.error.ErrorSchema;
import org.aion.rpcgenerator.util.SchemaUtils;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TypeSchema implements Mappable {

    private final String decodeErrorName;
    private final String encodeErrorName;
    private List<Type> compositeTypes;
    private List<Type> constrainedTypes;
    private List<Type> enumTypes;
    private List<Type> paramTypes;
    private List<Type> primitiveTypes;
    private List<Type> arrayType;
    private ErrorSchema decodeError;
    private ErrorSchema encodeError;

    public TypeSchema(Document document) {
        Element root = document.getDocumentElement();
        compositeTypes = readTypes(root, "composite", "type-composite");
        constrainedTypes = readTypes(root, "constrained", "type-constrained");
        enumTypes = readTypes(root, "enum", "type-enum");
        paramTypes = readTypes(root, "param", "type-params-wrapper");
        primitiveTypes = readTypes(root, "primitives", "type-primitive");
        arrayType = readTypes(root, "array", "type-list");
        decodeErrorName = readErrorName(root, "decode-error");
        encodeErrorName = readErrorName(root, "encode-error");
        SchemaUtils.initializeTypes(toList());
    }

    private List<Type> readTypes(final Element root, final String tagName, final String typeName) {
        return XMLUtils.elements(root.getElementsByTagName(tagName)).stream()
            .flatMap(element -> XMLUtils.elements(element.getElementsByTagName(typeName)).stream())
            .map(Type::fromNode).collect(
                Collectors.toList());
    }

    private String readErrorName(final Element root, String tag){
        return XMLUtils.elements(root.getElementsByTagName(tag)).stream()
            .findFirst().map(e->XMLUtils.valueFromAttribute(e, "error_class"))
            .orElseThrow();
    }

    public void  setErrors(List<ErrorSchema> errors){
        boolean initializedDecode = false;
        boolean initializedEncode = false;
        for (ErrorSchema errorSchema :
            errors) {
            if (errorSchema.getErrorClass().equals(decodeErrorName)){
                decodeError = errorSchema;
                initializedDecode = true;
            }
            if (errorSchema.getErrorClass().equals(encodeErrorName)){
                encodeError = errorSchema;
                initializedEncode = true;
            }

            if (initializedDecode && initializedEncode){
                return;
            }
        }
        throw new IllegalStateException();
    }


    @Override
    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("compositeTypes",
                compositeTypes.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("constrainedTypes", constrainedTypes.stream().map(Type::toMap)
                .collect(Collectors.toUnmodifiableList())),
            Map.entry("enumTypes",
                enumTypes.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("paramTypes",
                paramTypes.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("primitivesTypes",
                primitiveTypes.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("arrayTypes",
                arrayType.stream().map(Type::toMap).collect(Collectors.toUnmodifiableList())),
            Map.entry("encodeError", encodeError.toMap()),
            Map.entry("decodeError", decodeError.toMap())
        );
    }

    public List<Type> toList() {
        return Stream.of(compositeTypes, constrainedTypes, enumTypes, paramTypes, primitiveTypes, arrayType)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
    }
}
