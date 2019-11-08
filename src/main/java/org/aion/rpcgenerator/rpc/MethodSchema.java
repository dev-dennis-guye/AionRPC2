package org.aion.rpcgenerator.rpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import jdk.jshell.execution.Util;
import org.aion.rpcgenerator.Mappable;
import org.aion.rpcgenerator.data.Type;
import org.aion.rpcgenerator.error.ErrorSchema;
import org.aion.rpcgenerator.util.SchemaUtils;
import org.aion.rpcgenerator.util.Utils;
import org.aion.rpcgenerator.util.XMLUtils;
import org.w3c.dom.Element;

public class MethodSchema implements Mappable {

    private final String name;
    private final String paramName;
    private final String returnName;
    private Type paramType;
    private Type returnType;
    private List<String> comments;
    private final List<String> errors;
    private final List<ErrorSchema> errorSchemas;

    MethodSchema(String name, String paramName, String returnName,
        List<String> comments, List<String> errors) {
        this.name = name;
        this.paramName = paramName;
        this.returnName = returnName;
        this.comments = comments;
        this.errors = errors;
        errorSchemas = new ArrayList<>();
    }

    public MethodSchema(Element node) {
        this(
            XMLUtils.valueFromAttribute(node, "name"),
            XMLUtils.valueFromAttribute(node, "param"),
            XMLUtils.valueFromAttribute(node, "returnType"),
            SchemaUtils.getComments(node.getChildNodes()),
            XMLUtils.elementFromTag(node, "errors")
                .map(e->SchemaUtils.getErrors(e.getChildNodes()))
                .orElse(Collections.emptyList()));
    }

    public void setParamType(List<Type> types) {
        for (Type type : types) {
            if (type.name.equals(paramName)) {
                paramType = type;
                return;
            }
        }
        throw new NoSuchElementException();
    }

    public void setErrorsSchemas(List<ErrorSchema> errorsSchemas){
        if (this.errorSchemas.size() == errors.size()) return;

        for (ErrorSchema errorSchema: errorsSchemas) {
            for (int i = 0; i < errors.size(); i++) {
                String error = errors.get(i);
                if (error.equals(errorSchema.getErrorClass())){
                    this.errorSchemas.add(errorSchema);
                }
            }
        }
        if (this.errorSchemas.size() == errors.size()) return;
        throw new IllegalStateException(
            String.format("Could not find all errors for: %s", this.toMap().toString()));
    }

    public void setReturnType(List<Type> types) {
        for (Type type : types) {
            if (type.name.equals(returnName)) {
                returnType = type;
                return;
            }
        }
        throw new NoSuchElementException();
    }

    public Map<String, Object> toMap() {
        return Map.ofEntries(
            Map.entry("name", name),
            Map.entry("param", paramType.toMap()),
            Map.entry("returnType", returnType.toMap()),
            Map.entry("comments", comments),
            Map.entry("errors", Utils.toListOfMaps(errorSchemas))
        );
    }
}
