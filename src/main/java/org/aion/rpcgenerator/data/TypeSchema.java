package org.aion.rpcgenerator.data;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.error.ErrorSchema;
import org.aion.rpcgenerator.util.SchemaUtils;
import org.aion.rpcgenerator.util.Utils;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TypeSchema implements Mappable {

    private final String decodeErrorName;
    private final String encodeErrorName;
    private List<Type> compositeTypes;
    private List<Type> constrainedTypes;
    private List<Type> enumTypes;
    private List<Type> paramTypes;
    private List<Type> primitiveTypes;
    private List<Type> arrayType;
    private List<Type> unionType;
    private ErrorSchema decodeError;
    private ErrorSchema encodeError;
    private List<Pattern> patterns;

    public TypeSchema(Document document) {
        Element root = document.getDocumentElement();
        compositeTypes = readTypes(root, "composite", "type-composite");
        constrainedTypes = readTypes(root, "constrained", "type-constrained");
        enumTypes = readTypes(root, "enum", "type-enum");
        paramTypes = readTypes(root, "param", "type-params-wrapper");
        primitiveTypes = readTypes(root, "primitives", "type-primitive");
        arrayType = readTypes(root, "array", "type-list");
        unionType = readTypes(root, "union","type-union");
        decodeErrorName = readErrorName(root, "decode-error");
        encodeErrorName = readErrorName(root, "encode-error");
        patterns = readPatterns(root);
        SchemaUtils.initializeTypes(toList());
    }

    private List<Pattern> readPatterns(final Element root){
        Element regexRoot = (Element) root.getElementsByTagName("regexes").item(0);
        return XMLUtils.elements(regexRoot.getChildNodes()).stream().map(Pattern::new).collect(
            Collectors.toUnmodifiableList());
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
            Map.entry("date", ZonedDateTime.now().toLocalDate()),
            Map.entry("compositeTypes", Utils.toListOfMaps(compositeTypes)),
            Map.entry("constrainedTypes", Utils.toListOfMaps(constrainedTypes)),
            Map.entry("enumTypes", Utils.toListOfMaps(enumTypes)),
            Map.entry("paramTypes", Utils.toListOfMaps(paramTypes)),
            Map.entry("primitivesTypes", Utils.toListOfMaps(primitiveTypes)),
            Map.entry("arrayTypes", Utils.toListOfMaps(arrayType)),
            Map.entry("unionTypes", Utils.toListOfMaps(unionType)),
            Map.entry("encodeError", encodeError.toMap()),
            Map.entry("decodeError", decodeError.toMap()),
            Map.entry("patterns", Utils.toListOfMaps(patterns))
        );
    }

    public List<Type> toList() {
        return Stream.of(compositeTypes, constrainedTypes, enumTypes, paramTypes, primitiveTypes, arrayType, unionType)
            .flatMap(Collection::stream)
            .collect(Collectors.toUnmodifiableList());
    }
}
